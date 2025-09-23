# Stage 1: Build React app
FROM node:18 AS build

WORKDIR /app

# Copy dependency files
COPY package.json package-lock.json ./

# Install deps
RUN npm install

# Copy the rest of the app
COPY . .

# Build production-ready static files
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:1.25-alpine

# Remove default Nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy React build to Nginx folder
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
