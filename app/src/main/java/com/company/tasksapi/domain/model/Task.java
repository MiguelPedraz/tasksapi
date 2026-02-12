package com.company.tasksapi.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Task {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private String assignedTo;
    private String reporter;
    private List<UUID> subtasks;
    private List<Comment> comments;
    private List<Attachment> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    private Task(TaskBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status != null ? builder.status : TaskStatus.TODO;
        this.priority = builder.priority != null ? builder.priority : Priority.MEDIUM;
        this.dueDate = builder.dueDate;
        this.assignedTo = builder.assignedTo;
        this.reporter = builder.reporter;
        this.subtasks = builder.subtasks != null ? new ArrayList<>(builder.subtasks) : new ArrayList<>();
        this.comments = builder.comments != null ? new ArrayList<>(builder.comments) : new ArrayList<>();
        this.attachments = builder.attachments != null ? new ArrayList<>(builder.attachments) : new ArrayList<>();
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.createdBy = builder.createdBy;
        this.updatedBy = builder.updatedBy;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public String getReporter() {
        return reporter;
    }
    
    public List<UUID> getSubtasks() {
        return Collections.unmodifiableList(subtasks);
    }
    
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }
    
    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    // Business methods
    public void changeStatus(TaskStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changePriority(Priority newPriority) {
        if (newPriority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.priority = newPriority;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void assign(String assignee) {
        if (assignee == null || assignee.isBlank()) {
            throw new IllegalArgumentException("Assignee cannot be null or empty");
        }
        this.assignedTo = assignee;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }
        this.comments.add(comment);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addAttachment(Attachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }
        this.attachments.add(attachment);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addSubtask(UUID subtaskId) {
        if (subtaskId == null) {
            throw new IllegalArgumentException("Subtask ID cannot be null");
        }
        if (!this.subtasks.contains(subtaskId)) {
            this.subtasks.add(subtaskId);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void removeSubtask(UUID subtaskId) {
        this.subtasks.remove(subtaskId);
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status != TaskStatus.DONE;
    }
    
    public boolean isBlocked() {
        return status == TaskStatus.BLOCKED;
    }
    
    private void validateStatusTransition(TaskStatus current, TaskStatus next) {
        if (next == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        // Business rules for status transitions
        if (current == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status from CANCELLED");
        }
        
        if (current == TaskStatus.DONE && next != TaskStatus.TODO) {
            throw new IllegalStateException("Can only reopen DONE tasks to TODO");
        }
    }
    
    public static TaskBuilder builder() {
        return new TaskBuilder();
    }
    
    public static class TaskBuilder {
        private UUID id;
        private String title;
        private String description;
        private TaskStatus status;
        private Priority priority;
        private LocalDateTime dueDate;
        private String assignedTo;
        private String reporter;
        private List<UUID> subtasks;
        private List<Comment> comments;
        private List<Attachment> attachments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        
        public TaskBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public TaskBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        public TaskBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public TaskBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }
        
        public TaskBuilder priority(Priority priority) {
            this.priority = priority;
            return this;
        }
        
        public TaskBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }
        
        public TaskBuilder assignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }
        
        public TaskBuilder reporter(String reporter) {
            this.reporter = reporter;
            return this;
        }
        
        public TaskBuilder subtasks(List<UUID> subtasks) {
            this.subtasks = subtasks;
            return this;
        }
        
        public TaskBuilder comments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }
        
        public TaskBuilder attachments(List<Attachment> attachments) {
            this.attachments = attachments;
            return this;
        }
        
        public TaskBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public TaskBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public TaskBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }
        
        public TaskBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }
        
        public Task build() {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title is required");
            }
            return new Task(this);
        }
    }
}
