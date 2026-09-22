---
name: aiops-java-build
description: Build, test, or change Java and Maven behavior in this AIOps backend. Use for JDK, wrapper, Lombok, dependency, compilation, or Java test work.
---

# Java build and tests

Use Java 21 and the Maven wrapper declared in this repository. Inspect `pom.xml`
and the affected source and test packages before changing dependencies or build
behavior. Keep Spring Boot's managed dependency versions unless a concrete
compatibility need calls for an override.

For a focused users API check, run `.\mvnw.cmd -Dtest=UserControllerTests test`.
Run `.\mvnw.cmd test` when shared wiring, build configuration, or startup can be
affected. Report the command and result; do not infer runtime PostgreSQL
compatibility from an H2 test alone.
