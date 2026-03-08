package com.company.tasksapi.domain.model;

public record TaskFilter(
        TaskStatus status,
        Priority priority,
        String assignedTo,
        String reporter,
        boolean overdueOnly) {

    public static TaskFilter empty() {
        return new TaskFilter(null, null, null, null, false);
    }
}
