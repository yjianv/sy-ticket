package com.syticket.controller;

import com.syticket.entity.Ticket;
import com.syticket.entity.TicketComment;
import com.syticket.entity.TicketFlow;
import com.syticket.entity.User;
import com.syticket.entity.Workspace;
import com.syticket.service.*;
import com.syticket.util.SecurityUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 工单页面控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Controller
@RequestMapping("/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TicketCommentService commentService;
    
    @Autowired
    private TicketFlowService flowService;
    
    /**
     * 工单列表页面
     */
    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "1") int page,
                      @RequestParam(value = "size", defaultValue = "20") int size,
                      @RequestParam(value = "workspaceId", required = false) Long workspaceId,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "priority", required = false) String priority,
                      @RequestParam(value = "type", required = false) String type,
                      @RequestParam(value = "keyword", required = false) String keyword,
                      Model model) {
        
        // 获取工作空间列表
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
        workspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        model.addAttribute("currentWorkspace", currentWorkspace);
        
        // 分页查询工单
        PageInfo<Ticket> pageInfo = ticketService.getTicketPage(page, size, workspaceId, status, priority, type, keyword);
        model.addAttribute("pageInfo", pageInfo);
        
        // 查询参数
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        
        // 枚举值
        model.addAttribute("priorities", Ticket.Priority.values());
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("types", Ticket.Type.values());
        
        return "tickets/list";
    }
    
    /**
     * 工单详情页面
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return "error/404";
        }
        
        model.addAttribute("ticket", ticket);
        
        // 获取工作空间列表（用于左侧菜单显示）
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
        
        // 确定当前工作空间（优先使用工单关联的工作空间）
        Long workspaceId = ticket.getWorkspaceId();
        Workspace currentWorkspace = workspaceService.getCurrentWorkspace(workspaceId, userDefaultWorkspaceId, workspaces);
        model.addAttribute("currentWorkspace", currentWorkspace);
        
        // 获取评论列表
        List<TicketComment> comments = commentService.getCommentsByTicketId(id);
        model.addAttribute("comments", comments);
        
        // 获取流转记录
        List<TicketFlow> flows = flowService.getFlowsByTicketId(id);
        model.addAttribute("flows", flows);
        
        // 获取用户列表（用于指派）
        List<User> users = userService.getAllEnabled();
        model.addAttribute("users", users);
        
        // 枚举值
        model.addAttribute("priorities", Ticket.Priority.values());
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("types", Ticket.Type.values());
        
        return "tickets/detail";
    }
    
    /**
     * 创建工单页面
     */
    @GetMapping("/create")
    public String create(@RequestParam(value = "workspaceId", required = false) Long workspaceId, Model model) {
        // 获取工作空间列表
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
        
        // 枚举值
        model.addAttribute("priorities", Ticket.Priority.values());
        model.addAttribute("types", Ticket.Type.values());
        
        // 设置默认工作空间ID（用于表单隐藏字段）
        Long defaultWorkspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        if (workspaceId != null) {
            defaultWorkspaceId = workspaceId;
        }
        model.addAttribute("defaultWorkspaceId", defaultWorkspaceId);
        
        return "tickets/create";
    }
    
    /**
     * 提交创建工单
     */
    @PostMapping("/create")
    public String submitCreate(@RequestParam String title,
                              @RequestParam String content,
                              @RequestParam String priority,
                              @RequestParam String type,
                              @RequestParam Long workspaceId,
                              @RequestParam(required = false) String estimatedHours,
                              RedirectAttributes redirectAttributes) {
        
        try {
            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setContent(content);
            ticket.setPriority(Ticket.Priority.valueOf(priority));
            ticket.setType(Ticket.Type.valueOf(type));
            ticket.setWorkspaceId(workspaceId);
            
            if (estimatedHours != null && !estimatedHours.trim().isEmpty()) {
                ticket.setEstimatedHours(new BigDecimal(estimatedHours));
            }
            
            Ticket createdTicket = ticketService.create(ticket);
            redirectAttributes.addFlashAttribute("message", "工单创建成功");
            return "redirect:/tickets/" + createdTicket.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "工单创建失败: " + e.getMessage());
            return "redirect:/tickets/create";
        }
    }
    
    /**
     * 编辑工单页面
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return "error/404";
        }
        
        model.addAttribute("ticket", ticket);
        
        // 获取工作空间列表（用于左侧菜单显示和编辑选择）
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
        
        // 确定当前工作空间（优先使用工单关联的工作空间）
        Long workspaceId = ticket.getWorkspaceId();
        Workspace currentWorkspace = workspaceService.getCurrentWorkspace(workspaceId, userDefaultWorkspaceId, workspaces);
        model.addAttribute("currentWorkspace", currentWorkspace);
        
        // 获取用户列表
        List<User> users = userService.getAllEnabled();
        model.addAttribute("users", users);
        
        // 枚举值
        model.addAttribute("priorities", Ticket.Priority.values());
        model.addAttribute("statuses", Ticket.Status.values());
        model.addAttribute("types", Ticket.Type.values());
        
        return "tickets/edit";
    }
    
    /**
     * 提交编辑工单
     */
    @PostMapping("/{id}/edit")
    public String submitEdit(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String content,
                            @RequestParam String priority,
                            @RequestParam String type,
                            @RequestParam(required = false) Long assigneeId,
                            @RequestParam(required = false) String estimatedHours,
                            @RequestParam(required = false) String actualHours,
                            @RequestParam(required = false) String dueDate,
                            RedirectAttributes redirectAttributes) {
        
        try {
            Ticket ticket = new Ticket();
            ticket.setId(id);
            ticket.setTitle(title);
            ticket.setContent(content);
            ticket.setPriority(Ticket.Priority.valueOf(priority));
            ticket.setType(Ticket.Type.valueOf(type));
            ticket.setAssigneeId(assigneeId);
            
            if (estimatedHours != null && !estimatedHours.trim().isEmpty()) {
                ticket.setEstimatedHours(new BigDecimal(estimatedHours));
            }
            
            if (actualHours != null && !actualHours.trim().isEmpty()) {
                ticket.setActualHours(new BigDecimal(actualHours));
            }
            
            if (dueDate != null && !dueDate.trim().isEmpty()) {
                ticket.setDueDate(LocalDateTime.parse(dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            }
            
            ticketService.update(ticket);
            redirectAttributes.addFlashAttribute("message", "工单更新成功");
            return "redirect:/tickets/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "工单更新失败: " + e.getMessage());
            return "redirect:/tickets/" + id + "/edit";
        }
    }
}
