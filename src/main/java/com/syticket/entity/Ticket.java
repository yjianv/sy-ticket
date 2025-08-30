package com.syticket.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工单实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class Ticket {
    
    /**
     * 优先级枚举
     */
    public enum Priority {
        LOW("低"),
        MEDIUM("中"),
        HIGH("高"),
        URGENT("紧急");
        
        private final String description;
        
        Priority(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 状态枚举
     */
    public enum Status {
        OPEN("待处理"),
        IN_PROGRESS("处理中"),
        RESOLVED("已解决"),
        CLOSED("已关闭"),
        CANCELLED("已取消");
        
        private final String description;
        
        Status(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 类型枚举
     */
    public enum Type {
        BUG("缺陷"),
        FEATURE("功能"),
        IMPROVEMENT("改进"),
        QUESTION("问题"),
        OTHER("其他");
        
        private final String description;
        
        Type(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private Long id;
    private String title;
    private String content;
    private String ticketNo;
    private Priority priority;
    private Status status;
    private Type type;
    private Long workspaceId;
    private Long creatorId;
    private Long assigneeId;
    private Long resolverId;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联对象
    private Workspace workspace;
    private User creator;
    private User assignee;
    private User resolver;
    
    // 构造函数
    public Ticket() {
    }
    
    public Ticket(String title, String content, Priority priority, Type type, Long workspaceId, Long creatorId) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.type = type;
        this.workspaceId = workspaceId;
        this.creatorId = creatorId;
        this.status = Status.OPEN;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getTicketNo() {
        return ticketNo;
    }
    
    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public Long getWorkspaceId() {
        return workspaceId;
    }
    
    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public Long getAssigneeId() {
        return assigneeId;
    }
    
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
    
    public Long getResolverId() {
        return resolverId;
    }
    
    public void setResolverId(Long resolverId) {
        this.resolverId = resolverId;
    }
    
    public BigDecimal getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(BigDecimal estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public BigDecimal getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(BigDecimal actualHours) {
        this.actualHours = actualHours;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
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
    
    public Workspace getWorkspace() {
        return workspace;
    }
    
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public User getAssignee() {
        return assignee;
    }
    
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }
    
    public User getResolver() {
        return resolver;
    }
    
    public void setResolver(User resolver) {
        this.resolver = resolver;
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
