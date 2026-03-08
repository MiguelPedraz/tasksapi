package com.company.tasksapi.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.tasksapi.domain.exception.TaskNotFoundException;
import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskPage;
import com.company.tasksapi.domain.model.TaskStatus;
import com.company.tasksapi.domain.port.in.TaskUseCase;
import com.company.tasksapi.domain.port.out.TaskRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService implements TaskUseCase {

    private final TaskRepositoryPort taskRepository;
    private final AsyncEventPublisher asyncEventPublisher;

    @Override
    public Task createTask(Task task) {
        log.info("Creating new task: {}", task.getTitle());
        Task savedTask = taskRepository.save(task);
        asyncEventPublisher.publishTaskCreated(savedTask);
        log.info("Task created successfully with id: {}", savedTask.getId());
        return savedTask;
    }

    @Override
    public Task updateTask(UUID id, Task task) {
        log.info("Updating task with id: {}", id);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        Task updatedTask = Task.builder()
                .id(existingTask.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignedTo(task.getAssignedTo())
                .reporter(existingTask.getReporter())
                .subtasks(existingTask.getSubtasks())
                .comments(existingTask.getComments())
                .attachments(existingTask.getAttachments())
                .createdAt(existingTask.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .createdBy(existingTask.getCreatedBy())
                .updatedBy(task.getUpdatedBy())
                .build();

        Task savedTask = taskRepository.save(updatedTask);
        log.info("Task updated successfully: {}", id);
        return savedTask;
    }

    @Override
    public void deleteTask(UUID id) {
        log.info("Deleting task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        log.info("Task deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(UUID id) {
        log.debug("Fetching task with id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskPage getTasks(TaskFilter filter, int page, int size, String sortBy, boolean ascending) {
        log.debug("Fetching tasks — filter: {}, page: {}, size: {}, sortBy: {}", filter, page, size, sortBy);
        return taskRepository.findByFilter(filter, page, size, sortBy, ascending);
    }

    @Override
    public Task changeTaskStatus(UUID id, TaskStatus newStatus, String changedBy) {
        log.info("Changing status of task {} to {}", id, newStatus);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        TaskStatus oldStatus = task.getStatus();
        task.changeStatus(newStatus);

        Task updatedTask = taskRepository.save(task);
        asyncEventPublisher.publishStatusChanged(id, oldStatus, newStatus, changedBy);
        log.info("Task status changed successfully: {} -> {}", oldStatus, newStatus);
        return updatedTask;
    }

    @Override
    public Task assignTask(UUID id, String assignee, String assignedBy) {
        log.info("Assigning task {} to {}", id, assignee);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.assign(assignee);
        Task updatedTask = taskRepository.save(task);
        asyncEventPublisher.publishTaskAssigned(id, assignee, assignedBy);
        log.info("Task assigned successfully to: {}", assignee);
        return updatedTask;
    }

    @Override
    public Task changePriority(UUID id, Priority newPriority) {
        log.info("Changing priority of task {} to {}", id, newPriority);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.changePriority(newPriority);
        Task updatedTask = taskRepository.save(task);
        log.info("Task priority changed successfully to: {}", newPriority);
        return updatedTask;
    }

}
