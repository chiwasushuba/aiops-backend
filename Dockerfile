# syntax=docker/dockerfile:1

# Build the executable Spring Boot JAR in an isolated, reproducible stage.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

# Keep compilers, Maven caches, and source code out of the runtime image.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd --system --gid 10001 spring && useradd --system --uid 10001 --gid spring --create-home spring
COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
