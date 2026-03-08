package com.company.tasksapi.infrastructure.adapter.in.rest;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.company.tasksapi.application.dto.AssignRequest;
import com.company.tasksapi.application.dto.PageResponse;
import com.company.tasksapi.application.dto.StatusChangeRequest;
import com.company.tasksapi.application.dto.TaskRequest;
import com.company.tasksapi.application.dto.TaskResponse;
import com.company.tasksapi.application.mapper.TaskMapper;
import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskPage;
import com.company.tasksapi.domain.model.TaskStatus;
import com.company.tasksapi.domain.port.in.TaskUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {

    private final TaskUseCase taskUseCase;
    private final TaskMapper taskMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a task", description = "Creates a new task and returns its location")
    @ApiResponse(responseCode = "201", description = "Task created", headers = @Header(name = "Location", description = "URL of the created task"),
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        Task task = taskMapper.requestToDomain(request);
        Task created = taskUseCase.createTask(task);
        TaskResponse response = taskMapper.domainToResponse(created);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns a paginated, filtered list of tasks")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<TaskResponse>> getTasks(
            @Parameter(description = "Filter by status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter by priority") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Filter by assignee") @RequestParam(required = false) String assignee,
            @Parameter(description = "Filter by reporter") @RequestParam(required = false) String reporter,
            @Parameter(description = "Return only overdue tasks") @RequestParam(defaultValue = "false") boolean overdue,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort ascending") @RequestParam(defaultValue = "false") boolean ascending) {

        TaskFilter filter = new TaskFilter(status, priority, assignee, reporter, overdue);
        TaskPage taskPage = taskUseCase.getTasks(filter, page, size, sortBy, ascending);

        PageResponse<TaskResponse> response = PageResponse.of(
                taskPage.content().stream().map(taskMapper::domainToResponse).toList(),
                taskPage.page(),
                taskPage.size(),
                taskPage.totalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", description = "Task found", content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Task UUID") @PathVariable UUID id) {
        Task task = taskUseCase.getTaskById(id);
        return ResponseEntity.ok(taskMapper.domainToResponse(task));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a task", description = "Full replacement of task fields")
    @ApiResponse(responseCode = "200", description = "Task updated",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task UUID") @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request) {
        Task task = taskMapper.requestToDomain(request, id);
        Task updated = taskUseCase.updateTask(id, task);
        return ResponseEntity.ok(taskMapper.domainToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task UUID") @PathVariable UUID id) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Change task status")
    @ApiResponse(responseCode = "200", description = "Status changed",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content)
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<TaskResponse> changeTaskStatus(
            @Parameter(description = "Task UUID") @PathVariable UUID id,
            @Valid @RequestBody StatusChangeRequest request) {
        Task updated = taskUseCase.changeTaskStatus(id, request.newStatus(), request.changedBy());
        return ResponseEntity.ok(taskMapper.domainToResponse(updated));
    }

    @PatchMapping(value = "/{id}/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Assign task to a user")
    @ApiResponse(responseCode = "200", description = "Task assigned",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<TaskResponse> assignTask(
            @Parameter(description = "Task UUID") @PathVariable UUID id,
            @Valid @RequestBody AssignRequest request) {
        Task updated = taskUseCase.assignTask(id, request.assignee(), request.assignedBy());
        return ResponseEntity.ok(taskMapper.domainToResponse(updated));
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Change task priority")
    @ApiResponse(responseCode = "200", description = "Priority changed",
            content = @Content(schema = @Schema(implementation = TaskResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid priority value", content = @Content)
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    public ResponseEntity<TaskResponse> changePriority(
            @Parameter(description = "Task UUID") @PathVariable UUID id,
            @Parameter(description = "New priority", required = true) @RequestParam Priority priority) {
        Task updated = taskUseCase.changePriority(id, priority);
        return ResponseEntity.ok(taskMapper.domainToResponse(updated));
    }

    // Retain backward-compatible status filter endpoint (deprecated)
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status (deprecated)", description = "Deprecated: use GET /api/v1/tasks?status=VALUE instead", deprecated = true)
    public ResponseEntity<PageResponse<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        TaskFilter filter = new TaskFilter(status, null, null, null, false);
        TaskPage taskPage = taskUseCase.getTasks(filter, page, size, "createdAt", false);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Deprecation", "true")
                .header("Link", "</api/v1/tasks?status=" + status + ">; rel=\"successor-version\"")
                .body(PageResponse.of(
                        taskPage.content().stream().map(taskMapper::domainToResponse).toList(),
                        taskPage.page(), taskPage.size(), taskPage.totalElements()));
    }
}
