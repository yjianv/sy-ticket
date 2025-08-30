package com.syticket.mapper;

import com.syticket.entity.TicketComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工单评论数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface TicketCommentMapper {
    
    /**
     * 根据ID查找评论
     * 
     * @param id 评论ID
     * @return 评论信息
     */
    @Select("SELECT * FROM ticket_comments WHERE id = #{id}")
    TicketComment findById(@Param("id") Long id);
    
    /**
     * 根据工单ID查找评论列表（包含用户信息）
     * 
     * @param ticketId 工单ID
     * @return 评论列表
     */
    List<TicketComment> findByTicketIdWithUser(@Param("ticketId") Long ticketId);
    
    /**
     * 插入评论
     * 
     * @param comment 评论信息
     * @return 影响的行数
     */
    int insert(TicketComment comment);
    
    /**
     * 更新评论
     * 
     * @param comment 评论信息
     * @return 影响的行数
     */
    int update(TicketComment comment);
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @return 影响的行数
     */
    @Select("DELETE FROM ticket_comments WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计工单评论数量
     * 
     * @param ticketId 工单ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM ticket_comments WHERE ticket_id = #{ticketId}")
    int countByTicketId(@Param("ticketId") Long ticketId);
}
