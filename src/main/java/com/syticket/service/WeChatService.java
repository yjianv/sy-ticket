package com.syticket.service;

import com.syticket.entity.Ticket;
import com.syticket.entity.User;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信推送服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class WeChatService {
    
    @Value("${wechat.webhook-url}")
    private String webhookUrl;
    
    private final WebClient webClient;
    
    public WeChatService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * 发送工单创建通知
     * 
     * @param ticket 工单信息
     * @param creator 创建人信息
     */
    public void sendTicketCreatedNotification(Ticket ticket, User creator) {
        try {
            String message = buildTicketCreatedMessage(ticket, creator);
            sendMessage(message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送工单解决通知
     * 
     * @param ticket 工单信息
     * @param resolver 解决人信息
     */
    public void sendTicketResolvedNotification(Ticket ticket, User resolver) {
        try {
            String message = buildTicketResolvedMessage(ticket, resolver);
            sendMessage(message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送工单指派通知
     * 
     * @param ticket 工单信息
     * @param assignee 指派人信息
     * @param operator 操作人信息
     */
    public void sendTicketAssignedNotification(Ticket ticket, User assignee, User operator) {
        try {
            String message = buildTicketAssignedMessage(ticket, assignee, operator);
            sendMessage(message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送工单状态变更通知
     * 
     * @param ticket 工单信息
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param operator 操作人信息
     */
    public void sendTicketStatusChangedNotification(Ticket ticket, String oldStatus, String newStatus, User operator) {
        try {
            String message = buildTicketStatusChangedMessage(ticket, oldStatus, newStatus, operator);
            sendMessage(message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息到企业微信
     * 
     * @param message 消息内容
     */
    private void sendMessage(String message) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("msgtype", "text");
        
        Map<String, String> text = new HashMap<>();
        text.put("content", message);
        requestBody.put("text", text);
        
        webClient.post()
                .uri(webhookUrl)
                .header("Content-Type", "application/json")
                .body(Mono.just(JSON.toJSONString(requestBody)), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> System.out.println("企业微信通知发送成功: " + response),
                    error -> System.err.println("企业微信通知发送失败: " + error.getMessage())
                );
    }
    
    /**
     * 构建工单创建消息
     * 
     * @param ticket 工单信息
     * @param creator 创建人信息
     * @return 消息内容
     */
    private String buildTicketCreatedMessage(Ticket ticket, User creator) {
        StringBuilder message = new StringBuilder();
        message.append("📋 新工单创建通知\n\n");
        message.append("工单编号：").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题：").append(ticket.getTitle()).append("\n");
        message.append("优先级：").append(getPriorityIcon(ticket.getPriority())).append(" ").append(ticket.getPriority().getDescription()).append("\n");
        message.append("类型：").append(getTypeIcon(ticket.getType())).append(" ").append(ticket.getType().getDescription()).append("\n");
        message.append("创建人：").append(creator.getRealName()).append("\n");
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间：").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        if (ticket.getAssignee() != null) {
            message.append("指派给：").append(ticket.getAssignee().getRealName()).append("\n");
        }
        
        message.append("\n请及时处理！");
        
        return message.toString();
    }
    
    /**
     * 构建工单解决消息
     * 
     * @param ticket 工单信息
     * @param resolver 解决人信息
     * @return 消息内容
     */
    private String buildTicketResolvedMessage(Ticket ticket, User resolver) {
        StringBuilder message = new StringBuilder();
        message.append("✅ 工单解决通知\n\n");
        message.append("工单编号：").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题：").append(ticket.getTitle()).append("\n");
        message.append("解决人：").append(resolver.getRealName()).append("\n");
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间：").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        if (ticket.getResolvedAt() != null) {
            message.append("解决时间：").append(ticket.getResolvedAt().toString()).append("\n");
        }
        
        message.append("\n工单已解决，请相关人员确认！");
        
        return message.toString();
    }
    
    /**
     * 构建工单指派消息
     * 
     * @param ticket 工单信息
     * @param assignee 指派人信息
     * @param operator 操作人信息
     * @return 消息内容
     */
    private String buildTicketAssignedMessage(Ticket ticket, User assignee, User operator) {
        StringBuilder message = new StringBuilder();
        message.append("👤 工单指派通知\n\n");
        message.append("工单编号：").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题：").append(ticket.getTitle()).append("\n");
        message.append("指派人：").append(operator.getRealName()).append("\n");
        message.append("接收人：").append(assignee.getRealName()).append("\n");
        message.append("优先级：").append(getPriorityIcon(ticket.getPriority())).append(" ").append(ticket.getPriority().getDescription()).append("\n");
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间：").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        message.append("\n@").append(assignee.getRealName()).append(" 请及时处理！");
        
        return message.toString();
    }
    
    /**
     * 构建工单状态变更消息
     * 
     * @param ticket 工单信息
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param operator 操作人信息
     * @return 消息内容
     */
    private String buildTicketStatusChangedMessage(Ticket ticket, String oldStatus, String newStatus, User operator) {
        StringBuilder message = new StringBuilder();
        message.append("🔄 工单状态变更通知\n\n");
        message.append("工单编号：").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题：").append(ticket.getTitle()).append("\n");
        message.append("操作人：").append(operator.getRealName()).append("\n");
        message.append("状态变更：").append(getStatusDescription(oldStatus)).append(" → ").append(getStatusDescription(newStatus)).append("\n");
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间：").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        return message.toString();
    }
    
    /**
     * 获取优先级图标
     * 
     * @param priority 优先级
     * @return 图标
     */
    private String getPriorityIcon(Ticket.Priority priority) {
        switch (priority) {
            case URGENT:
                return "🔴";
            case HIGH:
                return "🟠";
            case MEDIUM:
                return "🟡";
            case LOW:
                return "🟢";
            default:
                return "⚪";
        }
    }
    
    /**
     * 获取类型图标
     * 
     * @param type 类型
     * @return 图标
     */
    private String getTypeIcon(Ticket.Type type) {
        switch (type) {
            case BUG:
                return "🐛";
            case FEATURE:
                return "⭐";
            case IMPROVEMENT:
                return "📈";
            case QUESTION:
                return "❓";
            default:
                return "📝";
        }
    }
    
    /**
     * 获取状态描述
     * 
     * @param status 状态
     * @return 状态描述
     */
    private String getStatusDescription(String status) {
        try {
            Ticket.Status ticketStatus = Ticket.Status.valueOf(status);
            return ticketStatus.getDescription();
        } catch (Exception e) {
            return status;
        }
    }
}
