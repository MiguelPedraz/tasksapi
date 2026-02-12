# API Usage Examples

This document provides comprehensive examples of how to use the Tasks API.

## Base URL

```
http://localhost:8080/api/v1/tasks
```

## Authentication

*Note: This version does not include authentication. Future versions will implement JWT/OAuth2.*

---

## 1. Create a Task

### Request

```http
POST /api/v1/tasks
Content-Type: application/json

{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "reporter": "john.doe",
  "assignedTo": "jane.smith"
}
```

### cURL

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication to the API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "reporter": "john.doe",
    "assignedTo": "jane.smith"
  }'
```

### Response (201 Created)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "assignedTo": "jane.smith",
  "reporter": "john.doe",
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

---

## 2. Get All Tasks

### Request

```http
GET /api/v1/tasks
```

### cURL

```bash
curl http://localhost:8080/api/v1/tasks
```

### Response (200 OK)

```json
[
  {
    "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
    "title": "Implement user authentication",
    "status": "TODO",
    "priority": "HIGH",
    "assignedTo": "jane.smith",
    "reporter": "john.doe",
    "overdue": false,
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
    "title": "Write API documentation",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "assignedTo": "bob.johnson",
    "reporter": "john.doe",
    "overdue": false,
    "createdAt": "2024-01-14T09:15:00"
  }
]
```

---

## 3. Get Task by ID

### Request

```http
GET /api/v1/tasks/{id}
```

### cURL

```bash
curl http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d
```

### Response (200 OK)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "assignedTo": "jane.smith",
  "reporter": "john.doe",
  "subtaskIds": [],
  "commentCount": 0,
  "attachmentCount": 0,
  "overdue": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

## 4. Update a Task

### Request

```http
PUT /api/v1/tasks/{id}
Content-Type: application/json

{
  "title": "Implement user authentication (Updated)",
  "description": "Add JWT and OAuth2 authentication to the API",
  "status": "IN_PROGRESS",
  "priority": "CRITICAL",
  "dueDate": "2024-12-15T23:59:59",
  "reporter": "john.doe",
  "assignedTo": "jane.smith"
}
```

### cURL

```bash
curl -X PUT http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication (Updated)",
    "description": "Add JWT and OAuth2 authentication to the API",
    "status": "IN_PROGRESS",
    "priority": "CRITICAL",
    "dueDate": "2024-12-15T23:59:59",
    "reporter": "john.doe",
    "assignedTo": "jane.smith"
  }'
```

### Response (200 OK)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication (Updated)",
  "description": "Add JWT and OAuth2 authentication to the API",
  "status": "IN_PROGRESS",
  "priority": "CRITICAL",
  "dueDate": "2024-12-15T23:59:59",
  "assignedTo": "jane.smith",
  "reporter": "john.doe",
  "updatedAt": "2024-01-15T14:45:00"
}
```

---

## 5. Change Task Status

### Request

```http
PATCH /api/v1/tasks/{id}/status
Content-Type: application/json

{
  "newStatus": "IN_REVIEW",
  "changedBy": "jane.smith"
}
```

### cURL

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_REVIEW",
    "changedBy": "jane.smith"
  }'
```

### Response (200 OK)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication",
  "status": "IN_REVIEW",
  "priority": "HIGH",
  "updatedAt": "2024-01-15T15:30:00"
}
```

---

## 6. Assign Task

### Request

```http
PATCH /api/v1/tasks/{id}/assign
Content-Type: application/json

{
  "assignee": "bob.johnson",
  "assignedBy": "john.doe"
}
```

### cURL

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d/assign \
  -H "Content-Type: application/json" \
  -d '{
    "assignee": "bob.johnson",
    "assignedBy": "john.doe"
  }'
```

### Response (200 OK)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication",
  "assignedTo": "bob.johnson",
  "updatedAt": "2024-01-15T16:00:00"
}
```

---

## 7. Change Task Priority

### Request

```http
PATCH /api/v1/tasks/{id}/priority?priority=CRITICAL
```

### cURL

```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d/priority?priority=CRITICAL"
```

### Response (200 OK)

```json
{
  "id": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "title": "Implement user authentication",
  "priority": "CRITICAL",
  "updatedAt": "2024-01-15T16:15:00"
}
```

---

## 8. Get Tasks by Status

