# CMS Operator Guide

## Services

The branch preview stack contains three services:

- `frontend`: Nginx-served React app on host port `5173`.
- `backend`: Spring Boot REST API on host port `8080`.
- `postgres`: PostgreSQL 16 on host port `5432`.

## Startup

```bash
docker compose up --build
```

The backend runs Flyway migrations from `database/migrations/` during startup. `database/seed/local-seed.sql` is provided for local verification after migrations are applied; PostgreSQL init scripts only run for an empty data volume and are intentionally not used by the compose stack.

## Common Operations

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose restart backend
docker compose build --no-cache backend frontend
```

## Backup

```bash
docker compose exec postgres pg_dump -U cms -d cms > cms-backup.sql
```

Upload files are represented by metadata in this MVP. In production, preserve the object storage bucket and database backup as a matched recovery point.

## Restore

```bash
docker compose down -v
docker compose up -d postgres
cat cms-backup.sql | docker compose exec -T postgres psql -U cms -d cms
docker compose up -d backend frontend
```

Confirm `/api/v1/health`, `/actuator/health`, portal search, admin audit logs, and notice acknowledgement after restore.

## Incident Response

1. Check `backend` health and logs.
2. Confirm PostgreSQL health with `pg_isready`.
3. Review recent audit logs for role, publication, and content target changes.
4. Rebuild and restart only the affected app service when possible.
5. If data loss is suspected, freeze writes, preserve logs, and restore from the latest verified backup.
