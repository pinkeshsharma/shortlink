import time
from sqlalchemy import func
from sqlalchemy.orm import Session
from sqlalchemy import select
from app.db.models import ShortLink
from app.db.redis_client import redis_client


# Default expiry: 30 days in ms
DEFAULT_EXPIRY_MS = int(timedelta(days=30).total_seconds() * 1000)

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
        created_at=int(time.time() * 1000),
        expires_at=expiry,
    )
    db.add(link)
    db.commit()
    db.refresh(link)

    # Cache it
    redis_client.setex(f"{tenant_id}:{short_code}", 3600, original_url)

    return short_code, link


def get_by_short_code(db: Session, code: str, tenant_id: str) -> ShortLink | None:
    # 1. Check Redis
    cached_url = redis_client.get(f"{tenant_id}:{code}")
    if cached_url:
        return ShortLink(
            original_url=cached_url,
            tenant_id=tenant_id,
            short_code=code,
            domain="",
            created_at=int(time.time() * 1000),
            expires_at=None,
        )

    # 2. Fallback DB
    link = db.execute(
        select(ShortLink).where(
            ShortLink.short_code == code,
            ShortLink.tenant_id == tenant_id,
        )
    ).scalars().first()

    if not link or link.is_expired:
        return None

    # 3. Cache result
    redis_client.setex(f"{tenant_id}:{code}", 3600, link.original_url)
    return link


def list_links(db: Session, tenant_id: str, page: int, size: int):
    # Fetch paged rows
    stmt = (
        select(ShortLink)
        .where(ShortLink.tenant_id == tenant_id)
        .offset(page * size)
        .limit(size)
    )
    rows = db.execute(stmt).scalars().all()

    # Count total
    total = db.execute(
        select(func.count()).select_from(ShortLink).where(ShortLink.tenant_id == tenant_id)
    ).scalar_one()

    return rows, total
