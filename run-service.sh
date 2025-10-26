#!/bin/bash

echo "Triangle API Service Runner"
echo "=========================="

# Check if Docker and Docker Compose are available
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! docker compose version &> /dev/null 2>&1; then
    echo "Docker Compose is not available. Please install Docker and Docker Compose first."
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

echo "Building Triangle Service..."
cd triangle-service
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Failed to build Triangle Service"
    exit 1
fi

cd ..

echo "Starting Triangle Service with Docker Compose..."
docker compose up -d triangle-service

if [ $? -ne 0 ]; then
    echo "Failed to start Triangle Service"
    exit 1
fi

echo "Waiting for service to be ready..."
COUNTER=0
MAX_ATTEMPTS=30
until curl -f http://localhost:8080/actuator/health &> /dev/null; do
    if [ $COUNTER -ge $MAX_ATTEMPTS ]; then
        echo "❌ Service failed to start within 60 seconds"
        echo "Showing service logs:"
        docker compose logs triangle-service
        docker compose down
        exit 1
    fi
    if [ $COUNTER -eq 0 ]; then
        echo "⏳ Waiting for service to start (this may take 10-15 seconds)..."
    elif [ $((COUNTER % 5)) -eq 0 ]; then
        echo "⏳ Still waiting... (attempt $((COUNTER + 1))/$MAX_ATTEMPTS)"
    fi
    sleep 2
    COUNTER=$((COUNTER + 1))
done

echo "✅ Service is healthy and ready!"

echo ""
echo "🚀 Triangle Service is running on http://localhost:8080"
echo ""
echo "Available commands:"
echo "  Run API tests:     ./test-service.sh"
echo "  Run tests in Docker: docker compose --profile test up triangle-api-tests"
echo "  Stop service:      docker compose down"
echo "  View logs:         docker compose logs triangle-service"
echo ""
echo "Press Ctrl+C to stop the service and exit"

# Wait for user to stop
trap 'echo ""; echo "Stopping service..."; docker compose down; echo "Service stopped"; exit 0' INT

while true; do
    sleep 1
done