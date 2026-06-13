# CMS Smoke Validation

## API

```bash
curl http://localhost:8080/api/v1/health
curl -H "X-CMS-Roles: EMPLOYEE" "http://localhost:8080/api/v1/portal/search?q=security"
curl -H "X-CMS-Roles: ADMIN" http://localhost:8080/api/v1/admin/audit-logs
```

Expected:

- Health returns `UP`.
- Portal search returns only `PUBLISHED` content visible to the user role.
- Admin audit logs return for `ADMIN` and fail with `403` for `EMPLOYEE`.

## UI

- First route `/` opens the portal home.
- Portal home shows search, required notice, latest updates, popular knowledge, and category shortcuts.
- `/admin` redirects to forbidden for `EMPLOYEE` and opens the dashboard for `ADMIN`.
- Content editor exposes edit, preview, and table-of-contents tabs.
- Review queue exposes approval and rejection decision dialog.
- Notice detail allows acknowledgement and records local acknowledged state.
- Analytics dashboard shows period filters, KPI cards, popular content, search terms, and export action.

## Database

- `V001__initial_cms_schema.sql` creates users, roles, content, versions, audiences, attachments, audit, metrics, and backup metadata tables.
- `V002__content_search_indexes.sql` creates PostgreSQL full-text and operational indexes.
- Reusing an existing PostgreSQL volume does not rerun PostgreSQL init scripts; use Flyway migrations for schema changes and apply `database/seed/local-seed.sql` manually when local relational seed rows are needed.
