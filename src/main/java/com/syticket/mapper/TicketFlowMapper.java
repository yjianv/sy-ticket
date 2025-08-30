package com.syticket.mapper;

import com.syticket.entity.TicketFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工单流转记录数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface TicketFlowMapper {
    
    /**
     * 根据ID查找流转记录
     * 
     * @param id 流转记录ID
     * @return 流转记录信息
     */
    @Select("SELECT * FROM ticket_flows WHERE id = #{id}")
    TicketFlow findById(@Param("id") Long id);
    
    /**
     * 根据工单ID查找流转记录列表（包含用户信息）
     * 
     * @param ticketId 工单ID
     * @return 流转记录列表
     */
    List<TicketFlow> findByTicketIdWithUser(@Param("ticketId") Long ticketId);
    
    /**
     * 插入流转记录
     * 
     * @param flow 流转记录信息
     * @return 影响的行数
     */
    int insert(TicketFlow flow);
    
    /**
     * 根据用户查找相关的流转记录
     * 
     * @param userId 用户ID
     * @return 流转记录列表
     */
    List<TicketFlow> findByUserId(@Param("userId") Long userId);
}
