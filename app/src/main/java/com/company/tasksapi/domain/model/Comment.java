package com.company.tasksapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private UUID id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    
    private Comment(CommentBuilder builder) {
        this.id = builder.id;
        this.content = builder.content;
        this.author = builder.author;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public static CommentBuilder builder() {
        return new CommentBuilder();
    }
    
    public static class CommentBuilder {
        private UUID id;
        private String content;
        private String author;
        private LocalDateTime createdAt;
        
        public CommentBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public CommentBuilder content(String content) {
            this.content = content;
            return this;
        }
        
        public CommentBuilder author(String author) {
            this.author = author;
            return this;
        }
        
        public CommentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Comment build() {
            return new Comment(this);
        }
    }
}
