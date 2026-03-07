package com.company.tasksapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Attachment(UUID id, String filename, String fileUrl, String contentType, Long fileSize, String uploadedBy,
        LocalDateTime uploadedAt) {
}
