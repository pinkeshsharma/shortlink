# Shortlink Full Stack (Service + UI + DB + Redis)

This project runs the complete shortlink system:
- **Postgres** (DB)
- **Redis** (cache)
- **Spring Boot Service** (backend API)
- **React App** (frontend UI)

## How to Run

From the project root (where this `docker-compose.yml` lives):

```sh
docker-compose build
docker-compose up
```

This will start all services:
- UI → [http://localhost:3000](http://localhost:3000)  
- Backend API → [http://localhost:8080](http://localhost:8080)  
- Postgres DB → `localhost:5432` (user: `postgres`, pass: `postgres`, db: `shortenerdb`)  
- Redis → `localhost:6379`

## Stop

```sh
docker-compose down
```

## Clean volumes (optional)

```sh
docker-compose down -v
```

This removes database and redis data.
