FROM maven:3.9.6-eclipse-temurin-17-alpine

WORKDIR /app

# Install curl for health checks and wait-for-it script
RUN apk add --no-cache curl wget bash

# Download wait-for-it script
RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x wait-for-it.sh

# Copy parent POM and install it locally
COPY pom.xml ./parent-pom.xml
RUN mvn install:install-file \
    -Dfile=parent-pom.xml \
    -DgroupId=com.triangle \
    -DartifactId=triangle-project \
    -Dversion=1.0.0 \
    -Dpackaging=pom

# Copy test module pom.xml
COPY triangle-api-tests/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the test project
COPY triangle-api-tests/ .

# Create test output directory
RUN mkdir -p test-output

# Run tests with wait for service
CMD ["./wait-for-it.sh", "triangle-service:8080", "--timeout=60", "--", "mvn", "clean", "test"] 