package com.syticket.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 自定义用户详情类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class CustomUserDetails implements UserDetails {
    
    private final Long id;
    private final String username;
    private final String password;
    private final String realName;
    private final String email;
    private final String avatar;
    private final Collection<? extends GrantedAuthority> authorities;
    
    public CustomUserDetails(Long id, String username, String password, String realName, 
                           String email, String avatar, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.email = email;
        this.avatar = avatar;
        this.authorities = authorities;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
