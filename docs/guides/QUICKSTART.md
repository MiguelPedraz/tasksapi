# Quick Start Guide

This guide will help you get the Tasks API up and running in minutes.

## Prerequisites

- Java 21 installed
- Docker and Docker Compose (for PostgreSQL)
- Or a local PostgreSQL 18 installation

## Step 1: Start PostgreSQL

### Option A: Using Docker Compose (Recommended)

```bash
# From the tasksapi directory
docker compose up -d
```

This will start PostgreSQL 18 on port 5432 with:
- Database: `tasksdb`
- Username: `postgres`
- Password: `postgres`

### Option B: Using Docker directly

```bash
docker run --name tasks-postgres \
  -e POSTGRES_DB=tasksdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:18-alpine
```

### Option C: Using local PostgreSQL

Create a database named `tasksdb` and update `application.properties` with your credentials.

## Step 2: Build the Application

```bash
# Navigate to the project directory
cd tasksapi

# Build the project (skip tests for quick start)
./gradlew build -x test
```

## Step 3: Run the Application

```bash
./gradlew bootRun
```

Or using Java directly:

```bash
java -jar app/build/libs/app.jar
```

The application will start on **http://localhost:8080**

## Step 4: Verify Installation

Open your browser and navigate to:

**Swagger UI**: http://localhost:8080/swagger-ui.html

You should see the interactive API documentation.

## Step 5: Create Your First Task

### Using cURL

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Task",
    "description": "Testing the Tasks API",
    "status": "TODO",
    "priority": "MEDIUM",
    "reporter": "john.doe"
  }'
```

### Using PowerShell

```powershell
$body = @{
    title = "My First Task"
    description = "Testing the Tasks API"
    status = "TODO"
    priority = "MEDIUM"
    reporter = "john.doe"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tasks" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Using Swagger UI

1. Go to http://localhost:8080/swagger-ui.html
2. Find the `POST /api/v1/tasks` endpoint
3. Click "Try it out"
4. Enter the request body
5. Click "Execute"

## Step 6: Explore the API

Visit the [API Examples](API_EXAMPLES.md) document for comprehensive usage examples.

## Common Issues

### Port 8080 already in use

Change the port in `application.properties`:

```properties
server.port=8081
```

### Cannot connect to PostgreSQL

Check if PostgreSQL is running:

```bash
docker ps
```

Check the logs:

```bash
docker logs tasks-postgres
```

### Build fails

Make sure you have Java 21:

```bash
java -version
```

Clean and rebuild:

```bash
./gradlew clean build
```

## Next Steps

- Read the [README.md](README.md) for architecture details
- Explore [API_EXAMPLES.md](API_EXAMPLES.md) for comprehensive examples
- Check the Swagger UI for interactive testing
- Review the test suite for usage patterns

## Stop the Application

Press `Ctrl+C` in the terminal where the application is running.

To stop PostgreSQL:

```bash
docker compose down
```

To stop and remove all data:

```bash
docker compose down -v
```

## Development Mode

For development with auto-reload, use Spring Boot DevTools (already included in dependencies):

```bash
./gradlew bootRun
```

Any changes to Java files will trigger an automatic restart.

---

**Happy coding! 🚀**
