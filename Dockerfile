# =============================================================================
# Multi-stage Dockerfile — Maven build -> distroless JRE runtime
# =============================================================================
# Stage 1 compiles the jar with Maven; Stage 2 runs it on a hardened distroless
# Java image (no shell, no package manager, nonroot). Kaniko (in the governed
# Build-and-Push template) builds this with NO Docker daemon.
#
# NOTE: the image TAG is NOT set here — the Harness pipeline tags the pushed
# image with the pom.xml version (see the pipeline's read_version step).
# =============================================================================

# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# ---- runtime stage ----
FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
COPY --from=build /src/target/app.jar /app/app.jar
EXPOSE 8080
USER nonroot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
