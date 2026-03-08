package com.company.tasksapi.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Task resource representation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    @Schema(description = "Task unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Task title", example = "Fix login bug")
    private String title;

    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Current task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Task priority", example = "HIGH")
    private Priority priority;

    @Schema(description = "Due date (UTC)", example = "2026-06-01T09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;

    @Schema(description = "Assigned user", example = "john.doe")
    private String assignedTo;

    @Schema(description = "Reporter username", example = "jane.smith")
    private String reporter;

    @Schema(description = "IDs of subtasks")
    private List<UUID> subtaskIds;

    @Schema(description = "Number of comments", example = "3")
    private Integer commentCount;

    @Schema(description = "Number of attachments", example = "1")
    private Integer attachmentCount;

    @Schema(description = "Whether the task is past its due date")
    private boolean overdue;

    @Schema(description = "Creation timestamp (UTC)", example = "2026-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp (UTC)", example = "2026-03-08T14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created this task", example = "admin")
    private String createdBy;

    @Schema(description = "User who last updated this task", example = "john.doe")
    private String updatedBy;
}
