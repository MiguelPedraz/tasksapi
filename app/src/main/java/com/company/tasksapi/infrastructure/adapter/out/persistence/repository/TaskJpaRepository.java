package com.company.tasksapi.infrastructure.adapter.out.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.tasksapi.domain.model.TaskStatus;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    
    List<TaskJpaEntity> findByStatus(TaskStatus status);
    
    List<TaskJpaEntity> findByAssignedTo(String assignee);
    
    List<TaskJpaEntity> findByReporter(String reporter);
    
    @Query("SELECT t FROM TaskJpaEntity t WHERE t.dueDate < :dateTime AND t.status <> 'DONE' AND t.status <> 'CANCELLED'")
    List<TaskJpaEntity> findOverdueTasks(@Param("dateTime") LocalDateTime dateTime);
    
    @Query("SELECT t FROM TaskJpaEntity t WHERE t.parentTask IS NULL")
    List<TaskJpaEntity> findAllRootTasks();
}
