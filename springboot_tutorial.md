# Spring Boot practice guide for this AIOps backend

This guide uses the existing users API. Work through it in order, then try the
exercises without copying the current implementation. The project uses Java 21,
Spring Boot 4.1.1, Maven, Spring MVC, Spring Data JPA, Flyway, and PostgreSQL.

## 1. How the users API is set up

### Translate the Express/NestJS concepts

| Express or NestJS | In this Spring Boot project | Where to look |
| --- | --- | --- |
| Route definitions | Mapping annotations on a controller | `users/UserController.java` |
| Controller | `@RestController` | `users/UserController.java` |
| Service | `@Service` | `users/UserService.java` |
| ORM model | JPA `@Entity` | `users/User.java` |
| Data access methods | Spring Data `JpaRepository` | `users/UserRepository.java` |
| Request validation | Jakarta Validation annotations and `@Valid` | `users/UserRequest.java` |
| Database migration | Versioned Flyway SQL file | `db/migration/V1__create_users.sql` |
| Exception filter | `@RestControllerAdvice` | `users/UserErrorHandler.java` |

NestJS normally groups these files in a `UsersModule`. This project does not
need a `UsersModule.java`. Instead, it groups the files in a Java package named
`com.aiops.aiops_backend.users`, and Spring Boot discovers the annotated classes
in that package automatically.

### Create the folders and files

From the repository root, the users feature has this layout:

```text
src/
|-- main/
|   |-- java/com/aiops/aiops_backend/
|   |   |-- AiopsBackendApplication.java
|   |   `-- users/
|   |       |-- User.java
|   |       |-- UserController.java
|   |       |-- UserErrorHandler.java
|   |       |-- UserPage.java
|   |       |-- UserRepository.java
|   |       |-- UserRequest.java
|   |       |-- UserResponse.java
|   |       `-- UserService.java
|   `-- resources/
|       |-- application.properties
|       `-- db/migration/
|           `-- V1__create_users.sql
`-- test/
    |-- java/com/aiops/aiops_backend/users/
    |   `-- UserControllerTests.java
    `-- resources/
        `-- application-test.properties
```

In IntelliJ IDEA or another Java IDE, create a package named
`com.aiops.aiops_backend.users` under `src/main/java`, then create the Java
files inside that package. Each file begins with the matching package line:

```java
package com.aiops.aiops_backend.users;
```

Create database migration files under `src/main/resources/db/migration`, not
inside the Java package. Create tests under the matching package path in
`src/test/java`. Files in the same Java package can refer to each other without
import statements.

### Build the feature in this order

When creating a similar API from scratch, this order makes the dependencies
easier to understand:

1. Create `V1__create_users.sql` to define the `users` database table.
2. Create `User.java` and map it to that table with `@Entity` and `@Table`.
3. Create `UserRepository.java` by extending `JpaRepository<User, Long>`.
   Spring Data generates the repository implementation; you do not create a
   `UserRepositoryImpl` for these standard operations.
4. Create `UserRequest.java` for incoming JSON, `UserResponse.java` for outgoing
   JSON, and `UserPage.java` for paginated list responses.
5. Create `UserService.java` for user operations and database transactions.
6. Create `UserController.java` to define the `/api/users` HTTP routes.
7. Create `UserErrorHandler.java` to translate user exceptions into HTTP status
   codes such as `404 Not Found` and `409 Conflict`.
8. Create `UserControllerTests.java` under `src/test/java` to verify the whole
   request flow.

### Connect the files

The dependency flow is:

```text
HTTP request
    -> UserController
        -> UserService
            -> UserRepository
                -> JPA/Hibernate
                    -> users table

JSON body -> UserRequest
User entity -> UserResponse -> JSON response
Exceptions -> UserErrorHandler -> HTTP error response
```

You connect the controller to the service by declaring a constructor parameter:

```java
private final UserService service;

public UserController(UserService service) {
    this.service = service;
}
```

You connect the service to the repository in the same way:

```java
private final UserRepository repository;

