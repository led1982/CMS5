# CMS5

Internal knowledge, document, and announcement CMS MVP.

## Structure

- `backend/`: Spring Boot REST API, JPA domain model, Flyway PostgreSQL migrations.
- `frontend/`: React portal, CMS, and admin SPA.
- `docker-compose.yml`: Branch preview stack with PostgreSQL, backend, and frontend.

## Local Preview

```bash
docker compose up --build
```

The frontend is published at `http://localhost:8088` and proxies API calls to the backend.

Demo bearer tokens are simple role names for local preview: `employee`, `editor`, `reviewer`, and `admin`.
The frontend stores the selected token in local storage and sends it as `Authorization: Bearer <role>`.

PostgreSQL init scripts run only when the `postgres-data` volume is empty. Remove the volume before expecting
Flyway seed data to be recreated from scratch.

## Operations Notes

- Backend health: `GET http://localhost:8080/actuator/health` or `GET http://localhost:8080/api/v1/health`.
- Logs: `docker compose logs -f backend frontend`.
- Rebuild: `docker compose build --no-cache backend frontend`.
- Restart: `docker compose restart backend frontend`.
- DB backup: `docker compose exec postgres pg_dump -U cms cms > cms-backup.sql`.
- DB restore into a fresh volume: `cat cms-backup.sql | docker compose exec -T postgres psql -U cms cms`.
- Uploaded files are stored in the `uploads` Docker volume and should be backed up with database snapshots.
