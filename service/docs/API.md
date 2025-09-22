# API Usage

## Endpoints

- **POST** `/shorten` → create or fetch short link
- **GET** `/s/{shortCode}` → 301 redirect
- **GET** `/links?page={page}&size={size}` → list all short links (paginated)

---

### Examples

#### **POST** `/shorten`

**Request**

```json
{
  "originalUrl": "https://example.com/articles/123",
  "customCode": "mycode123"
}
```

**Response**

```json
{ "shortUrl": "http://localhost:8080/s/mycode123" }
```

---

#### **GET** `/s/{shortCode}`

```bash
curl -i http://localhost:8080/s/mycode123
```

---

#### **GET** `/links`

```bash
curl "http://localhost:8080/links?page=0&size=5"
```

**Response**

```json
{
  "content": [
    {
      "id": "48a8353c-f1a0-4535-9c8f-eea221192639",
      "originalUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
      "tenantId": "defaultTenant",
      "domain": "http://localhost:8080",
      "shortCode": "b7c07abb",
      "createdAt": 1758520241136,
      "expiresAt": 1761112241136
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true
}
```
