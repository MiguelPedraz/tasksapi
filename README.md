# Tasks API - Hexagonal Architecture

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-purple)
![Coverage](https://img.shields.io/badge/Coverage-100%25-success)
![Dependencies](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen)

Task Management API built with Hexagonal Architecture (Ports & Adapters), following SOLID principles, DDD, and Event-Driven design patterns.

## ✨ Key Features

- 🏗️ **Hexagonal Architecture** with clear separation of concerns
- 🔄 **Latest Dependencies** - Automated updates via Renovate/Dependabot
- 🔒 **Security Scanning** - OWASP Dependency Check integrated
- ✅ **100% Test Coverage** - JaCoCo verification
- 📊 **Observability Ready** - Prometheus metrics with Actuator
- 📚 **API Documentation** - OpenAPI/Swagger UI
- 🎯 **Production Ready** - Best practices applied

## 🏗️ Architecture

This project implements **Hexagonal Architecture** with clear separation of concerns:

```
tasksapi/
├── domain/                      # Business Logic Layer (Core)
│   ├── model/                  # Domain Entities
│   │   ├── Task.java          # Task aggregate root
│   │   ├── Comment.java       # Comment value object
│   │   ├── Attachment.java    # Attachment value object
│   │   ├── TaskStatus.java    # Status enum
│   │   └── Priority.java      # Priority enum
│   ├── port/                   # Interfaces defining contracts
│   │   ├── in/                # Use Cases (Input Ports)
│   │   │   └── TaskUseCase.java
│   │   └── out/               # Repository Contracts (Output Ports)
│   │       └── TaskRepositoryPort.java
│   ├── event/                  # Domain Events
│   │   ├── TaskCreatedEvent.java
│   │   ├── TaskStatusChangedEvent.java
│   │   └── TaskAssignedEvent.java
│   └── exception/              # Domain Exceptions
│       └── TaskNotFoundException.java
│
├── application/                 # Application Layer (Use Cases Implementation)
│   ├── service/                # Service implementations
│   │   └── TaskService.java
│   ├── dto/                    # Data Transfer Objects
│   │   ├── TaskRequest.java
│   │   ├── TaskResponse.java
│   │   ├── StatusChangeRequest.java
│   │   └── AssignRequest.java
│   └── mapper/                 # DTO ↔ Domain mappers
│       └── TaskMapper.java
│
└── infrastructure/              # Infrastructure Layer (Adapters)
    ├── adapter/
    │   ├── in/rest/            # REST Controllers (Input Adapters)
    │   │   └── TaskController.java
    │   └── out/persistence/    # Database Adapters (Output Adapters)
    │       ├── entity/         # JPA Entities
    │       │   ├── TaskJpaEntity.java
    │       │   ├── CommentJpaEntity.java
    │       │   └── AttachmentJpaEntity.java
    │       ├── repository/     # Spring Data JPA Repositories
    │       │   └── TaskJpaRepository.java
    │       ├── TaskEntityMapper.java
    │       └── TaskRepositoryAdapter.java
    ├── config/                 # Framework Configuration
    │   ├── AsyncConfig.java
    │   ├── OpenApiConfig.java
    │   └── JpaAuditingConfig.java
    └── exception/              # Exception Handlers
        └── GlobalExceptionHandler.java
```

## 🎯 Features

### Core Functionality
- ✅ **CRUD Operations** for Tasks
- ✅ **Task Status Management** (TODO, IN_PROGRESS, IN_REVIEW, DONE, CANCELLED, BLOCKED)
- ✅ **Priority Management** (LOW, MEDIUM, HIGH, CRITICAL)
- ✅ **Task Assignment** with tracking of assignee and reporter
- ✅ **Subtasks Support** (hierarchical task structure)
- ✅ **Comments System** with author tracking
- ✅ **File Attachments** with metadata
- ✅ **Due Date Tracking** with overdue detection

### Technical Features
- 🏗️ **Hexagonal Architecture** (Ports & Adapters)
- 🎯 **Domain-Driven Design** (DDD)
- 🔄 **Event-Driven Architecture** with async event publishing
- 📊 **JPA Auditing** (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
- ✅ **Bean Validation** with comprehensive error handling
- 📚 **OpenAPI/Swagger** documentation
- 🔍 **Advanced Queries** (by status, assignee, reporter, overdue)
- 🎭 **Builder Pattern** for domain entities
- 🚫 **Domain Invariants** enforcement

## 🚀 Getting Started

### Prerequisites
- Java 21
- PostgreSQL 18
- Gradle 8.5+

### Database Setup

```bash
# Using Docker
docker run --name postgres-tasks \
  -e POSTGRES_DB=tasksdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:18-alpine
```

### Configuration

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasksdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Build & Run

```bash
# Navigate to project
cd tasksapi

# Build project
./gradlew build

# Run application
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

### API Documentation

Once running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📋 API Endpoints

### Task Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks` | Create a new task |
| GET | `/api/v1/tasks` | Get all tasks |
| GET | `/api/v1/tasks/{id}` | Get task by ID |
| PUT | `/api/v1/tasks/{id}` | Update task |
| DELETE | `/api/v1/tasks/{id}` | Delete task |

### Task Queries

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/tasks/status/{status}` | Get tasks by status |
| GET | `/api/v1/tasks/assignee/{assignee}` | Get tasks by assignee |
| GET | `/api/v1/tasks/reporter/{reporter}` | Get tasks by reporter |
| GET | `/api/v1/tasks/overdue` | Get overdue tasks |

### Task Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/api/v1/tasks/{id}/status` | Change task status |
| PATCH | `/api/v1/tasks/{id}/assign` | Assign task to user |
| PATCH | `/api/v1/tasks/{id}/priority` | Change task priority |

### Example Request

```bash
# Create a task
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement authentication",
    "description": "Add JWT authentication to the API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "reporter": "john.doe"
  }'
```

### Example Response

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement authentication",
  "description": "Add JWT authentication to the API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "reporter": "john.doe",
  "assignedTo": null,
  "subtaskIds": [],
  "commentCount": 0,
  "attachmentCount": 0,
  "overdue": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "createdBy": null,
  "updatedBy": null
}
```

## 🧪 Testing

This project enforces **100% code coverage** using JaCoCo.

```bash
# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage (fails if < 100%)
./gradlew jacocoTestCoverageVerification
```

Coverage reports are generated at:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## 🔄 Dependency Management

This project uses **automated dependency updates** to stay current with latest stable versions.

### Check for Updates

```bash
# Using convenience script (PowerShell)
.\scripts\update-dependencies.ps1

# Or directly with Gradle
./gradlew dependencyUpdates
```

### Security Scanning

```bash
# Check for known vulnerabilities
./gradlew dependencyCheckAnalyze

# View report
open app/build/reports/dependency-check-report.html
```

### Automated Updates

- **Renovate Bot**: Automatically creates PRs for dependency updates
- **Dependabot**: GitHub native dependency updates
- **Auto-merge**: Safe updates (patch/minor) auto-merge after tests pass

See [DEPENDENCY_MANAGEMENT.md](DEPENDENCY_MANAGEMENT.md) for detailed documentation.

## 🔧 Technology Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.3.0 |
| **Build Tool** | Gradle | 9.2.0 |
| **Database** | PostgreSQL | 18 |
| **ORM** | Spring Data JPA | 3.3.0 |
| **Testing** | JUnit 5 | 5.12.1 |
| **Code Coverage** | JaCoCo | Latest |
| **API Documentation** | SpringDoc OpenAPI | 2.5.0 |
| **Boilerplate Reduction** | Lombok | Latest |
| **Database Driver** | PostgreSQL JDBC | 42.7.3 |
| **Testing Database** | H2 | 2.2.224 |
| **Build Tool** | Gradle | 9.2.0 |
| **Dependency Management** | Version Catalog | Latest |
| **Security Scanner** | OWASP Dependency Check | 10.0.3 |
| **Update Checker** | Gradle Versions Plugin | 0.51.0 |

## 📐 Design Patterns Applied

1. **Hexagonal Architecture (Ports & Adapters)**
   - Clear separation between domain, application, and infrastructure
   - Domain is independent of frameworks and tools

2. **Repository Pattern**
   - Abstraction over data access through ports
   - JPA implementation hidden behind adapters

3. **Builder Pattern**
   - Fluent interface for domain entity construction
   - Enforces immutability

4. **Event-Driven Architecture**
   - Domain events for important business events
   - Asynchronous event processing

5. **DTO Pattern**
   - Separation of API contracts from domain models
   - Request/Response objects for REST API

6. **Exception Handling Pattern**
   - Global exception handler with ProblemDetail (RFC 7807)
   - Domain-specific exceptions

## 🔒 Business Rules

### Task Status Transitions
- Cannot change status from `CANCELLED`
- Can only reopen `DONE` tasks to `TODO`
- Other transitions are allowed

### Validation Rules
- **Title**: Required, 3-200 characters
- **Description**: Optional, max 5000 characters
- **Status**: Required
- **Priority**: Required
- **Reporter**: Required
- **Assignee**: Optional

## 📊 Domain Events

The system publishes the following domain events:

1. **TaskCreatedEvent**
   - Triggered when a new task is created
   - Contains: taskId, title, reporter, timestamp

2. **TaskStatusChangedEvent**
   - Triggered when task status changes
   - Contains: taskId, oldStatus, newStatus, changedBy, timestamp

3. **TaskAssignedEvent**
   - Triggered when a task is assigned
   - Contains: taskId, assignedTo, assignedBy, timestamp

Events are published asynchronously using Spring's `@Async`.

## 🛠️ Development

### Project Structure Philosophy

This project follows **Hexagonal Architecture** principles:

- **Domain Layer**: Contains pure business logic, no framework dependencies
- **Application Layer**: Orchestrates use cases, coordinates domain objects
- **Infrastructure Layer**: Implements technical concerns (REST, JPA, etc.)

### Key Benefits

✅ **Testability**: Business logic isolated from frameworks  
✅ **Flexibility**: Easy to swap implementations (e.g., change database)  
✅ **Maintainability**: Clear boundaries and dependencies  
✅ **Scalability**: Each layer can evolve independently  

## 📝 License

This project is licensed under the Apache 2.0 License.

## 🤝 Contributing

This project was generated using GitHub Copilot agents and skills for Hexagonal Architecture.

### Agents Used
- `arquitectura-hexagonal.md` - Hexagonal architecture pattern
- `crear-entidad-domain.md` - Domain entity creation
- `crear-repository-port.md` - Repository ports and adapters
- `implementar-service.md` - Service implementation
- `crear-rest-controller.md` - REST controller creation

---

Built with ❤️ using Hexagonal Architecture and best practices from GitHub Copilot agents.
