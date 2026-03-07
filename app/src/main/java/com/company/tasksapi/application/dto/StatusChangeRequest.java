package com.company.tasksapi.application.dto;

import com.company.tasksapi.domain.model.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "New status is required") TaskStatus newStatus,
        String changedBy) {
}
