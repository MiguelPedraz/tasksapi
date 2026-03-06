package com.company.tasksapi.application.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.company.tasksapi.domain.event.TaskAssignedEvent;
import com.company.tasksapi.domain.event.TaskCreatedEvent;
import com.company.tasksapi.domain.event.TaskStatusChangedEvent;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Async("taskExecutor")
    public void publishTaskCreated(Task task) {
        eventPublisher.publishEvent(new TaskCreatedEvent(task));
        log.debug("Published TaskCreatedEvent for task: {}", task.getId());
    }

    @Async("taskExecutor")
    public void publishStatusChanged(UUID taskId, TaskStatus oldStatus, TaskStatus newStatus, String changedBy) {
        eventPublisher.publishEvent(new TaskStatusChangedEvent(taskId, oldStatus, newStatus, changedBy));
        log.debug("Published TaskStatusChangedEvent for task: {}", taskId);
    }

    @Async("taskExecutor")
    public void publishTaskAssigned(UUID taskId, String assignee, String assignedBy) {
        eventPublisher.publishEvent(new TaskAssignedEvent(taskId, assignee, assignedBy));
        log.debug("Published TaskAssignedEvent for task: {}", taskId);
    }
}
