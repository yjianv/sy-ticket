package com.syticket.service;

import com.syticket.entity.TicketComment;
import com.syticket.mapper.TicketCommentMapper;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工单评论服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class TicketCommentService {
    
    @Autowired
    private TicketCommentMapper commentMapper;
    
    /**
     * 根据工单ID获取评论列表
     * 
     * @param ticketId 工单ID
     * @return 评论列表
     */
    public List<TicketComment> getCommentsByTicketId(Long ticketId) {
        return commentMapper.findByTicketIdWithUser(ticketId);
    }
    
    /**
     * 根据ID获取评论
     * 
     * @param id 评论ID
     * @return 评论信息
     */
    public TicketComment getById(Long id) {
        return commentMapper.findById(id);
    }
    
    /**
     * 创建评论
     * 
     * @param ticketId 工单ID
     * @param content 评论内容
     * @return 创建的评论
     */
    @Transactional
    public TicketComment create(Long ticketId, String content) {
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setUserId(SecurityUtils.getCurrentUserId());
        comment.setContent(content);
        comment.setType(TicketComment.Type.COMMENT);
        
        commentMapper.insert(comment);
        return getById(comment.getId());
    }
    
    /**
     * 创建系统评论
     * 
     * @param ticketId 工单ID
     * @param content 评论内容
     * @return 创建的评论
     */
    @Transactional
    public TicketComment createSystemComment(Long ticketId, String content) {
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setUserId(SecurityUtils.getCurrentUserId());
        comment.setContent(content);
        comment.setType(TicketComment.Type.SYSTEM);
        
        commentMapper.insert(comment);
        return comment;
    }
    
    /**
     * 更新评论
     * 
     * @param commentId 评论ID
     * @param content 新内容
     * @return 更新后的评论
     */
    @Transactional
    public TicketComment update(Long commentId, String content) {
        TicketComment comment = getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查权限：只能编辑自己的评论
        if (!comment.getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("无权限编辑此评论");
        }
        
        comment.setContent(content);
        commentMapper.update(comment);
        
        return getById(commentId);
    }
    
    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     */
    @Transactional
    public void delete(Long commentId) {
        TicketComment comment = getById(commentId);
        if (comment == null) {
            return;
        }
        
        // 检查权限：只能删除自己的评论
        if (!comment.getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("无权限删除此评论");
        }
        
        commentMapper.deleteById(commentId);
    }
    
    /**
     * 统计工单评论数量
     * 
     * @param ticketId 工单ID
     * @return 评论数量
     */
    public int countByTicketId(Long ticketId) {
        return commentMapper.countByTicketId(ticketId);
    }
}
