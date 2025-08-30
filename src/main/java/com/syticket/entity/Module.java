package com.syticket.entity;

import java.time.LocalDateTime;

/**
 * 系统模块实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class Module {
    
    private Long id;
    private String name;
    private String description;
    private Long ownerId; // 负责人用户ID
    private Long workspaceId; // 所属工作空间ID
    private Boolean enabled;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联对象
    private User owner; // 负责人
    private Workspace workspace; // 所属工作空间
    
    // 构造函数
    public Module() {
    }
    
    public Module(String name, String description, Long ownerId, Long workspaceId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.workspaceId = workspaceId;
        this.enabled = true;
        this.sortOrder = 0;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public Long getWorkspaceId() {
        return workspaceId;
    }
    
    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public Workspace getWorkspace() {
        return workspace;
    }
    
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", workspaceId=" + workspaceId +
                ", enabled=" + enabled +
                '}';
    }
}
