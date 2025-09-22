DROP TABLE IF EXISTS short_links CASCADE;
DROP TABLE IF EXISTS dlq CASCADE;

-- short_links table with UUID id and epoch millis
CREATE TABLE short_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(100) NOT NULL,
    short_code VARCHAR(100) NOT NULL,
    original_url TEXT NOT NULL,
    domain VARCHAR(255) NOT NULL,
    created_at BIGINT NOT NULL,   -- epoch millis
    expires_at BIGINT NULL,       -- epoch millis, nullable for permanent links
    CONSTRAINT uq_tenant_code UNIQUE (tenant_id, short_code)
);

CREATE INDEX idx_short_links_code ON short_links (short_code);
CREATE INDEX idx_short_links_tenant_url ON short_links (tenant_id, original_url);
CREATE INDEX idx_short_links_expiry ON short_links (expires_at);

-- DLQ table with UUID id and epoch millis
CREATE TABLE dlq (
    id UUID PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    reason TEXT,
    domain VARCHAR(255),
    custom_code VARCHAR(255),
    tenant_id VARCHAR(255),
    created_at BIGINT NOT NULL    -- epoch millis
);
