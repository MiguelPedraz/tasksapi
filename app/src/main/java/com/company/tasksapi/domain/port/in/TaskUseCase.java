package com.company.tasksapi.domain.port.in;

import java.util.UUID;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskPage;
import com.company.tasksapi.domain.model.TaskStatus;

public interface TaskUseCase {

    Task createTask(Task task);

    Task updateTask(UUID id, Task task);

    void deleteTask(UUID id);

    Task getTaskById(UUID id);

    TaskPage getTasks(TaskFilter filter, int page, int size, String sortBy, boolean ascending);

    Task changeTaskStatus(UUID id, TaskStatus newStatus, String changedBy);

    Task assignTask(UUID id, String assignee, String assignedBy);

    Task changePriority(UUID id, Priority newPriority);
}
