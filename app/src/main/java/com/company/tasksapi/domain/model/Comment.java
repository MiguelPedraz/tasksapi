package com.company.tasksapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Comment(UUID id, String content, String author, LocalDateTime createdAt) {
}
