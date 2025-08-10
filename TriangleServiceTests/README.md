# Triangle Service Tests

This directory contains the comprehensive test suite for the Triangle API Service.

## Structure

- `src/test/java/triangle_api_tests/` - Test classes
- `src/main/java/triangle_api/` - Test helper classes and utilities
- `pom.xml` - Maven configuration
- `testng.xml` - TestNG suite configuration

## Test Classes

- **AddTriangle_Tests** - Triangle creation validation
- **GetAllTriangles_Test** - List all triangles
- **GetTriangle_Tests** - Get specific triangle
- **DeleteTriangle_Tests** - Delete triangle operations
- **GetArea_Tests** - Area calculation
- **GetPerimeter_Test** - Perimeter calculation
- **NoAuthorization_Tests** - Authentication validation

## Running Tests

### Prerequisites
- Java 11+
- Maven 3.6+
- Triangle Service running on http://localhost:8080 (or set BASE_URL environment variable)

### Commands

```bash
# Run all tests
mvn clean test -Dtest=testng.xml

# Run specific test class
mvn test -Dtest=AddTriangle_Tests

# Run with Allure reporting
mvn clean test -Dtest=testng.xml
allure serve test-output/allure-results

# Compile only
mvn clean compile
```

### Environment Variables

- `BASE_URL` - Base URL for the API service (default: http://localhost:8080)

## Test Configuration

The tests are configured to run against the Triangle Service and include:
- Authentication validation
- Triangle validation (including known bugs)
- Error handling verification
- Performance and edge case testing

## Known Issues Tested

The test suite validates the following intentionally implemented bugs:
1. Triangle Limit Bug: Allows 11 triangles instead of 10
2. Delete Validation Bug: Accepts any ID for deletion
3. Separator Error Bug: Some separators cause 500 errors
4. Payload Validation Inconsistency: Mixed error codes 