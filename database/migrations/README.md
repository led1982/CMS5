# CMS Database Migrations

Flyway-compatible migrations live in this directory. The backend Docker build copies these files into `classpath:db/migration` so Spring Boot can initialize PostgreSQL on first startup.

PostgreSQL Docker init scripts only run for an empty data volume. When an existing volume is reused, apply new migrations through Flyway by starting the backend service with the same database.
