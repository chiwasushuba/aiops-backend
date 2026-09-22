---
name: aiops-database-migrations
description: Change this backend's PostgreSQL schema or Spring Data persistence. Use for Flyway migrations, JPA mapping, queries, constraints, and data integrity.
---

# Database migrations

Inspect `src/main/resources/db/migration`, the affected entity and repository,
and both application property files. Add a new versioned Flyway migration for
schema changes; do not edit an already applied migration. Keep Hibernate schema
mode at `validate`.

Check the impact on existing rows and concurrent writes when changing keys,
constraints, or indexes. Run the relevant H2-backed tests, then verify
PostgreSQL-specific SQL or behavior against PostgreSQL when the change depends
on dialect, locking, or constraint semantics. State clearly when that runtime
check could not be run.
