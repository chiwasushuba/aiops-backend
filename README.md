# AIOps backend

New to Spring Boot? Work through [the practice guide](springboot_tutorial.md)
using this project's users API.

## Run the backend and PostgreSQL with Docker

Prerequisite: Docker Desktop must be running. This project uses the Docker
Compose plugin (`docker compose`), not the older `docker-compose` binary.

1. Create an uncommitted local environment file:

   ```powershell
   Copy-Item .env.example .env
   ```

2. In `.env`, replace `POSTGRES_PASSWORD` with a strong local password. Do not
   commit the file. The database defaults to `aiops` / `aiops`; optionally set
   `POSTGRES_DB` and `POSTGRES_USER` in that same file.

3. Build and start both services:

   ```powershell
   docker compose up --build
   ```

   Compose waits for PostgreSQL to pass its health check before starting Spring
   Boot. The API listens at `http://localhost:8080`; PostgreSQL is reachable by
   a local database client at `localhost:5432`.

4. From another terminal, inspect the stack:

   ```powershell
   docker compose ps
   docker compose logs -f backend
   ```

   A successful backend startup contains `Started AiopsBackendApplication`.

5. Stop the stack while retaining database data:

   ```powershell
   docker compose down
   ```

   The `postgres-data` named volume persists data between starts. To
   deliberately erase it, use `docker compose down --volumes`.

### Runtime configuration

| Variable | Default outside Docker | Compose value |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/aiops` | PostgreSQL container `db` |
| `DB_USERNAME` | `aiops` | `POSTGRES_USER` |
| `DB_PASSWORD` | empty | `POSTGRES_PASSWORD` from `.env` |
| `JPA_DDL_AUTO` | `validate` | `validate` |

`JPA_DDL_AUTO=validate` prevents Hibernate from changing the schema. Flyway runs
versioned migrations from `src/main/resources/db/migration` when the application
starts, then Hibernate validates the resulting schema.

## Users API

The users feature lives in `com.aiops.aiops_backend.users`. The controller maps
HTTP requests, the service owns user operations, the Spring Data repository
accesses PostgreSQL, and the `User` entity maps the `users` table. Spring MVC
replaces the separate Express route file with annotations on the controller.

| Method | Path | Result |
| --- | --- | --- |
| `POST` | `/api/users` | Create a user (`201`, with `Location`) |
| `GET` | `/api/users/{id}` | Get one user (`404` if missing) |
| `GET` | `/api/users?page=0&size=20` | List users by ID, up to 100 per page |
| `PUT` | `/api/users/{id}` | Replace name and email (`404` if missing) |
| `DELETE` | `/api/users/{id}` | Delete a user (`204`, or `404` if missing) |

Create and update take `{ "name": "Alice", "email": "alice@example.com" }`.
Responses contain `id`, `name`, and `email`. The list response contains
`content`, `page`, `size`, and `totalElements`. Names are trimmed, emails are
trimmed and stored lowercase, and duplicate emails return `409`. Invalid input
returns `400`.

This feature contains profile data only. Authentication and authorization still
need to be added before exposing these endpoints to untrusted clients.

## Messages API

Messages contain a `role` (`USER` or `ASSISTANT`) and `content`. The API supports
creation, retrieval, pagination, replacement, partial updates, and deletion.

| Method | Path | Result |
| --- | --- | --- |
| `POST` | `/api/messages` | Create a message (`201`, with `Location`) |
| `GET` | `/api/messages/{id}` | Get one message (`404` if missing) |
| `GET` | `/api/messages?page=0&size=20` | List messages by ID, up to 100 per page |
| `PUT` | `/api/messages/{id}` | Replace the role and content |
| `PATCH` | `/api/messages/{id}` | Update one or both fields |
| `DELETE` | `/api/messages/{id}` | Delete a message (`204`, or `404` if missing) |

Create and replace requests take `{ "role": "USER", "content": "Hello" }`.
A patch may contain only `role` or only `content`. Invalid input returns `400`.

For a clean backend-image rebuild after changing Java dependencies or the
Dockerfile:

```powershell
docker compose build --no-cache backend
docker compose up
```
