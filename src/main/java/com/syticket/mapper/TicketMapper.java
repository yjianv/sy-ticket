package com.syticket.mapper;

import com.syticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 工单数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface TicketMapper {
    
    /**
     * 根据ID查找工单
     * 
     * @param id 工单ID
     * @return 工单信息（包含关联用户和工作空间信息）
     */
    Ticket findByIdWithRelations(@Param("id") Long id);
    
    /**
     * 根据工单编号查找工单
     * 
     * @param ticketNo 工单编号
     * @return 工单信息
     */
    @Select("SELECT * FROM tickets WHERE ticket_no = #{ticketNo}")
    Ticket findByTicketNo(@Param("ticketNo") String ticketNo);
    
    /**
     * 分页查询工单列表
     * 
     * @param params 查询参数
     * @return 工单列表（包含关联用户和工作空间信息）
     */
    List<Ticket> findWithConditions(@Param("params") Map<String, Object> params);
    
    /**
     * 查询用户相关的工单
     * 
     * @param userId 用户ID
     * @param workspaceId 工作空间ID
     * @return 工单列表
     */
    List<Ticket> findUserRelatedTickets(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId);
    
    /**
     * 插入新工单
     * 
     * @param ticket 工单信息
     * @return 影响的行数
     */
    int insert(Ticket ticket);
    
    /**
     * 更新工单信息
     * 
     * @param ticket 工单信息
     * @return 影响的行数
     */
    int update(Ticket ticket);
    
    /**
     * 获取下一个工单编号
     * 
     * @param workspaceCode 工作空间代码
     * @return 工单编号
     */
    String generateTicketNo(@Param("workspaceCode") String workspaceCode);
    
    /**
     * 统计工单数量
     * 
     * @param params 查询参数
     * @return 数量
     */
    int countWithConditions(@Param("params") Map<String, Object> params);
}
