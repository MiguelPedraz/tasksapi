package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.tasksapi.domain.model.Task;

public class TaskCreatedEvent {
    private final UUID taskId;
    private final String title;
    private final String reporter;
    private final LocalDateTime occurredAt;
    
    public TaskCreatedEvent(Task task) {
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.reporter = task.getReporter();
        this.occurredAt = LocalDateTime.now();
    }
    
    public UUID getTaskId() {
        return taskId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getReporter() {
        return reporter;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
