#!/bin/bash

echo "Testing Triangle API Service"
echo "============================"

# Check if we should run tests locally or in Docker
RUN_MODE=${1:-"local"}
BASE_URL=${BASE_URL:-"http://localhost:8080"}

if [ "$RUN_MODE" = "docker" ]; then
    echo "Running tests in Docker..."
    
    # Build the API tests first
    echo "Building API tests..."
    cd triangle-api-tests
    mvn clean compile -q
    cd ..
    
    # Run tests using docker compose
    docker compose --profile test up --build triangle-api-tests
    
    # Copy test results out of container if needed
    echo "Test results are available in triangle-api-tests/test-output/"
    
elif [ "$RUN_MODE" = "local" ]; then
    echo "Running tests locally..."
    echo "Base URL: $BASE_URL"
    
    # Check if service is running
    if ! curl -f "$BASE_URL/actuator/health" &> /dev/null; then
        echo "Service is not running at $BASE_URL"
        echo "Please start the service first with: ./run-service.sh"
        exit 1
    fi
    
    # Run Maven tests
    cd triangle-api-tests
    mvn clean test
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ All tests passed!"
        echo "Test reports available in triangle-api-tests/test-output/"
    else
        echo ""
        echo "❌ Some tests failed. Check the reports in triangle-api-tests/test-output/"
    fi
    
else
    echo "Usage: $0 [local|docker]"
    echo "  local  - Run tests locally against running service (default)"
    echo "  docker - Run tests in Docker container"
    exit 1
fi