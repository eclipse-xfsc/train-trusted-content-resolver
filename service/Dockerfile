# Stage 1: Maven-Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Nur pom.xml kopieren und Dependencies cachen
COPY pom.xml .
RUN mvn dependency:go-offline

# Restliche Projektdateien kopieren und bauen
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM bellsoft/liberica-openjdk-alpine:21

# Copy das gebaute JAR aus dem Builder-Stage
COPY --from=builder /build/target/trusted-content-resolver-service-*.jar /trusted-content-resolver-service.jar

ENTRYPOINT ["java", "-jar", "/trusted-content-resolver-service.jar"]
