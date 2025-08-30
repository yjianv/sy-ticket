package com.syticket.util;

import com.syticket.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security 工具类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class SecurityUtils {
    
    /**
     * 获取当前登录用户详情
     * 
     * @return 当前用户详情
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * 获取当前登录用户ID
     * 
     * @return 当前用户ID
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }
    
    /**
     * 获取当前登录用户名
     * 
     * @return 当前用户名
     */
    public static String getCurrentUsername() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getUsername() : null;
    }
    
    /**
     * 获取当前登录用户真实姓名
     * 
     * @return 当前用户真实姓名
     */
    public static String getCurrentUserRealName() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getRealName() : null;
    }
    
    /**
     * 检查是否已经登录
     * 
     * @return 是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
