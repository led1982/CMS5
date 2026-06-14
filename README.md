# CMS5

Internal CMS and employee portal MVP scaffold.

## Structure

- `frontend/`: React/Vite portal and admin console.
- `backend/`: Spring Boot REST API with local mock RBAC and OpenAPI resource.
- `database/`: PostgreSQL migrations and local seed data.
- `docs/operations/`: deployment, backup, recovery, and smoke validation notes.

## Branch Preview

```bash
docker compose up --build
```

Then open `http://localhost:5173`. The backend API is published on `http://localhost:8080`.

Use the role selector in the preview header to switch between portal and admin scenarios. The backend accepts matching `X-CMS-User` and `X-CMS-Roles` headers for local smoke calls.
