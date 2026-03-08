package com.company.tasksapi.infrastructure.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskPage;
import com.company.tasksapi.domain.port.out.TaskRepositoryPort;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import com.company.tasksapi.infrastructure.adapter.out.persistence.repository.TaskJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository taskJpaRepository;

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = TaskEntityMapper.toEntity(task);
        TaskJpaEntity savedEntity = taskJpaRepository.save(entity);
        return TaskEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return taskJpaRepository.findById(id)
                .map(TaskEntityMapper::toDomain);
    }

    @Override
    public TaskPage findByFilter(TaskFilter filter, int page, int size, String sortBy, boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, field));

        Page<TaskJpaEntity> result = taskJpaRepository.findAll(
                TaskSpecification.fromFilter(filter), pageRequest);

        return new TaskPage(
                result.getContent().stream().map(TaskEntityMapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    public void deleteById(UUID id) {
        taskJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return taskJpaRepository.existsById(id);
    }
}
