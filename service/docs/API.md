# API Usage

## Endpoints

- **POST** `/api/v1/shorten` → create or fetch short link  
- **GET** `/s/{shortCode}` → 301 redirect

### Example

**POST**

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

**GET**

```bash
curl -i http://localhost:8080/s/mycode123
```
