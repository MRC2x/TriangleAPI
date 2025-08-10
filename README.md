# Triangle API Project

This project consists of the Triangle API Service and a set of comprehensive tests for it.

## Project Structure

- `TriangleService/` - Spring Boot API service implementation
- `TriangleServiceTests/` - RestAssured + TestNG test suite for the API
- `src/test/java/triangle_api_tests/` - Test classes
- `src/main/java/triangle_api/` - Test helper classes

## Triangle API Service

The Triangle Service is a Spring Boot application that provides REST API endpoints for triangle operations:

### Endpoints

- `POST /triangle/` - Create a new triangle (max 10 triangles)
- `GET /triangle/all` - Get all triangles
- `GET /triangle/{id}` - Get specific triangle
- `DELETE /triangle/{id}` - Delete triangle
- `GET /triangle/{id}/area` - Get triangle area
- `GET /triangle/{id}/perimeter` - Get triangle perimeter

### Authentication

All endpoints require the `X-User` header with the token: `9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564`

### Request Format

Create triangle request:
```json
{
  "separator": ";",
  "input": "3;4;5"
}
```

## Running the Service

### Option 1: Docker Compose (Recommended)

```bash
# Build and run both service and tests
cd TriangleService
docker-compose up --build
```

### Option 2: Manual Docker

```bash
# Build the service
cd TriangleService
mvn clean package
docker build -t triangle-service .

# Run the service
docker run -p 8080:8080 triangle-service
```

### Option 3: Local Development

```bash
# Run the service locally
cd TriangleService
mvn spring-boot:run

# Access Swagger UI
# Open browser: http://localhost:8080/swagger-ui.html

# Run tests against local service
cd TriangleServiceTests
mvn clean test -Dtest=testng.xml
```

## Running Tests

The test suite uses TestNG and RestAssured to validate the API behavior.

### Test Categories

- **AddTriangle_Tests** - Triangle creation validation
- **GetAllTriangles_Test** - List all triangles
- **GetTriangle_Tests** - Get specific triangle
- **DeleteTriangle_Tests** - Delete triangle operations
- **GetArea_Tests** - Area calculation
- **GetPerimeter_Test** - Perimeter calculation
- **NoAuthorization_Tests** - Authentication validation

### Known Issues/Bugs Implemented

The service intentionally includes several bugs to match the original test expectations:

1. **Triangle Limit Bug**: Allows 11 triangles instead of 10
2. **Delete Validation Bug**: Accepts any ID for deletion without validation
3. **Separator Error Bug**: Some separators cause 500 errors instead of 422
4. **Payload Validation Inconsistency**: Mixed 400/422 error codes for similar issues

### Running Tests

```bash
# Run all tests
cd TriangleServiceTests
mvn clean test -Dtest=testng.xml

# Run specific test class
cd TriangleServiceTests
mvn test -Dtest=AddTriangle_Tests

# Run with Allure reporting
cd TriangleServiceTests
mvn clean test -Dtest=testng.xml
allure serve test-output/allure-results
```

## GitHub Actions

The project includes a GitHub Actions workflow that:

1. Builds the Triangle Service
2. Creates a Docker container
3. Runs the test suite
4. Uploads test results as artifacts

The workflow runs on push to main/master and pull requests.

## Configuration

### Environment Variables

- `BASE_URL` - Base URL for the API service (default: http://localhost:8080)

### Service Configuration

The service runs on port 8080 by default. Configuration can be modified in `TriangleService/src/main/resources/application.yml`.

## API Documentation

### Swagger UI

The service includes interactive API documentation powered by Swagger/OpenAPI.

**Access Swagger UI:**
- **URL**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

**Features:**
- Interactive API testing
- Request/response examples
- Authentication support (X-User header)
- Detailed endpoint descriptions
- Error response documentation

### Triangle Creation

**POST** `/triangle/`

Headers:
- `Content-Type: application/json`
- `X-User: 9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564`

Body:
```json
{
  "separator": ";",
  "input": "3;4;5"
}
```

Response:
```json
{
  "id": "uuid-string",
  "firstSide": 3.0,
  "secondSide": 4.0,
  "thirdSide": 5.0
}
```

### Error Responses

- `400 Bad Request` - Invalid request format
- `401 Unauthorized` - Missing or invalid token
- `404 Not Found` - Triangle not found
- `405 Method Not Allowed` - Unsupported HTTP method
- `422 Unprocessable Entity` - Invalid triangle data
- `500 Internal Server Error` - Server error (for specific separators)

## Development

### Prerequisites

- Java 11+
- Maven 3.6+
- Docker (for containerized deployment)

### Building

```bash
# Build service
cd TriangleService
mvn clean package

# Build tests
cd TriangleServiceTests
mvn clean compile
```

### Testing

```bash
# Run tests
cd TriangleServiceTests
mvn clean test

# Run with specific profile
cd TriangleServiceTests
mvn test -Dspring.profiles.active=test
```
