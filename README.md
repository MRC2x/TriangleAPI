# Triangle API Project

A multi-module Maven project containing a Triangle API service and comprehensive black-box API tests for it.

## Project Structure

```
triangle-project/
├── pom.xml                    # Parent POM with shared configuration
├── triangle-service/          # Spring Boot REST API service
│   ├── src/main/java/        # Service source code
│   ├── pom.xml               # Service dependencies
│   └── Dockerfile            # Service container image
├── triangle-api-tests/        # API test suite
│   ├── src/test/java/        # Test source code
│   ├── testng.xml            # TestNG configuration
│   ├── pom.xml               # Test dependencies
│   └── Dockerfile            # Test container image
├── docker-compose.yml         # Container orchestration
├── run-service.sh            # Service startup script
└── test-service.sh           # Test execution script
```

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

**Note for macOS users**: The scripts use built-in shell commands and don't require additional tools like `timeout` or `coreutils`.

### Running the Service

1. **Start the service:**
   ```bash
   ./run-service.sh
   ```
   This will build and start the Triangle service on run 

2. **Run API tests locally:**
   ```bash
   ./test-service.sh local
   ```

3. **Run API tests in Docker:**
   ```bash
   ./test-service.sh docker
   ```

### Manual Commands

**Build everything:**
```bash
mvn clean package
```

**Start service only:**
```bash
docker-compose up triangle-service
```

**Run tests with full orchestration:**
```bash
docker compose --profile test up --build triangle-api-tests
```

**Stop everything:**
```bash
docker-compose down
```

## API Endpoints

- `GET /actuator/health` - Health check
- `POST /triangle/` - Create triangle
- `GET /triangle/all` - Get all triangles
- `GET /triangle/{id}` - Get triangle by ID
- `GET /triangle/{id}/area` - Get triangle area
- `GET /triangle/{id}/perimeter` - Get triangle perimeter
- `DELETE /triangle/{id}` - Delete triangle

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html (when service is running)
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs (when service is running)

## Testing

The API tests use:
- **TestNG** for test framework
- **REST Assured** for API testing
- **Allure** for test reporting
- **Docker** for isolated test execution

Test reports are generated in `triangle-api-tests/test-output/`

## Development

### Adding New Tests

1. Add test classes in `triangle-api-tests/src/test/java/`
2. Update `triangle-api-tests/testng.xml` if needed
3. Run tests: `./test-service.sh local`

### Modifying the Service

1. Edit code in `triangle-service/src/main/java/`
2. Rebuild: `mvn clean package -pl triangle-service`
3. Restart: `docker-compose restart triangle-service`
