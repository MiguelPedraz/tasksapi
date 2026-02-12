package com.company.tasksapi.application.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.tasksapi.application.dto.TaskRequest;
import com.company.tasksapi.application.dto.TaskResponse;
import com.company.tasksapi.domain.model.Task;

@Component
public class TaskMapper {
    
    public Task requestToDomain(TaskRequest request) {
        return Task.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .assignedTo(request.getAssignedTo())
                .reporter(request.getReporter())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public Task requestToDomain(TaskRequest request, UUID id) {
        return Task.builder()
                .id(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .assignedTo(request.getAssignedTo())
                .reporter(request.getReporter())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public TaskResponse domainToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignedTo(task.getAssignedTo())
                .reporter(task.getReporter())
                .subtaskIds(task.getSubtasks())
                .commentCount(task.getComments().size())
                .attachmentCount(task.getAttachments().size())
                .overdue(task.isOverdue())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .createdBy(task.getCreatedBy())
                .updatedBy(task.getUpdatedBy())
                .build();
    }
}
