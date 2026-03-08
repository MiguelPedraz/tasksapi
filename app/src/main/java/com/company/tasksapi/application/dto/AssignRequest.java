package com.company.tasksapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRequest(
        @NotBlank(message = "Assignee is required") String assignee,
        String assignedBy) {

}
