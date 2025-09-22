# Running & Testing

## Run with Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

Services:
- API → `http://localhost:8080`
- Postgres → `localhost:5432`
- Redis → `localhost:6379`

## Run locally

```bash
./mvnw clean spring-boot:run
```

## Testing

```bash
./mvnw test
```
