FROM gradle:8.11-jdk21 AS build
WORKDIR /workspace
COPY backend/settings.gradle backend/build.gradle ./
COPY backend/src ./src
COPY database/migrations ./src/main/resources/db/migration
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
