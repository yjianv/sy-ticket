package com.syticket.controller.api;

import com.syticket.entity.Ticket;
import com.syticket.entity.TicketComment;
import com.syticket.service.TicketCommentService;
import com.syticket.service.TicketFlowService;
import com.syticket.service.TicketService;
import com.syticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 工单API控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketApiController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketCommentService commentService;
    
    @Autowired
    private TicketFlowService flowService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 添加评论
     * 
     * @param ticketId 工单ID
     * @param request 评论请求
     * @return 添加结果
     */
    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long ticketId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "评论内容不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            TicketComment comment = commentService.create(ticketId, content.trim());
            
            result.put("success", true);
            result.put("message", "评论添加成功");
            result.put("data", comment);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "评论添加失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 指派工单
     * 
     * @param ticketId 工单ID
     * @param request 指派请求
     * @return 指派结果
     */
    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<Map<String, Object>> assignTicket(@PathVariable Long ticketId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long assigneeId = Long.valueOf(request.get("assigneeId").toString());
            
            Ticket ticket = ticketService.assign(ticketId, assigneeId);
            
            result.put("success", true);
            result.put("message", "工单指派成功");
            result.put("data", ticket);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "工单指派失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 变更工单状态
     * 
     * @param ticketId 工单ID
     * @param request 状态变更请求
     * @return 变更结果
     */
    @PostMapping("/{ticketId}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable Long ticketId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String statusStr = request.get("status");
            String reason = request.get("reason");
            
            if (statusStr == null || statusStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "状态不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            Ticket.Status newStatus = Ticket.Status.valueOf(statusStr.trim().toUpperCase());
            Ticket ticket = flowService.changeStatus(ticketId, newStatus, reason);
            
            result.put("success", true);
            result.put("message", "状态更新成功");
            result.put("data", ticket);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的状态值");
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 移交工单
     * 
     * @param ticketId 工单ID
     * @param request 移交请求
     * @return 移交结果
     */
    @PostMapping("/{ticketId}/transfer")
    public ResponseEntity<Map<String, Object>> transferTicket(@PathVariable Long ticketId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long toUserId = Long.valueOf(request.get("toUserId").toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : "";
            
            Ticket ticket = flowService.transferTicket(ticketId, toUserId, reason);
            
            result.put("success", true);
            result.put("message", "工单移交成功");
            result.put("data", ticket);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "工单移交失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 获取工单详情
     * 
     * @param ticketId 工单ID
     * @return 工单详情
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<Map<String, Object>> getTicketDetail(@PathVariable Long ticketId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket == null) {
                result.put("success", false);
                result.put("message", "工单不存在");
                return ResponseEntity.notFound().build();
            }
            
            result.put("success", true);
            result.put("data", ticket);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取工单详情失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 删除评论
     * 
     * @param ticketId 工单ID
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{ticketId}/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long ticketId, @PathVariable Long commentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            commentService.delete(commentId);
            
            result.put("success", true);
            result.put("message", "评论删除成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "评论删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 更新评论
     * 
     * @param ticketId 工单ID
     * @param commentId 评论ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{ticketId}/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable Long ticketId, 
                                                           @PathVariable Long commentId, 
                                                           @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "评论内容不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            TicketComment comment = commentService.update(commentId, content.trim());
            
            result.put("success", true);
            result.put("message", "评论更新成功");
            result.put("data", comment);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "评论更新失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
