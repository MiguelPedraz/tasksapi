package com.company.tasksapi.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskPage;

public interface TaskRepositoryPort {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    TaskPage findByFilter(TaskFilter filter, int page, int size, String sortBy, boolean ascending);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}