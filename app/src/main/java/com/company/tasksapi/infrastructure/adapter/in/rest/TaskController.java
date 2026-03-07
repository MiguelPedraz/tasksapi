package com.company.tasksapi.infrastructure.adapter.in.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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

import com.company.tasksapi.application.dto.AssignRequest;
import com.company.tasksapi.application.dto.StatusChangeRequest;
import com.company.tasksapi.application.dto.TaskRequest;
import com.company.tasksapi.application.dto.TaskResponse;
import com.company.tasksapi.application.mapper.TaskMapper;
import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskStatus;
import com.company.tasksapi.domain.port.in.TaskUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {
    
    private final TaskUseCase taskUseCase;
    private final TaskMapper taskMapper;
    
    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        Task task = taskMapper.requestToDomain(request);
        Task createdTask = taskUseCase.createTask(task);
        TaskResponse response = taskMapper.domainToResponse(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates an existing task with new details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request) {
        Task task = taskMapper.requestToDomain(request, id);
        Task updatedTask = taskUseCase.updateTask(id, task);
        TaskResponse response = taskMapper.domainToResponse(updatedTask);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID") @PathVariable UUID id) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a task by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<TaskResponse> getTaskById(@Parameter(description = "Task ID") @PathVariable UUID id) {
        Task task = taskUseCase.getTaskById(id);
        TaskResponse response = taskMapper.domainToResponse(task);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks in the system")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskUseCase.getAllTasks();
        List<TaskResponse> response = tasks.stream()
                .map(taskMapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieves all tasks with a specific status")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable TaskStatus status) {
        List<Task> tasks = taskUseCase.getTasksByStatus(status);
        List<TaskResponse> response = tasks.stream()
                .map(taskMapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/assignee/{assignee}")
    @Operation(summary = "Get tasks by assignee", description = "Retrieves all tasks assigned to a specific user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(
            @Parameter(description = "Assignee username") @PathVariable String assignee) {
        List<Task> tasks = taskUseCase.getTasksByAssignee(assignee);
        List<TaskResponse> response = tasks.stream()
                .map(taskMapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reporter/{reporter}")
    @Operation(summary = "Get tasks by reporter", description = "Retrieves all tasks created by a specific user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<TaskResponse>> getTasksByReporter(
            @Parameter(description = "Reporter username") @PathVariable String reporter) {
        List<Task> tasks = taskUseCase.getTasksByReporter(reporter);
        List<TaskResponse> response = tasks.stream()
                .map(taskMapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Retrieves all tasks that are past their due date")
    @ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<Task> tasks = taskUseCase.getOverdueTasks();
        List<TaskResponse> response = tasks.stream()
                .map(taskMapper::domainToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change task status", description = "Changes the status of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status changed successfully",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content)
    })
    public ResponseEntity<TaskResponse> changeTaskStatus(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody StatusChangeRequest request) {
        Task updatedTask = taskUseCase.changeTaskStatus(id, request.newStatus(), request.changedBy());
        TaskResponse response = taskMapper.domainToResponse(updatedTask);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign task", description = "Assigns a task to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task assigned successfully",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<TaskResponse> assignTask(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody AssignRequest request) {
        Task updatedTask = taskUseCase.assignTask(id, request.assignee(), request.assignedBy());
        TaskResponse response = taskMapper.domainToResponse(updatedTask);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/priority")
    @Operation(summary = "Change task priority", description = "Changes the priority of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Priority changed successfully",
                content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<TaskResponse> changePriority(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Parameter(description = "New priority") @RequestParam Priority priority) {
        Task updatedTask = taskUseCase.changePriority(id, priority);
        TaskResponse response = taskMapper.domainToResponse(updatedTask);
        return ResponseEntity.ok(response);
    }
}
