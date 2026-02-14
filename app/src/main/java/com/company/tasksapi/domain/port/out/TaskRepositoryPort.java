package com.company.tasksapi.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskStatus;

public interface TaskRepositoryPort {
    
    Task save(Task task);
    
    Optional<Task> findById(UUID id);
    
    List<Task> findAll();
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByAssignedTo(String assignee);
    
    List<Task> findByReporter(String reporter);
    
    List<Task> findByDueDateBefore(LocalDateTime dateTime);
    
    void deleteById(UUID id);
    
    boolean existsById(UUID id);
}
