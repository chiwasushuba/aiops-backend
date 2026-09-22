---
name: aiops-springboot-runtime
description: Change Spring Boot configuration or application wiring in this backend. Use for profiles, validation setup, transactions, startup, and Flyway/JPA integration.
---

# Spring Boot runtime

Use the Spring Boot 4.1.1 and Java 21 versions in `pom.xml`. Read
`application.properties`, `application-test.properties`, the application entry
point, and the affected component before changing configuration or wiring.

Keep database connection values supplied by environment variables for local
and container runs. The `test` profile uses H2; normal startup uses PostgreSQL
and Flyway before Hibernate validates the schema. Verify affected wiring with
an application context or API test, and use the Compose stack when behavior
depends on the real database or container environment.
