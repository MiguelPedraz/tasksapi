package com.company.tasksapi.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.tasksapi.domain.model.Task;

public record TaskCreatedEvent(UUID taskId, String title, String reporter, LocalDateTime occurredAt) {

    public TaskCreatedEvent(Task task) {
        this(task.getId(), task.getTitle(), task.getReporter(), LocalDateTime.now());
    }
}
