# CMS5

Greenfield internal CMS and employee portal MVP.

## Stack

- Frontend: React, TypeScript, Vite
- Backend: Spring Boot REST API, Java 21
- Database: PostgreSQL with Flyway migrations

## Run With Docker Compose

```bash
docker compose -f infra/docker-compose.yml up --build
```

Open http://localhost:3000 for the portal. The backend API is published at http://localhost:8080/api/v1.

Local seed users are available from the user selector in the top bar: employee, outside employee, content manager, reviewer, and administrator.
