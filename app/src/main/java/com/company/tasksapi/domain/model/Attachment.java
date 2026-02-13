package com.company.tasksapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Attachment {
    private UUID id;
    private String filename;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    
    private Attachment(AttachmentBuilder builder) {
        this.id = builder.id;
        this.filename = builder.filename;
        this.fileUrl = builder.fileUrl;
        this.contentType = builder.contentType;
        this.fileSize = builder.fileSize;
        this.uploadedBy = builder.uploadedBy;
        this.uploadedAt = builder.uploadedAt != null ? builder.uploadedAt : LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public static AttachmentBuilder builder() {
        return new AttachmentBuilder();
    }
    
    public static class AttachmentBuilder {
        private UUID id;
        private String filename;
        private String fileUrl;
        private String contentType;
        private Long fileSize;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        
        public AttachmentBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public AttachmentBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }
        
        public AttachmentBuilder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }
        
        public AttachmentBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }
        
        public AttachmentBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }
        
        public AttachmentBuilder uploadedBy(String uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }
        
        public AttachmentBuilder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }
        
        public Attachment build() {
            return new Attachment(this);
        }
    }
}
