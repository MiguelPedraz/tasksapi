package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskAssignedEvent {
    private final UUID taskId;
    private final String assignedTo;
    private final String assignedBy;
    private final LocalDateTime occurredAt;
    
    public TaskAssignedEvent(UUID taskId, String assignedTo, String assignedBy) {
        this.taskId = taskId;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.occurredAt = LocalDateTime.now();
    }
    
    public UUID getTaskId() {
        return taskId;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public String getAssignedBy() {
        return assignedBy;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
