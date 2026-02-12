package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.tasksapi.domain.model.TaskStatus;

public class TaskStatusChangedEvent {
    private final UUID taskId;
    private final TaskStatus oldStatus;
    private final TaskStatus newStatus;
    private final String changedBy;
    private final LocalDateTime occurredAt;
    
    public TaskStatusChangedEvent(UUID taskId, TaskStatus oldStatus, TaskStatus newStatus, String changedBy) {
        this.taskId = taskId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.occurredAt = LocalDateTime.now();
    }
    
    public UUID getTaskId() {
        return taskId;
    }
    
    public TaskStatus getOldStatus() {
        return oldStatus;
    }
    
    public TaskStatus getNewStatus() {
        return newStatus;
    }
    
    public String getChangedBy() {
        return changedBy;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
