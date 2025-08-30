package com.syticket.entity;

import java.time.LocalDateTime;

/**
 * 工单流转记录实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class TicketFlow {
    
    private Long id;
    private Long ticketId;
    private Long fromUserId;
    private Long toUserId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private String reason;
    private LocalDateTime createdAt;
    
    // 关联对象
    private User fromUser;
    private User toUser;
    
    // 构造函数
    public TicketFlow() {
    }
    
    public TicketFlow(Long ticketId, Long fromUserId, Long toUserId, String fromStatus, String toStatus, String action) {
        this.ticketId = ticketId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.action = action;
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
    
    public Long getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public Long getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    
    public String getFromStatus() {
        return fromStatus;
    }
    
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    public String getToStatus() {
        return toStatus;
    }
    
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getFromUser() {
        return fromUser;
    }
    
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    
    public User getToUser() {
        return toUser;
    }
    
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    
    @Override
    public String toString() {
        return "TicketFlow{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", action='" + action + '\'' +
                ", fromStatus='" + fromStatus + '\'' +
                ", toStatus='" + toStatus + '\'' +
                '}';
    }
}
