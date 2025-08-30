package com.syticket.entity;

import java.time.LocalDateTime;

/**
 * 文件实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class FileEntity {
    
    /**
     * 关联类型枚举
     */
    public enum RelatedType {
        TICKET("工单"),
        COMMENT("评论"),
        AVATAR("头像"),
        OTHER("其他");
        
        private final String description;
        
        RelatedType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private Long id;
    private String originalName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String fileHash;
    private Long uploaderId;
    private RelatedType relatedType;
    private Long relatedId;
    private LocalDateTime createdAt;
    
    // 关联对象
    private User uploader;
    
    // 构造函数
    public FileEntity() {
    }
    
    public FileEntity(String originalName, String storedName, String filePath, Long fileSize, String mimeType, Long uploaderId, RelatedType relatedType, Long relatedId) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.uploaderId = uploaderId;
        this.relatedType = relatedType;
        this.relatedId = relatedId;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getStoredName() {
        return storedName;
    }
    
    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getFileHash() {
        return fileHash;
    }
    
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    
    public Long getUploaderId() {
        return uploaderId;
    }
    
    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }
    
    public RelatedType getRelatedType() {
        return relatedType;
    }
    
    public void setRelatedType(RelatedType relatedType) {
        this.relatedType = relatedType;
    }
    
    public Long getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUploader() {
        return uploader;
    }
    
    public void setUploader(User uploader) {
        this.uploader = uploader;
    }
    
    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", originalName='" + originalName + '\'' +
                ", storedName='" + storedName + '\'' +
                ", fileSize=" + fileSize +
                ", relatedType=" + relatedType +
                '}';
    }
}
