package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskAssignedEvent(UUID taskId, String assignedTo, String assignedBy, LocalDateTime occurredAt) {

    public TaskAssignedEvent(UUID taskId, String assignedTo, String assignedBy) {
        this(taskId, assignedTo, assignedBy, LocalDateTime.now());
    }
}
