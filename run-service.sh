#!/bin/bash

echo "Triangle API Service Runner"
echo "=========================="

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

echo "Building Triangle Service..."
cd TriangleService
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Failed to build Triangle Service"
    exit 1
fi

echo "Building Docker image..."
docker build -t triangle-service .

if [ $? -ne 0 ]; then
    echo "Failed to build Docker image"
    exit 1
fi

echo "Starting Triangle Service..."
docker run -d --name triangle-service -p 8080:8080 triangle-service

if [ $? -ne 0 ]; then
    echo "Failed to start Triangle Service"
    exit 1
fi

echo "Waiting for service to be ready..."
timeout 60 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 2; done'

if [ $? -ne 0 ]; then
    echo "Service failed to start within 60 seconds"
    docker logs triangle-service
    docker stop triangle-service
    docker rm triangle-service
    exit 1
fi

echo "Triangle Service is running on http://localhost:8080"
echo ""
echo "To run tests:"
echo "  cd TriangleServiceTests && mvn clean test -Dtest=testng.xml"
echo ""
echo "To stop the service:"
echo "  docker stop triangle-service"
echo "  docker rm triangle-service"
echo ""
echo "Press Ctrl+C to stop the service and exit"

# Wait for user to stop
trap 'echo ""; echo "Stopping service..."; docker stop triangle-service; docker rm triangle-service; echo "Service stopped"; exit 0' INT

while true; do
    sleep 1
done 