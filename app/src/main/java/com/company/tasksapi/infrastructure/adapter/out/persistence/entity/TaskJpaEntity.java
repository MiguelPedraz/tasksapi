package com.company.tasksapi.infrastructure.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.company.tasksapi.domain.model.Priority;
import com.company.tasksapi.domain.model.TaskStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_assigned_to", columnList = "assignedTo"),
    @Index(name = "idx_task_reporter", columnList = "reporter"),
    @Index(name = "idx_task_due_date", columnList = "dueDate")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    
    private LocalDateTime dueDate;
    
    private String assignedTo;
    
    @Column(nullable = false)
    private String reporter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private TaskJpaEntity parentTask;
    
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskJpaEntity> subtasks = new ArrayList<>();
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentJpaEntity> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentJpaEntity> attachments = new ArrayList<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
    
    public void addSubtask(TaskJpaEntity subtask) {
        subtasks.add(subtask);
        subtask.setParentTask(this);
    }
    
    public void removeSubtask(TaskJpaEntity subtask) {
        subtasks.remove(subtask);
        subtask.setParentTask(null);
    }
    
    public void addComment(CommentJpaEntity comment) {
        comments.add(comment);
        comment.setTask(this);
    }
    
    public void addAttachment(AttachmentJpaEntity attachment) {
        attachments.add(attachment);
        attachment.setTask(this);
    }
}
