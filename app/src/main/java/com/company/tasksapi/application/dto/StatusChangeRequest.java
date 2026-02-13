package com.company.tasksapi.application.dto;

import com.company.tasksapi.domain.model.TaskStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequest {
    
    @NotNull(message = "New status is required")
    private TaskStatus newStatus;
    
    private String changedBy;
}
