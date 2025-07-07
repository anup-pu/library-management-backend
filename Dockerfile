# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-21-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/library-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
