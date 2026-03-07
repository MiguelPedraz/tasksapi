package com.company.tasksapi.infrastructure.adapter.out.persistence;

import com.company.tasksapi.domain.model.Attachment;
import com.company.tasksapi.domain.model.Comment;
import com.company.tasksapi.domain.model.Task;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.AttachmentJpaEntity;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.CommentJpaEntity;
import com.company.tasksapi.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;

public class TaskEntityMapper {

    private TaskEntityMapper() {
    }

    public static Task toDomain(TaskJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .dueDate(entity.getDueDate())
                .assignedTo(entity.getAssignedTo())
                .reporter(entity.getReporter())
                .subtasks(entity.getSubtasks().stream()
                        .map(TaskJpaEntity::getId)
                        .toList())
                .comments(entity.getComments().stream()
                        .map(TaskEntityMapper::commentToDomain)
                        .toList())
                .attachments(entity.getAttachments().stream()
                        .map(TaskEntityMapper::attachmentToDomain)
                        .toList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
    
    public static TaskJpaEntity toEntity(Task domain) {
        if (domain == null) {
            return null;
        }
        
        TaskJpaEntity entity = TaskJpaEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .priority(domain.getPriority())
                .dueDate(domain.getDueDate())
                .assignedTo(domain.getAssignedTo())
                .reporter(domain.getReporter())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
        
        // Map comments
        domain.getComments().forEach(comment -> {
            CommentJpaEntity commentEntity = commentToEntity(comment);
            entity.addComment(commentEntity);
        });
        
        // Map attachments
        domain.getAttachments().forEach(attachment -> {
            AttachmentJpaEntity attachmentEntity = attachmentToEntity(attachment);
            entity.addAttachment(attachmentEntity);
        });
        
        return entity;
    }
    
    private static Comment commentToDomain(CommentJpaEntity entity) {
        return new Comment(entity.getId(), entity.getContent(), entity.getAuthor(), entity.getCreatedAt());
    }
    
    private static CommentJpaEntity commentToEntity(Comment domain) {
        return CommentJpaEntity.builder()
                .id(domain.id())
                .content(domain.content())
                .author(domain.author())
                .createdAt(domain.createdAt())
                .build();
    }
    
    private static Attachment attachmentToDomain(AttachmentJpaEntity entity) {
        return new Attachment(entity.getId(), entity.getFilename(), entity.getFileUrl(),
                entity.getContentType(), entity.getFileSize(), entity.getUploadedBy(), entity.getUploadedAt());
    }
    
    private static AttachmentJpaEntity attachmentToEntity(Attachment domain) {
        return AttachmentJpaEntity.builder()
                .id(domain.id())
                .filename(domain.filename())
                .fileUrl(domain.fileUrl())
                .contentType(domain.contentType())
                .fileSize(domain.fileSize())
                .uploadedBy(domain.uploadedBy())
                .uploadedAt(domain.uploadedAt())
                .build();
    }
}
