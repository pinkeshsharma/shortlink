import uuid
from sqlalchemy import String, Integer, BigInteger, DateTime, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column
from app.db.database import Base
from datetime import datetime, timezone

class ShortLink(Base):
    __tablename__ = "short_links"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    original_url: Mapped[str] = mapped_column(String(2048), nullable=False)
    tenant_id: Mapped[str] = mapped_column(String(255), nullable=False)
    domain: Mapped[str] = mapped_column(String(255), nullable=False)
    short_code: Mapped[str] = mapped_column(String(64), nullable=False, unique=True, index=True)
    created_at: Mapped[int] = mapped_column(BigInteger, nullable=False, default=lambda: int(datetime.now(tz=timezone.utc).timestamp() * 1000))
    expires_at: Mapped[int] = mapped_column(BigInteger, nullable=True)

    @property
    def is_expired(self) -> bool:
        return self.expires_at is not None and self.expires_at > 0 and self.expires_at < int(datetime.now(tz=timezone.utc).timestamp() * 1000)


class DeadLetter(Base):
    __tablename__ = "dlq"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    original_url: Mapped[str] = mapped_column(String(2048), nullable=True)
    reason: Mapped[str] = mapped_column(String(512), nullable=True)
    domain: Mapped[str] = mapped_column(String(255), nullable=True)
    custom_code: Mapped[str] = mapped_column(String(64), nullable=True)
    tenant_id: Mapped[str] = mapped_column(String(255), nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
