# Database Schema

Tables are designed with UUID IDs and epoch millis timestamps.

```sql

CREATE TABLE IF NOT EXISTS short_links (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  tenant_id VARCHAR(100) NOT NULL,
  short_code VARCHAR(100) NOT NULL,
  original_url TEXT NOT NULL,
  domain VARCHAR(255) NOT NULL,
  created_at BIGINT NOT NULL,
  expires_at BIGINT NULL,
  CONSTRAINT uq_tenant_code UNIQUE (tenant_id, short_code)
);

CREATE INDEX idx_short_links_code ON short_links (short_code);
CREATE INDEX idx_short_links_tenant_url ON short_links (tenant_id, original_url);
CREATE INDEX idx_short_links_expiry ON short_links (expires_at);

CREATE TABLE dlq (
    id UUID PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    reason TEXT,
    domain VARCHAR(255),
    custom_code VARCHAR(255),
    tenant_id VARCHAR(255),
    created_at BIGINT NOT NULL    -- epoch millis
);
```
