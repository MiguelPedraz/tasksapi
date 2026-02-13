package com.company.tasksapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {
    
    @NotBlank(message = "Assignee is required")
    private String assignee;
    
    private String assignedBy;
}
