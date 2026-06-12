# CMS Operations Guide

## Local Run

Use the branch-preview stack from the repository root:

```bash
docker compose -f infra/docker-compose.yml up --build
```

Published ports:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- Backend health: http://localhost:8080/api/v1/health

The frontend includes a seeded-user selector for local validation:

- `admin@example.com`: administrator
- `editor@example.com`: content manager
- `reviewer@example.com`: reviewer
- `employee@example.com`: employee in `HR Employees`
- `outsider@example.com`: employee outside `HR Employees`

## Roles

- Employee: reads visible published portal content, searches, bookmarks, and acknowledges notices.
- Content manager: creates drafts, edits content, submits for review, publishes, archives, and manages attachment metadata.
- Reviewer: approves submitted content.
- Administrator: manages taxonomy, roles, audiences, audit review, and analytics summaries.

## Lifecycle Rules

Supported states:

```text
DRAFT -> SUBMITTED -> APPROVED -> PUBLISHED -> ARCHIVED
DRAFT -> SUBMITTED -> REJECTED -> DRAFT
APPROVED -> SCHEDULED -> PUBLISHED
```

The backend enforces lifecycle permissions and visibility. Portal reads, search results, bookmarks, notice acknowledgement, and attachment links only expose content that is published, within its publish window, and visible to the caller's audience.

## File Policy

Application multipart limits are configured in `backend/src/main/resources/application.yml`:

- Single file: 10MB
- Full request: 20MB

Oversized upload attempts return HTTP 413 with the `FILE_TOO_LARGE` error code. Binary storage is mounted at `/var/cms/uploads` in Docker Compose and backed by the `cms-uploads` named volume.

## PostgreSQL Initialization

Flyway migrations create the schema and seed smoke-test data at first application startup. PostgreSQL Docker entrypoint scripts and Flyway migrations only initialize an empty database. If an existing Docker volume is reused, previous data remains and seed changes are not replayed unless a new migration is added or the volume is removed intentionally.

## Logs and Restart

```bash
docker compose -f infra/docker-compose.yml logs -f backend
docker compose -f infra/docker-compose.yml logs -f frontend
docker compose -f infra/docker-compose.yml restart backend frontend
docker compose -f infra/docker-compose.yml up --build -d backend frontend
```

## Backup and Restore

Database backup:

```bash
docker compose -f infra/docker-compose.yml exec postgres pg_dump -U cms -d cms > cms-backup.sql
```

Database restore into a fresh database:

```bash
docker compose -f infra/docker-compose.yml exec -T postgres psql -U cms -d cms < cms-backup.sql
```

Upload files are stored in the `cms-uploads` named volume. Back up that volume alongside PostgreSQL so attachment metadata and binary objects stay consistent.

## Handover Checklist

- Confirm seeded smoke users can switch roles from the frontend.
- Confirm `outsider@example.com` cannot see HR-only content in feed, search, or direct portal detail.
- Confirm lifecycle actions write audit events.
- Confirm required notice acknowledgement is idempotent for the current published version.
- Confirm backup and restore commands are adapted to the target production environment.
