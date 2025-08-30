package com.syticket.controller;

import com.github.pagehelper.PageInfo;
import com.syticket.entity.*;
import com.syticket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @Autowired
    private FileService fileService;
    
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
        
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理）
        Workspace currentWorkspace = (Workspace) model.getAttribute("currentWorkspace");
        workspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        
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
        
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理，但工单详情页优先使用工单关联的工作空间）
        Long workspaceId = ticket.getWorkspaceId();
        Workspace ticketWorkspace = workspaceService.getById(workspaceId);
        if (ticketWorkspace != null && ticketWorkspace.getEnabled()) {
            model.addAttribute("currentWorkspace", ticketWorkspace);
        }
        // 如果工单关联的工作空间无效，则使用WorkspaceControllerAdvice设置的默认工作空间
        
        // 获取评论列表
        List<TicketComment> comments = commentService.getCommentsByTicketId(id);
        model.addAttribute("comments", comments);
        
        // 获取流转记录
        List<TicketFlow> flows = flowService.getFlowsByTicketId(id);
        model.addAttribute("flows", flows);
        
        // 获取用户列表（用于指派）
        List<User> users = userService.getAllEnabled();
        model.addAttribute("users", users);
        
        // 获取工单附件
        List<FileEntity> attachments = fileService.getFilesByRelated(FileEntity.RelatedType.TICKET, id);
        model.addAttribute("attachments", attachments);
        
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
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理）
        Workspace currentWorkspace = (Workspace) model.getAttribute("currentWorkspace");
        
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
                              @RequestParam(required = false) String attachmentIds,
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
            
            // 处理附件关联
            if (attachmentIds != null && !attachmentIds.trim().isEmpty()) {
                List<Long> fileIds = Arrays.stream(attachmentIds.split(","))
                    .filter(id -> !id.trim().isEmpty())
                    .map(id -> Long.parseLong(id.trim()))
                    .collect(Collectors.toList());
                
                if (!fileIds.isEmpty()) {
                    fileService.updateFilesRelation(fileIds, createdTicket.getId());
                }
            }
            
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
        
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理，但编辑页优先使用工单关联的工作空间）
        Long workspaceId = ticket.getWorkspaceId();
        Workspace ticketWorkspace = workspaceService.getById(workspaceId);
        if (ticketWorkspace != null && ticketWorkspace.getEnabled()) {
            model.addAttribute("currentWorkspace", ticketWorkspace);
        }
        // 如果工单关联的工作空间无效，则使用WorkspaceControllerAdvice设置的默认工作空间
        
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
