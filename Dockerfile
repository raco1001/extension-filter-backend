# Build stage
FROM gradle:7.6.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

ENTRYPOINT ["java", "-jar", "build/libs/file-extension-blocker-0.0.1-SNAPSHOT.jar"]