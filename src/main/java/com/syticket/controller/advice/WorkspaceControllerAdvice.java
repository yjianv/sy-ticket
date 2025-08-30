package com.syticket.controller.advice;

import com.syticket.entity.User;
import com.syticket.entity.Workspace;
import com.syticket.service.UserService;
import com.syticket.service.WorkspaceService;
import com.syticket.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 工作空间控制器增强
 * 统一处理工作空间相关的Model属性
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@ControllerAdvice
public class WorkspaceControllerAdvice {
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 为所有需要工作空间信息的页面添加通用属性
     * 排除登录页面、错误页面等不需要工作空间信息的页面
     */
    @ModelAttribute
    public void addWorkspaceAttributes(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        // 排除不需要工作空间信息的页面
        if (shouldSkipWorkspaceSetup(requestURI)) {
            return;
        }
        
        // 获取工作空间列表
        List<Workspace> workspaces = workspaceService.getAllEnabled();
        model.addAttribute("workspaces", workspaces);
        
        // 获取URL中的workspaceId参数
        String workspaceIdParam = request.getParameter("workspaceId");
        Long workspaceId = null;
        if (workspaceIdParam != null && !workspaceIdParam.trim().isEmpty()) {
            try {
                workspaceId = Long.parseLong(workspaceIdParam);
            } catch (NumberFormatException e) {
                // 忽略无效的workspaceId参数
            }
        }
        
        // 获取当前用户的默认工作空间偏好
        Long userDefaultWorkspaceId = null;
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            User currentUser = userService.findById(userId);
            if (currentUser != null) {
                userDefaultWorkspaceId = currentUser.getDefaultWorkspaceId();
            }
        }
        
        // 确定当前工作空间（优先级：URL参数 > 用户偏好 > 第一个可用的工作空间）
        Workspace currentWorkspace = workspaceService.getCurrentWorkspace(workspaceId, userDefaultWorkspaceId, workspaces);
        model.addAttribute("currentWorkspace", currentWorkspace);
    }
    
    /**
     * 判断是否应该跳过工作空间设置
     * 
     * @param requestURI 请求URI
     * @return 是否跳过
     */
    private boolean shouldSkipWorkspaceSetup(String requestURI) {
        // 排除登录页面
        if ("/login".equals(requestURI)) {
            return true;
        }
        
        // 排除错误页面
        if (requestURI.startsWith("/error")) {
            return true;
        }
        
        // 排除API接口（如果有的话）
        if (requestURI.startsWith("/api/")) {
            return true;
        }
        
        // 排除静态资源
        if (requestURI.startsWith("/static/") || 
            requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/")) {
            return true;
        }
        
        return false;
    }
}
