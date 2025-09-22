# ShortLink UI

This is the **React frontend** for the ShortLink application.  
It provides a minimal interface to:

- Create short links
- View a paginated list of all short links

The UI connects to the backend service (`shortener-service`) running on [http://localhost:8080](http://localhost:8080).

---

## Prerequisites

- [Node.js 18+](https://nodejs.org/) (if running locally)
- [Docker](https://www.docker.com/products/docker-desktop)

---

## Run Locally (Dev Mode)

1. Install dependencies:
   ```powershell
   npm install
   ```

2. Start the dev server:
   ```powershell
   npm start
   ```

3. Open in browser:  
   [http://localhost:3000](http://localhost:3000)

The UI will proxy API requests to [http://localhost:8080](http://localhost:8080).

---

## Run via Docker

1. Build the image:
   ```powershell
   docker build -t shortlink-ui .
   ```

2. Run the container:
   ```powershell
   docker run -d -p 3000:80 --name shortlink-ui shortlink-ui
   ```

3. Open in browser:  
   [http://localhost:3000](http://localhost:3000)

---

## Dockerfile

Place this `Dockerfile` in the root of the `shortlink-ui` folder:

```dockerfile
# Stage 1: Build React app
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

## Features

- **Create Link Form**
    - Enter original URL + optional custom code
    - Returns a generated short link

- **List Page**
    - Paginated table of all short links
    - Columns: `id`, `originalUrl`, `tenantId`, `domain`, `shortCode`, `createdAt`, `expiresAt`
    - Long URLs are truncated for readability

---

## Next Steps

- Add **search/filtering** for links
- Add **copy-to-clipboard** button for short URLs
- Add **auth support** (multi-tenant mode)
- Add **dark/light theme toggle**
