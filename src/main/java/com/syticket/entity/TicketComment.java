package com.syticket.entity;

import java.time.LocalDateTime;

/**
 * 工单评论实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class TicketComment {
    
    /**
     * 评论类型枚举
     */
    public enum Type {
        COMMENT("评论"),
        SYSTEM("系统");
        
        private final String description;
        
        Type(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private Long id;
    private Long ticketId;
    private Long userId;
    private String content;
    private Type type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联对象
    private User user;
    
    // 构造函数
    public TicketComment() {
    }
    
    public TicketComment(Long ticketId, Long userId, String content, Type type) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.content = content;
        this.type = type;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "TicketComment{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", userId=" + userId +
                ", type=" + type +
                '}';
    }
}
