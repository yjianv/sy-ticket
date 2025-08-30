package com.syticket.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class User {
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private String realName;
    private String avatar;
    private String phone;
    private Boolean enabled;
    private Long defaultWorkspaceId; // 用户默认工作空间ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public User() {
    }
    
    public User(String username, String password, String email, String realName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.realName = realName;
        this.enabled = true;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Long getDefaultWorkspaceId() {
        return defaultWorkspaceId;
    }
    
    public void setDefaultWorkspaceId(Long defaultWorkspaceId) {
        this.defaultWorkspaceId = defaultWorkspaceId;
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
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
