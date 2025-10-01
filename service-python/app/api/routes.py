import time

from fastapi import APIRouter, Depends, HTTPException, Query, Request
from fastapi.responses import RedirectResponse
from sqlalchemy.orm import Session

from app.api.schemas import ShortLinkRequest, ShortLinkResponse, PageResponse
from app.db.database import get_db, Base, engine
from app.services.shortlink_service import (
    create_or_get_short_code,
    get_by_short_code,
    list_links,
    get_by_short_code_from_db,
)

router = APIRouter()

# Ensure schema exists (dev/test)
Base.metadata.create_all(bind=engine)


@router.post("/shorten", response_model=ShortLinkResponse)
def create_short_link(
    payload: ShortLinkRequest,
    request: Request,
    db: Session = Depends(get_db),
):
    tenant_id = request.headers.get("x-tenant-id", "defaultTenant")
    domain = payload.domain if hasattr(payload, "domain") and payload.domain else str(request.base_url).rstrip("/")

    try:
        code, entity = create_or_get_short_code(
            db=db,
            original_url=str(payload.originalUrl),
            custom_code=payload.customCode,
            tenant_id=tenant_id,
            domain=domain,
            expires_at=payload.expiresAt,
        )
    except ValueError as ve:
        raise HTTPException(status_code=400, detail=str(ve))

    short_url = f"{domain}/s/{code}"
    return ShortLinkResponse(
        shortUrl=short_url,
        originalUrl=entity.original_url,
        createdAt=entity.created_at,
        expiresAt=entity.expires_at,
    )


@router.get("/s/{shortCode}")
def redirect(shortCode: str, request: Request, db: Session = Depends(get_db)):
    tenant_id = request.headers.get("x-tenant-id", "defaultTenant")
    link = get_by_short_code(db, shortCode, tenant_id)

    if not link:
        raise HTTPException(status_code=404, detail="Short code not found or expired")

    # Expiry check
    now_ms = int(time.time() * 1000)
    if link.expires_at and now_ms > int(link.expires_at):
            raise HTTPException(status_code=404, detail="Short code expired")

    return RedirectResponse(link.original_url, status_code=301)


@router.get("/links", response_model=PageResponse)
def get_links(
    request: Request,
    page: int = Query(0, ge=0),
    size: int = Query(5, ge=1, le=100),
    db: Session = Depends(get_db),
):
    tenant_id = request.headers.get("x-tenant-id", "defaultTenant")
    rows, total = list_links(db, tenant_id, page, size)

    items = [
        ShortLinkResponse(
            shortUrl=f"{row.domain}/s/{row.short_code}",
            originalUrl=row.original_url,
            createdAt=row.created_at,
            expiresAt=row.expires_at,
            tenantId=row.tenant_id,
            domain=row.domain,
            shortCode=row.short_code,
        )
        for row in rows
    ]

    return PageResponse(items=items, page=page, size=size, total=total)


@router.delete("/shortlinks/{shortCode}", status_code=204)
def delete_short_link(request: Request, shortCode: str, db: Session = Depends(get_db)):
    tenant_id = request.headers.get("x-tenant-id", "defaultTenant")
    link = get_by_short_code_from_db(db, shortCode, tenant_id)
    if not link:
        raise HTTPException(status_code=404, detail="Short code not found")

    db.delete(link)
    db.commit()
    return None
