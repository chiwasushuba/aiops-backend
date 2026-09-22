# AIOps backend project guidance

## Current scope

This repository is a Java 21, Spring Boot 4.1.1 REST backend. The implemented
feature is the users API in `src/main/java/com/aiops/aiops_backend/users`.
The AIOps domain is planned; do not assume incident, alert, telemetry, or model
workflows exist until they are added to the code and documented.

The controller owns HTTP mapping and response status, the service owns user
operations and transactions, the Spring Data repository owns persistence, and
Flyway migrations own database schema changes. `README.md` documents the API
and local Docker setup; `springboot_tutorial.md` is a practice guide.

## Commands

- Focused users API tests: `.\mvnw.cmd -Dtest=UserControllerTests test`
- All tests: `.\mvnw.cmd test`
- Package: `.\mvnw.cmd -DskipTests package`
- Local stack: `docker compose up --build` after configuring `.env` from
  `.env.example` as described in `README.md`.

Use the Maven wrapper and the pinned Java version. The tests use the `test`
profile and an in-memory H2 database; the local stack uses PostgreSQL.

## Working conventions

- Keep HTTP request and response contracts explicit. Validate input at the
  boundary and cover status codes and failure paths in API tests.
- Add versioned Flyway migrations for schema changes. Keep Hibernate schema
  mode at `validate`; do not use automatic schema mutation as a migration.
- Check both H2 test behavior and PostgreSQL-specific behavior when a change
  depends on SQL dialect, constraints, or database concurrency.
- Keep `.env`, passwords, tokens, and local runtime data out of Git and output.
- Check authorization before exposing user profile data to untrusted clients;
  authentication and authorization are not implemented yet.
- For new AIOps features, establish their data sources, contracts, retention,
  failure behavior, and operational impact from the actual requirement before
  choosing storage, queues, models, or external integrations.

## Project agents

Project-specific roles live in `.codex/agents/`. Use
`aiops-java-engineer` for JVM, Spring Boot, Maven, framework wiring, and test
issues; `aiops-backend-engineer` for HTTP APIs, services, integration contracts,
and feature behavior; `aiops-database-engineer` for Flyway, JPA mapping,
queries, and PostgreSQL data integrity; and `aiops-platform-engineer` for the
Dockerfile, Compose stack, and runtime configuration. Give one role primary
ownership of a change and involve another only for a distinct question or file
boundary. Global review, debugging, security, and AI specialists remain
available when those areas are actually involved.

## Project skills and feature log

Project workflows live in `.codex/skills/` and are registered in
`.codex/config.toml`. Use `aiops-java-build` for Maven and Java checks,
`aiops-backend-api` for HTTP contracts, `aiops-database-migrations` for schema
and persistence, `aiops-springboot-runtime` for application wiring and profiles,
and `aiops-performance` only for a measured performance task. The broader global
skills remain available for work outside these repository-specific workflows.

Keep `.codex/CHANGELOG.md` short. Add a concise entry when an application
feature or its external behavior changes; do not add entries for routine agent
configuration, refactoring, or test-only changes.

## Verification and review

Run focused tests for the changed feature, then the full suite when shared
configuration, persistence, or application startup is affected. Review changes
for HTTP compatibility, transaction behavior, validation, data integrity, and
secret exposure. Do not commit or deploy as part of ordinary implementation.