### Request

```http
GET /api/v1/tasks/status/{status}
```

### cURL

```bash
# Get all tasks with status TODO
curl http://localhost:8080/api/v1/tasks/status/TODO

# Get all tasks IN_PROGRESS
curl http://localhost:8080/api/v1/tasks/status/IN_PROGRESS

# Get all DONE tasks
curl http://localhost:8080/api/v1/tasks/status/DONE
```

### Available Statuses
- `TODO`
- `IN_PROGRESS`
- `IN_REVIEW`
- `DONE`
- `CANCELLED`
- `BLOCKED`

---

## 9. Get Tasks by Assignee

### Request

```http
GET /api/v1/tasks/assignee/{assignee}
```

### cURL

```bash
curl http://localhost:8080/api/v1/tasks/assignee/jane.smith
```

---

## 10. Get Tasks by Reporter

### Request

```http
GET /api/v1/tasks/reporter/{reporter}
```

### cURL

```bash
curl http://localhost:8080/api/v1/tasks/reporter/john.doe
```

---

## 11. Get Overdue Tasks

### Request

```http
GET /api/v1/tasks/overdue
```

### cURL

```bash
curl http://localhost:8080/api/v1/tasks/overdue
```

### Response (200 OK)

```json
[
  {
    "id": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
    "title": "Fix critical bug",
    "status": "TODO",
    "priority": "CRITICAL",
    "dueDate": "2024-01-10T23:59:59",
    "overdue": true,
    "assignedTo": "alice.williams"
  }
]
```

---

## 12. Delete a Task

### Request

```http
DELETE /api/v1/tasks/{id}
```

### cURL

```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d
```

### Response (204 No Content)

No response body.

---

## Error Responses

### 400 Bad Request - Validation Error

```json
{
  "type": "about:blank",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed for one or more fields",
  "timestamp": "2024-01-15T10:30:00Z",
  "errors": {
    "title": "Title is required",
    "priority": "Priority is required"
  }
}
```

### 404 Not Found - Task Not Found

```json
{
  "type": "about:blank",
  "title": "Task Not Found",
  "status": 404,
  "detail": "Task not found with id: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
  "timestamp": "2024-01-15T10:30:00Z",
  "taskId": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
}
```

### 409 Conflict - Invalid State Transition

```json
{
  "type": "about:blank",
  "title": "Invalid State Transition",
  "status": 409,
  "detail": "Cannot change status from CANCELLED",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## Priority Levels

- `LOW` - Low priority tasks
- `MEDIUM` - Medium priority tasks (default)
- `HIGH` - High priority tasks
- `CRITICAL` - Critical priority tasks

---

## Complete Workflow Example

### 1. Create a Task

```bash
TASK_ID=$(curl -s -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Develop new feature",
    "description": "Implement the new dashboard feature",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "reporter": "john.doe"
  }' | jq -r '.id')

echo "Created task: $TASK_ID"
```

### 2. Assign the Task

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/$TASK_ID/assign \
  -H "Content-Type: application/json" \
  -d '{
    "assignee": "jane.smith",
    "assignedBy": "john.doe"
  }'
```

### 3. Start Working (Change Status)

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/$TASK_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_PROGRESS",
    "changedBy": "jane.smith"
  }'
```

### 4. Move to Review

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/$TASK_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_REVIEW",
    "changedBy": "jane.smith"
  }'
```

### 5. Complete the Task

```bash
curl -X PATCH http://localhost:8080/api/v1/tasks/$TASK_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "DONE",
    "changedBy": "john.doe"
  }'
```

---

## Testing with PowerShell

### Create a Task

```powershell
$body = @{
    title = "Test Task"
    description = "This is a test task"
    status = "TODO"
    priority = "MEDIUM"
    reporter = "test.user"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tasks" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

### Get All Tasks

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tasks" -Method Get
```

### Get Task by ID

```powershell
$taskId = "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/tasks/$taskId" -Method Get
```

---

## Notes

- All dates are in ISO 8601 format: `yyyy-MM-dd'T'HH:mm:ss`
- UUIDs are generated automatically for new tasks
- Task status transitions follow business rules (e.g., cannot change from CANCELLED)
- The API uses RFC 7807 Problem Details for error responses
- Domain events are published asynchronously for task creation, status changes, and assignments
