package com.syticket.service;

import com.alibaba.fastjson2.JSON;
import com.syticket.entity.Ticket;
import com.syticket.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信应用消息推送服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class WeChatAppService {
    
    @Value("${wechat.app.corpid}")
    private String corpId;
    
    @Value("${wechat.app.corpsecret}")
    private String corpSecret;
    
    @Value("${wechat.app.agentid}")
    private String agentId;
    
    private final WebClient webClient;
    
    public WeChatAppService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * 发送工单认领通知
     * 
     * @param ticket 工单信息
     * @param assignee 认领人信息
     * @param operator 操作人信息
     */
    public void sendTicketClaimNotification(Ticket ticket, User assignee, User operator) {
        try {
            if (assignee.getWechatUserId() == null || assignee.getWechatUserId().trim().isEmpty()) {
                System.out.println("用户 " + assignee.getRealName() + " 未配置企业微信UserId，跳过消息推送");
                return;
            }
            
            String message = buildTicketClaimMessage(ticket, assignee, operator);
            sendAppMessage(assignee.getWechatUserId(), "工单认领通知", message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信工单认领通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送工单移交通知
     * 
     * @param ticket 工单信息
     * @param fromUser 移交人信息
     * @param toUser 接收人信息
     * @param reason 移交原因
     */
    public void sendTicketTransferNotification(Ticket ticket, User fromUser, User toUser, String reason) {
        try {
            if (toUser.getWechatUserId() == null || toUser.getWechatUserId().trim().isEmpty()) {
                System.out.println("用户 " + toUser.getRealName() + " 未配置企业微信UserId，跳过消息推送");
                return;
            }
            
            String message = buildTicketTransferMessage(ticket, fromUser, toUser, reason);
            sendAppMessage(toUser.getWechatUserId(), "工单移交通知", message);
        } catch (Exception e) {
            // 记录日志但不影响主要业务流程
            System.err.println("发送企业微信工单移交通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送企业微信应用消息
     * 
     * @param toUser 接收人的企业微信UserId
     * @param title 消息标题
     * @param content 消息内容
     */
    private void sendAppMessage(String toUser, String title, String content) {
        try {
            // 1. 获取access_token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                System.err.println("获取企业微信access_token失败");
                return;
            }
            
            // 2. 构建消息体
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("touser", toUser);
            messageBody.put("msgtype", "textcard");
            messageBody.put("agentid", Integer.parseInt(agentId));
            
            Map<String, Object> textcard = new HashMap<>();
            textcard.put("title", title);
            textcard.put("description", content);
            textcard.put("url", ""); // 可以设置点击后跳转的URL
            textcard.put("btntxt", "查看详情");
            
            messageBody.put("textcard", textcard);
            
            // 3. 发送消息
            String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken;
            
            webClient.post()
                    .uri(sendUrl)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(JSON.toJSONString(messageBody)), String.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                        response -> {
                            Map responseMap = JSON.parseObject(response, Map.class);
                            Integer errcode = (Integer) responseMap.get("errcode");
                            if (errcode == 0) {
                                System.out.println("企业微信应用消息发送成功: " + response);
                            } else {
                                System.err.println("企业微信应用消息发送失败: " + response);
                            }
                        },
                        error -> System.err.println("企业微信应用消息发送失败: " + error.getMessage())
                    );
                    
        } catch (Exception e) {
            System.err.println("发送企业微信应用消息异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取企业微信access_token
     * 
     * @return access_token
     */
    private String getAccessToken() {
        try {
            String tokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret;
            
            return webClient.get()
                    .uri(tokenUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        Map responseMap = JSON.parseObject(response, Map.class);
                        Integer errcode = (Integer) responseMap.get("errcode");
                        if (errcode == 0) {
                            return (String) responseMap.get("access_token");
                        } else {
                            System.err.println("获取access_token失败: " + response);
                            return null;
                        }
                    })
                    .block(); // 同步等待结果
                    
        } catch (Exception e) {
            System.err.println("获取企业微信access_token异常: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 构建工单认领消息
     * 
     * @param ticket 工单信息
     * @param assignee 认领人信息
     * @param operator 操作人信息
     * @return 消息内容
     */
    private String buildTicketClaimMessage(Ticket ticket, User assignee, User operator) {
        StringBuilder message = new StringBuilder();
        message.append("🎯 您有新的工单待处理\n\n");
        message.append("工单编号: ").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题: ").append(ticket.getTitle()).append("\n");
        message.append("优先级: ").append(getPriorityIcon(ticket.getPriority())).append(" ").append(ticket.getPriority().getDescription()).append("\n");
        message.append("类型: ").append(getTypeIcon(ticket.getType())).append(" ").append(ticket.getType().getDescription()).append("\n");
        
        if (!assignee.getId().equals(operator.getId())) {
            message.append("指派人: ").append(operator.getRealName()).append("\n");
        }
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间: ").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        if (ticket.getDueDate() != null) {
            message.append("截止时间: ").append(ticket.getDueDate().toString()).append("\n");
        }
        
        message.append("\n请及时处理此工单！");
        
        return message.toString();
    }
    
    /**
     * 构建工单移交消息
     * 
     * @param ticket 工单信息
     * @param fromUser 移交人信息
     * @param toUser 接收人信息
     * @param reason 移交原因
     * @return 消息内容
     */
    private String buildTicketTransferMessage(Ticket ticket, User fromUser, User toUser, String reason) {
        StringBuilder message = new StringBuilder();
        message.append("🔄 工单已移交给您\n\n");
        message.append("工单编号: ").append(ticket.getTicketNo()).append("\n");
        message.append("工单标题: ").append(ticket.getTitle()).append("\n");
        message.append("移交人: ").append(fromUser.getRealName()).append("\n");
        message.append("优先级: ").append(getPriorityIcon(ticket.getPriority())).append(" ").append(ticket.getPriority().getDescription()).append("\n");
        message.append("类型: ").append(getTypeIcon(ticket.getType())).append(" ").append(ticket.getType().getDescription()).append("\n");
        
        if (reason != null && !reason.trim().isEmpty()) {
            message.append("移交原因: ").append(reason).append("\n");
        }
        
        if (ticket.getWorkspace() != null) {
            message.append("工作空间: ").append(ticket.getWorkspace().getName()).append("\n");
        }
        
        if (ticket.getDueDate() != null) {
            message.append("截止时间: ").append(ticket.getDueDate().toString()).append("\n");
        }
        
        message.append("\n请接收并处理此工单！");
        
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
}
