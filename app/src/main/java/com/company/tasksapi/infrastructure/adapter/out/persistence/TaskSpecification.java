package com.company.tasksapi.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.company.tasksapi.domain.model.TaskFilter;
import com.company.tasksapi.domain.model.TaskStatus;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;

public class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<TaskJpaEntity> fromFilter(TaskFilter filter) {
        return Specification
                .where(withStatus(filter))
                .and(withPriority(filter))
                .and(withAssignee(filter))
                .and(withReporter(filter))
                .and(overdueOnly(filter));
    }

    private static Specification<TaskJpaEntity> withStatus(TaskFilter filter) {
        return (root, query, cb) ->
                filter.status() == null ? null : cb.equal(root.get("status"), filter.status());
    }

    private static Specification<TaskJpaEntity> withPriority(TaskFilter filter) {
        return (root, query, cb) ->
                filter.priority() == null ? null : cb.equal(root.get("priority"), filter.priority());
    }

    private static Specification<TaskJpaEntity> withAssignee(TaskFilter filter) {
        return (root, query, cb) ->
                filter.assignedTo() == null ? null : cb.equal(root.get("assignedTo"), filter.assignedTo());
    }

    private static Specification<TaskJpaEntity> withReporter(TaskFilter filter) {
        return (root, query, cb) ->
                filter.reporter() == null ? null : cb.equal(root.get("reporter"), filter.reporter());
    }

    private static Specification<TaskJpaEntity> overdueOnly(TaskFilter filter) {
        if (!filter.overdueOnly()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("dueDate")),
                cb.lessThan(root.get("dueDate"), now),
                cb.notEqual(root.get("status"), TaskStatus.DONE),
                cb.notEqual(root.get("status"), TaskStatus.CANCELLED));
    }
}
