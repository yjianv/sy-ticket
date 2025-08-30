package com.syticket.service;

import com.syticket.entity.Ticket;
import com.syticket.entity.Workspace;
import com.syticket.mapper.TicketMapper;
import com.syticket.util.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class TicketService {
    
    @Autowired
    private TicketMapper ticketMapper;
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private WeChatService weChatService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询工单列表
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param workspaceId 工作空间ID
     * @param status 状态
     * @param priority 优先级
     * @param type 类型
     * @param keyword 关键词
     * @return 工单分页信息
     */
    public PageInfo<Ticket> getTicketPage(int pageNum, int pageSize, Long workspaceId, 
                                         String status, String priority, String type, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("status", status);
        params.put("priority", priority);
        params.put("type", type);
        params.put("keyword", keyword);
        
        List<Ticket> tickets = ticketMapper.findWithConditions(params);
        return new PageInfo<>(tickets);
    }
    
    /**
     * 获取用户相关的工单
     * 
     * @param userId 用户ID
     * @param workspaceId 工作空间ID
     * @return 工单列表
     */
    public List<Ticket> getUserRelatedTickets(Long userId, Long workspaceId) {
        return ticketMapper.findUserRelatedTickets(userId, workspaceId);
    }
    
    /**
     * 根据ID获取工单详情
     * 
     * @param id 工单ID
     * @return 工单详情
     */
    public Ticket getById(Long id) {
        return ticketMapper.findByIdWithRelations(id);
    }
    
    /**
     * 根据工单编号获取工单
     * 
     * @param ticketNo 工单编号
     * @return 工单信息
     */
    public Ticket getByTicketNo(String ticketNo) {
        return ticketMapper.findByTicketNo(ticketNo);
    }
    
    /**
     * 创建工单
     * 
     * @param ticket 工单信息
     * @return 创建的工单
     */
    @Transactional
    public Ticket create(Ticket ticket) {
        // 设置创建人
        ticket.setCreatorId(SecurityUtils.getCurrentUserId());
        
        // 生成工单编号
        Workspace workspace = workspaceService.getById(ticket.getWorkspaceId());
        String ticketNo = ticketMapper.generateTicketNo(workspace.getCode());
        ticket.setTicketNo(ticketNo);
        
        // 设置默认状态
        ticket.setStatus(Ticket.Status.OPEN);
        
        ticketMapper.insert(ticket);
        Ticket createdTicket = getById(ticket.getId());
        
        // 发送企业微信通知
        User creator = userService.findById(ticket.getCreatorId());
        weChatService.sendTicketCreatedNotification(createdTicket, creator);
        
        return createdTicket;
    }
    
    /**
     * 更新工单
     * 
     * @param ticket 工单信息
     * @return 更新后的工单
     */
    @Transactional
    public Ticket update(Ticket ticket) {
        ticketMapper.update(ticket);
        return getById(ticket.getId());
    }
    
    /**
     * 指派工单
     * 
     * @param ticketId 工单ID
     * @param assigneeId 指派人ID
     * @return 更新后的工单
     */
    @Transactional
    public Ticket assign(Long ticketId, Long assigneeId) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setAssigneeId(assigneeId);
        ticket.setStatus(Ticket.Status.IN_PROGRESS);
        
        return update(ticket);
    }
    
    /**
     * 解决工单
     * 
     * @param ticketId 工单ID
     * @return 更新后的工单
     */
    @Transactional
    public Ticket resolve(Long ticketId) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(Ticket.Status.RESOLVED);
        ticket.setResolverId(SecurityUtils.getCurrentUserId());
        ticket.setResolvedAt(LocalDateTime.now());
        
        Ticket resolvedTicket = update(ticket);
        
        // 发送企业微信通知
        User resolver = userService.findById(SecurityUtils.getCurrentUserId());
        weChatService.sendTicketResolvedNotification(resolvedTicket, resolver);
        
        return resolvedTicket;
    }
    
    /**
     * 关闭工单
     * 
     * @param ticketId 工单ID
     * @return 更新后的工单
     */
    @Transactional
    public Ticket close(Long ticketId) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(Ticket.Status.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        
        return update(ticket);
    }
    
    /**
     * 重新打开工单
     * 
     * @param ticketId 工单ID
     * @return 更新后的工单
     */
    @Transactional
    public Ticket reopen(Long ticketId) {
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setResolvedAt(null);
        ticket.setClosedAt(null);
        
        return update(ticket);
    }
    
    /**
     * 统计工单数量
     * 
     * @param workspaceId 工作空间ID
     * @param status 状态
     * @return 工单数量
     */
    public int countTickets(Long workspaceId, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("status", status);
        
        return ticketMapper.countWithConditions(params);
    }
}
