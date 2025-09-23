# ShortLink UI

This is the **React frontend** for the ShortLink application.  
It provides a minimal interface to:

- Create short links
- View a paginated list of all short links

The UI connects to the backend service through **Nginx `/api` proxy**, which can be configured to point to:

- **Java backend** (`shortener-service`) → [http://localhost:8080](http://localhost:8080)
- **Python backend** (`shortener-python`) → [http://localhost:8000](http://localhost:8000)

---

## Prerequisites

- [Node.js 18+](https://nodejs.org/) (if running locally)
- [Docker](https://www.docker.com/products/docker-desktop)

---

## Run Locally (Dev Mode)

1. Install dependencies:
   ```powershell
   npm install
