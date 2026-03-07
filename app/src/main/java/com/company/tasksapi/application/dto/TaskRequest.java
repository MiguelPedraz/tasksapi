package com.company.tasksapi.application.dto;

import java.time.LocalDateTime;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Title is required") @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @NotNull(message = "Status is required")
        TaskStatus status,

        @NotNull(message = "Priority is required")
        Priority priority,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dueDate,

        String assignedTo,

        @NotBlank(message = "Reporter is required")
        String reporter) {
}
