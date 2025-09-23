# URL Shortener – Main README

This project implements a **multi-tenant URL shortener** using:

- **Postgres** (DB)
- **Redis** (cache)
- **Backend API**
    - Java → Spring Boot (`shortener-service`)
    - Python → FastAPI (`shortener-python`)
- **React App** (frontend UI)

The system supports running either **Java** or **Python** backend, selectable via Docker profiles.

---

## How to Run

From the project root (where this `docker-compose.yml` lives):

### Run with **Java backend**
```sh
docker compose --profile java up --build
```

### Run with **Python backend**
```sh
docker compose --profile python up --build
```

---

## Services

- **UI**
    - [http://localhost:3000](http://localhost:3000)
    - Proxies API requests via `/api` to the active backend (Java or Python).

- **Backend API**
    - Java: [http://localhost:8080](http://localhost:8080)
    - Python: [http://localhost:8000](http://localhost:8000)

- **Postgres DB**
    - `localhost:5432`
    - User: `postgres`
    - Password: `postgres`
    - Database: `shortenerdb`

- **Redis**
    - `localhost:6379`

---

## Stop Services

```sh
docker compose down
```

---

## Clean Volumes (optional)

```sh
docker compose down -v
```

This removes **Postgres** and **Redis** data.

---

## Notes

- Nginx configs are provided for both profiles:
    - `nginx-java.conf` → proxies to Java backend
    - `nginx-python.conf` → proxies to Python backend
- The UI automatically switches backend depending on the profile used at startup.

---

## 👤 Author
Developed by **Pinkesh Sharma**  
[GitHub Profile](https://github.com/pinkeshsharma)
