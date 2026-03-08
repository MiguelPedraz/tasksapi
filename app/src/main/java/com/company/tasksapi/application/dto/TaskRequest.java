package com.company.tasksapi.application.dto;

import java.time.LocalDateTime;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating or updating a task")
public record TaskRequest(
        @Schema(description = "Task title", example = "Fix login bug", minLength = 3, maxLength = 200) @NotBlank(message = "Title is required") @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,

        @Schema(description = "Detailed task description", example = "The login form fails on mobile browsers")
        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @Schema(description = "Initial task status", example = "TODO")
        @NotNull(message = "Status is required")
        TaskStatus status,

        @Schema(description = "Task priority level", example = "HIGH")
        @NotNull(message = "Priority is required")
        Priority priority,

        @Schema(description = "Due date in ISO-8601 format (UTC)", example = "2026-06-01T09:00:00") @FutureOrPresent(message = "Due date must be in the present or future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dueDate,

        @Schema(description = "Username of the assignee", example = "john.doe")
        String assignedTo,

        @Schema(description = "Username of the task reporter", example = "jane.smith")
        @NotBlank(message = "Reporter is required")
        String reporter) {
}
