package com.company.tasksapi.domain.port.in;

import java.util.List;
import java.util.UUID;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskStatus;

public interface TaskUseCase {
    
    Task createTask(Task task);
    
    Task updateTask(UUID id, Task task);
    
    void deleteTask(UUID id);
    
    Task getTaskById(UUID id);
    
    List<Task> getAllTasks();
    
    List<Task> getTasksByStatus(TaskStatus status);
    
    List<Task> getTasksByAssignee(String assignee);
    
    List<Task> getTasksByReporter(String reporter);
    
    Task changeTaskStatus(UUID id, TaskStatus newStatus, String changedBy);
    
    Task assignTask(UUID id, String assignee, String assignedBy);
    
    Task changePriority(UUID id, Priority newPriority);
    
    List<Task> getOverdueTasks();
}
