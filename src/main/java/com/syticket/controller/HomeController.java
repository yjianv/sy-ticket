package com.syticket.controller;

import com.syticket.entity.User;
import com.syticket.entity.Workspace;
import com.syticket.service.TicketService;
import com.syticket.service.UserService;
import com.syticket.service.WorkspaceService;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 主页控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Controller
public class HomeController {
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        if (logout != null) {
            model.addAttribute("message", "您已成功退出");
        }
        return "login";
    }
    
    /**
     * 主页面
     */
    @GetMapping("/")
    public String index(@RequestParam(value = "workspaceId", required = false) Long workspaceId, Model model) {
        // 获取所有工作空间
        List<Workspace> workspaces = workspaceService.getAllEnabled();
        model.addAttribute("workspaces", workspaces);
        
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
        
        if (currentWorkspace != null) {
            // 统计数据
            int openCount = ticketService.countTickets(currentWorkspace.getId(), "OPEN");
            int inProgressCount = ticketService.countTickets(currentWorkspace.getId(), "IN_PROGRESS");
            int resolvedCount = ticketService.countTickets(currentWorkspace.getId(), "RESOLVED");
            int closedCount = ticketService.countTickets(currentWorkspace.getId(), "CLOSED");
            
            model.addAttribute("openCount", openCount);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("resolvedCount", resolvedCount);
            model.addAttribute("closedCount", closedCount);
            
            // 用户相关的工单
            if (userId != null) {
                model.addAttribute("userTickets", ticketService.getUserRelatedTickets(userId, currentWorkspace.getId()));
            }
        }
        
        return "index";
    }
    
    /**
     * 错误页面
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
