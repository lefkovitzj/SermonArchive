# --- Stage 1: Build the application ---
FROM maven:3.9.14-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache Maven dependencies.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package the JAR.
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: Run as a non-root user.
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs
RUN chown -R spring:spring /app
USER spring:spring

# Copy the compiled JAR from the build stage.
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot's default port.
EXPOSE 8080

# Run with Java 21.
ENTRYPOINT ["java", "-jar", "app.jar"]