FROM openjdk:11-jre-slim

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the test project
COPY . .

# Install Maven wrapper
RUN chmod +x mvnw

# Run tests
CMD ["./mvnw", "clean", "test", "-Dtest=testng.xml"] 