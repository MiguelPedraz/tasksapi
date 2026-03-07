package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.tasksapi.domain.model.TaskStatus;

public record TaskStatusChangedEvent(UUID taskId, TaskStatus oldStatus, TaskStatus newStatus, String changedBy,
        LocalDateTime occurredAt) {

    public TaskStatusChangedEvent(UUID taskId, TaskStatus oldStatus, TaskStatus newStatus, String changedBy) {
        this(taskId, oldStatus, newStatus, changedBy, LocalDateTime.now());
    }
}
