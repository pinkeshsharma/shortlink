# Design Decisions & Tradeoffs

- **Schema**: single table, `(tenant_id, short_code)` composite uniqueness.  
- **Timestamps**: stored as epoch millis → no timezone issues.  
- **Cache**: Redis used for hot lookups, TTL = 30 minutes.  
- **Idempotency**: UPSERT via DB uniqueness.  
- **DLQ**: invalid URLs are logged for later inspection.  
- **Short code strategy**: custom if given, otherwise UUID substring.  
