package com.syticket.service;

import com.syticket.entity.Ticket;
import com.syticket.entity.TicketFlow;
import com.syticket.entity.User;
import com.syticket.mapper.TicketFlowMapper;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工单流转服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class TicketFlowService {
    
    @Autowired
    private TicketFlowMapper flowMapper;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketCommentService commentService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 根据工单ID获取流转记录列表
     * 
     * @param ticketId 工单ID
     * @return 流转记录列表
     */
    public List<TicketFlow> getFlowsByTicketId(Long ticketId) {
        return flowMapper.findByTicketIdWithUser(ticketId);
    }
    
    /**
     * 根据用户ID获取相关流转记录
     * 
     * @param userId 用户ID
     * @return 流转记录列表
     */
    public List<TicketFlow> getFlowsByUserId(Long userId) {
        return flowMapper.findByUserId(userId);
    }
    
    /**
     * 创建流转记录
     * 
     * @param ticketId 工单ID
     * @param toUserId 接收人ID
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @param action 操作类型
     * @param reason 流转原因
     * @return 创建的流转记录
     */
    @Transactional
    public TicketFlow createFlow(Long ticketId, Long toUserId, String fromStatus, 
                                String toStatus, String action, String reason) {
        
        TicketFlow flow = new TicketFlow();
        flow.setTicketId(ticketId);
        flow.setFromUserId(SecurityUtils.getCurrentUserId());
        flow.setToUserId(toUserId);
        flow.setFromStatus(fromStatus);
        flow.setToStatus(toStatus);
        flow.setAction(action);
        flow.setReason(reason);
        
        flowMapper.insert(flow);
        
        // 创建系统评论记录流转信息
        String commentContent = buildFlowComment(action, fromStatus, toStatus, reason, toUserId);
        commentService.createSystemComment(ticketId, commentContent);
        
        return flow;
    }
    
    /**
     * 工单移交
     * 
     * @param ticketId 工单ID
     * @param toUserId 接收人ID
     * @param reason 移交原因
     * @return 更新后的工单
     */
    @Transactional
    public Ticket transferTicket(Long ticketId, Long toUserId, String reason) {
        Ticket ticket = ticketService.getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        
        // 创建流转记录
        createFlow(ticketId, toUserId, ticket.getStatus().name(), 
                  Ticket.Status.IN_PROGRESS.name(), "TRANSFER", reason);
        
        // 更新工单指派人和状态
        return ticketService.assign(ticketId, toUserId);
    }
    
    /**
     * 工单状态流转
     * 
     * @param ticketId 工单ID
     * @param newStatus 新状态
     * @param reason 流转原因
     * @return 更新后的工单
     */
    @Transactional
    public Ticket changeStatus(Long ticketId, Ticket.Status newStatus, String reason) {
        Ticket ticket = ticketService.getById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        
        String oldStatus = ticket.getStatus().name();
        String action = getActionByStatus(newStatus);
        
        // 创建流转记录
        createFlow(ticketId, SecurityUtils.getCurrentUserId(), oldStatus, 
                  newStatus.name(), action, reason);
        
        // 更新工单状态
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticketId);
        updatedTicket.setStatus(newStatus);
        
        // 根据状态设置相应的时间和人员
        switch (newStatus) {
            case RESOLVED:
                return ticketService.resolve(ticketId);
            case CLOSED:
                return ticketService.close(ticketId);
            case OPEN:
                return ticketService.reopen(ticketId);
            default:
                return ticketService.update(updatedTicket);
        }
    }
    
    /**
     * 构建流转评论内容
     * 
     * @param action 操作类型
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @param reason 原因
     * @param toUserId 接收人ID
     * @return 评论内容
     */
    private String buildFlowComment(String action, String fromStatus, String toStatus, 
                                   String reason, Long toUserId) {
        StringBuilder content = new StringBuilder();
        String currentUserName = SecurityUtils.getCurrentUserRealName();
        
        switch (action) {
            case "TRANSFER":
                User toUser = userService.findById(toUserId);
                content.append(String.format("【%s】将工单移交给【%s】", 
                    currentUserName, toUser != null ? toUser.getRealName() : "未知用户"));
                break;
            case "RESOLVE":
                content.append(String.format("【%s】解决了工单", currentUserName));
                break;
            case "CLOSE":
                content.append(String.format("【%s】关闭了工单", currentUserName));
                break;
            case "REOPEN":
                content.append(String.format("【%s】重新打开了工单", currentUserName));
                break;
            case "ASSIGN":
                User assignee = userService.findById(toUserId);
                content.append(String.format("【%s】指派工单给【%s】", 
                    currentUserName, assignee != null ? assignee.getRealName() : "未知用户"));
                break;
            default:
                content.append(String.format("【%s】更新了工单状态：%s -> %s", 
                    currentUserName, fromStatus, toStatus));
        }
        
        if (reason != null && !reason.trim().isEmpty()) {
            content.append("，原因：").append(reason);
        }
        
        return content.toString();
    }
    
    /**
     * 根据状态获取操作类型
     * 
     * @param status 状态
     * @return 操作类型
     */
    private String getActionByStatus(Ticket.Status status) {
        switch (status) {
            case RESOLVED:
                return "RESOLVE";
            case CLOSED:
                return "CLOSE";
            case OPEN:
                return "REOPEN";
            case IN_PROGRESS:
                return "START";
            case CANCELLED:
                return "CANCEL";
            default:
                return "UPDATE";
        }
    }
}
