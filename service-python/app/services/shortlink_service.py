import json
import time
from datetime import timedelta
from sqlalchemy import func, select
from sqlalchemy.orm import Session
from app.db.models import ShortLink
from app.db.redis_client import redis_client

# Default expiry: 30 days in ms
DEFAULT_EXPIRY_MS = int(timedelta(days=30).total_seconds() * 1000)


def make_cache_key(tenant_id: str, code: str) -> str:
    return f"{tenant_id}:{code}"

def create_or_get_short_code(
    db: Session,
    original_url: str,
    custom_code: str | None,
    tenant_id: str,
    domain: str,
    expires_at: int | None = None,
):
    # Check if the same URL already exists for tenant
    existing = db.execute(
        select(ShortLink).where(
            ShortLink.original_url == original_url,
            ShortLink.tenant_id == tenant_id,
        )
    ).scalars().first()
    if existing:
        return existing.short_code, existing

    # If custom code is requested, check conflicts
    if custom_code:
        conflict = db.execute(
            select(ShortLink).where(
                ShortLink.short_code == custom_code,
                ShortLink.tenant_id == tenant_id,
            )
        ).scalars().first()
        if conflict:
            raise ValueError("Custom code already exists")
        short_code = custom_code
    else:
        # Auto-generate code
        short_code = str(int(time.time() * 1000))[-6:]

    now_ms = int(time.time() * 1000)
    expiry = expires_at if expires_at else now_ms + DEFAULT_EXPIRY_MS

    # Create new entity
    link = ShortLink(
        original_url=original_url,
        tenant_id=tenant_id,
        domain=domain,
        short_code=short_code,
        created_at=now_ms,
        expires_at=expiry,
    )
    db.add(link)
    db.commit()
    db.refresh(link)

    # Cache full entity
    link_data = {
        "original_url": link.original_url,
        "tenant_id": link.tenant_id,
        "domain": link.domain,
        "short_code": link.short_code,
        "created_at": link.created_at,
        "expires_at": link.expires_at,
    }
    redis_client.setex(make_cache_key(tenant_id, short_code), 3600, json.dumps(link_data))

    return short_code, link


def get_by_short_code(db: Session, code: str, tenant_id: str) -> ShortLink | None:
    # 1. Check Redis
    cached = redis_client.get(make_cache_key(tenant_id, code))
    if cached:
        data = json.loads(cached)
        link = ShortLink(
            original_url=data["original_url"],
            tenant_id=data["tenant_id"],
            short_code=data["short_code"],
            domain=data["domain"],
            created_at=data["created_at"],
            expires_at=data["expires_at"],
        )
        # check expiry for cached item
        now_ms = int(time.time() * 1000)
        if link.expires_at and now_ms > int(link.expires_at):
            return None
        return link

    # 2. Fallback DB
    link = (
        db.query(ShortLink)
        .filter(ShortLink.short_code == code, ShortLink.tenant_id == tenant_id)
        .first()
    )
    if not link or link.is_expired:
        return None

    # 3. Cache for next time
    link_data = {
        "original_url": link.original_url,
        "tenant_id": link.tenant_id,
        "short_code": link.short_code,
        "domain": link.domain,
        "created_at": link.created_at,
        "expires_at": link.expires_at,
    }
    redis_client.setex(make_cache_key(tenant_id, code), 3600, json.dumps(link_data))
    return link


def list_links(db: Session, tenant_id: str, page: int, size: int):
    stmt = (
        select(ShortLink)
        .where(ShortLink.tenant_id == tenant_id)
        .offset(page * size)
        .limit(size)
    )
    rows = db.execute(stmt).scalars().all()

    total = db.execute(
        select(func.count()).select_from(ShortLink).where(ShortLink.tenant_id == tenant_id)
    ).scalar_one()

    return rows, total


def get_by_short_code_from_db(db: Session, code: str, tenant_id: str) -> ShortLink | None:
    link = (
        db.query(ShortLink)
        .filter(ShortLink.short_code == code, ShortLink.tenant_id == tenant_id)
        .first()
    )
    if not link or link.is_expired:
        return None

    # Clean up Redis cache if entry exists
    redis_client.delete(make_cache_key(tenant_id, code))
    return link
