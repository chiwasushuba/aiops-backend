# AIOps backend

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

`JPA_DDL_AUTO=validate` deliberately prevents Hibernate from changing a
production schema. Add versioned database migrations before introducing tables.

For a clean backend-image rebuild after changing Java dependencies or the
Dockerfile:

```powershell
docker compose build --no-cache backend
docker compose up
```
