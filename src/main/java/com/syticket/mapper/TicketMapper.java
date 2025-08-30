package com.syticket.mapper;

import com.syticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
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
     * 查询用户相关的工单（限制数量）
     * 
     * @param userId 用户ID
     * @param workspaceId 工作空间ID
     * @param limit 限制数量
     * @return 工单列表
     */
    List<Ticket> findUserRelatedTicketsWithLimit(@Param("userId") Long userId, @Param("workspaceId") Long workspaceId, @Param("limit") Integer limit);
    
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
    
    // ===== 统计报表相关方法 =====
    
    /**
     * 获取工单总数
     */
    int getTotalCount(@Param("workspaceId") Long workspaceId);
    
    /**
     * 按状态统计工单数量
     */
    int getCountByStatus(@Param("workspaceId") Long workspaceId, @Param("status") String status);
    
    /**
     * 获取今日新增工单数
     */
    int getTodayNewCount(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取逾期工单数
     */
    int getOverDueCount(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取状态分布统计
     */
    List<Map<String, Object>> getStatusDistribution(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取优先级分布统计
     */
    List<Map<String, Object>> getPriorityDistribution(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取类型分布统计
     */
    List<Map<String, Object>> getTypeDistribution(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取创建趋势数据
     */
    List<Map<String, Object>> getCreationTrend(@Param("workspaceId") Long workspaceId, @Param("days") int days);
    
    /**
     * 获取用户工作量统计
     */
    List<Map<String, Object>> getUserWorkload(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取平均处理时间
     */
    BigDecimal getAverageProcessTime(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取平均解决时间
     */
    BigDecimal getAverageResolveTime(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取最快解决时间
     */
    BigDecimal getMinResolveTime(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取最慢解决时间
     */
    BigDecimal getMaxResolveTime(@Param("workspaceId") Long workspaceId);
    
    /**
     * 获取工作空间分布统计
     */
    List<Map<String, Object>> getWorkspaceDistribution();
}