public UserService(UserRepository repository) {
    this.repository = repository;
}
```

You do not call either constructor yourself. At startup, Spring sees
`@RestController`, `@Service`, and the `JpaRepository` interface, creates the
objects, and supplies each constructor dependency. This is dependency injection,
similar to listing providers and injecting them in NestJS.

`AiopsBackendApplication.java` is in the parent package
`com.aiops.aiops_backend`. Its `@SpringBootApplication` annotation scans that
package and all child packages, including `users`. If you put a feature outside
that package tree, Spring will not discover it unless you configure additional
component scanning. Keeping feature packages below the application package is
the simplest setup.

The database is connected separately through `application.properties`.
Spring Boot uses those datasource settings to connect JPA to PostgreSQL, while
Flyway finds and runs the migration files in `db/migration` at startup.

### Follow one request

For `POST /api/users`:

1. `@PostMapping` in `UserController` selects the method.
2. `@RequestBody` converts JSON into a `UserRequest`; `@Valid` checks its fields.
3. The controller calls `UserService.create` through constructor injection.
4. The service trims the name, normalizes the email, and saves a `User` through
   `UserRepository` inside a transaction.
5. JPA maps `User` to the `users` table. The table was created by Flyway when
   the application started.
6. The controller returns `201 Created`, a `Location` header, and a
   `UserResponse` JSON body.

`UserRequest` and `UserResponse` are API data shapes. `User` is the database
entity. Keeping them separate lets you change the table without automatically
changing every API response.

## 2. Run it and make requests

From the repository root in PowerShell, make sure `.env` contains your local
PostgreSQL password, then start the stack:

```powershell
docker compose up --build
```

In another terminal, create a user with an email you have not used before:

```powershell
$user = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/users `
  -ContentType 'application/json' `
  -Body '{"name":"Ada Lovelace","email":"ada@example.com"}'
$user
```

Try the read, list, update, and delete routes:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/$($user.id)"
Invoke-RestMethod -Uri 'http://localhost:8080/api/users?page=0&size=20'

Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/users/$($user.id)" `
  -ContentType 'application/json' `
  -Body '{"name":"Ada Byron","email":"ada@example.com"}'

Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/users/$($user.id)"
```

Inspect `docker compose logs backend` to see startup and migration output.
`docker compose down` stops the services while retaining the database volume.

## 3. Read the annotations in context

- `@RestController` makes controller return values HTTP response bodies.
- `@RequestMapping("/api/users")` sets the shared URL prefix. `@GetMapping`,
  `@PostMapping`, `@PutMapping`, and `@DeleteMapping` select a method and path.
- `@PathVariable` reads `{id}`; `@RequestParam` reads `page` and `size`.
- `@Valid` runs the constraints on `UserRequest`. Invalid input returns `400`.
- `@Service` marks the class that owns user operations. Spring passes its
  repository dependency through the constructor.
- `@Transactional` groups writes into a database transaction. The read methods
  use `@Transactional(readOnly = true)`.
- `@Entity`, `@Table`, `@Id`, and `@Column` map a Java object to the table.
  `JpaRepository<User, Long>` supplies persistence operations such as `findById`.
- `@RestControllerAdvice` maps user-specific exceptions to `404` and `409`.

The unique email rule also exists in the database migration. Request validation
helps callers correct input, while the database constraint protects uniqueness
when two requests arrive at nearly the same time.

## 4. Practice tasks

Do each task in a small edit, run the tests, and make an HTTP request to check
what the client sees.

### A. Explore validation

Send a blank name, an invalid email, and a `size=101` list request. Predict the
status before each request. Find which annotation handles each case. Then add a
test in `UserControllerTests` that checks one invalid request returns `400`.

### B. Add a lookup by email

Add `GET /api/users/by-email?email=...` without changing `GET /api/users/{id}`.
Add a repository method that returns `Optional<User>`, a service method that
normalizes the email before lookup, and a controller method. Return `404` when
no user matches. Test a mixed-case lookup and a missing email.

Hint: Spring Data can derive a query from a method named `findByEmail`.

### C. Add a profile field

Add an optional `displayName` field. Make a **new** Flyway file such as
`V2__add_user_display_name.sql`; do not edit `V1` after it has run. Then update
`User`, the request and response records, the service, and the API tests. Check
the migration runs against an existing local database as well as a new one.

### D. Try pagination

Create three users. Request `GET /api/users?page=0&size=2` and then page `1`.
Explain why the API orders by ID and why the controller caps page size at 100.
Check `content` and `totalElements` in both responses.

## 5. Test and debug

Run all tests with the checked-in Maven wrapper:

```powershell
.\mvnw.cmd test
```

`UserControllerTests` exercises HTTP handling, validation, service logic, JPA,
and migrations with the test database configured in
`src/test/resources/application-test.properties`. That database is H2 in
PostgreSQL compatibility mode. For PostgreSQL-specific behavior, also test
against PostgreSQL; H2 does not reproduce every PostgreSQL rule.

When something fails, trace the request in this order: controller mapping,
request validation, service call, repository query, entity mapping, migration,
and datasource configuration. A startup schema validation failure often means
the entity and migration disagree. The production configuration keeps
`spring.jpa.hibernate.ddl-auto=validate`, so Hibernate checks the schema rather
than silently changing it.

Authentication and authorization are outside this exercise. Add them before
exposing these endpoints to untrusted clients.
