package com.company.tasksapi.domain.model;

import java.util.List;

public record TaskPage(
        List<Task> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
