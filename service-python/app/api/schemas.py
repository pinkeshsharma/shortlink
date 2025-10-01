from pydantic import BaseModel, HttpUrl, field_validator
from typing import Optional, List
import re

CODE_REGEX = r"^[a-zA-Z0-9_-]{4,20}$"

class ShortLinkRequest(BaseModel):
    originalUrl: HttpUrl
    customCode: Optional[str] = None
    expiresAt: Optional[int] = None
    domain: Optional[str] = None

    @field_validator("customCode")
    @classmethod
    def validate_code(cls, v: Optional[str]) -> Optional[str]:
        if v is None:
            return v
        if not re.fullmatch(CODE_REGEX, v):
            raise ValueError("customCode must match ^[a-zA-Z0-9_-]{4,20}$")
        return v


class ShortLinkResponse(BaseModel):
    shortUrl: HttpUrl
    originalUrl: Optional[HttpUrl] = None
    createdAt: Optional[int] = None
    expiresAt: Optional[int] = None
    tenantId: Optional[str] = None
    domain: Optional[str] = None
    shortCode: Optional[str] = None


class PageResponse(BaseModel):
    items: List[ShortLinkResponse]
    page: int
    size: int
    total: int