package com.company.tasksapi.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.domain.model.TaskStatus;
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
    public List<Task> findAll() {
        return taskJpaRepository.findAll().stream()
                .map(TaskEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return taskJpaRepository.findByStatus(status).stream()
                .map(TaskEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Task> findByAssignedTo(String assignee) {
        return taskJpaRepository.findByAssignedTo(assignee).stream()
                .map(TaskEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Task> findByReporter(String reporter) {
        return taskJpaRepository.findByReporter(reporter).stream()
                .map(TaskEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Task> findByDueDateBefore(LocalDateTime dateTime) {
        return taskJpaRepository.findOverdueTasks(dateTime).stream()
                .map(TaskEntityMapper::toDomain)
                .toList();
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
