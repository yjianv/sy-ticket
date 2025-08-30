package com.syticket.mapper;

import com.syticket.entity.Workspace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工作空间数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface WorkspaceMapper {
    
    /**
     * 查询所有启用的工作空间
     * 
     * @return 工作空间列表
     */
    @Select("SELECT * FROM workspaces WHERE enabled = true ORDER BY created_at ASC")
    List<Workspace> findAllEnabled();
    
    /**
     * 根据ID查找工作空间
     * 
     * @param id 工作空间ID
     * @return 工作空间信息
     */
    @Select("SELECT * FROM workspaces WHERE id = #{id}")
    Workspace findById(@Param("id") Long id);
    
    /**
     * 根据代码查找工作空间
     * 
     * @param code 工作空间代码
     * @return 工作空间信息
     */
    @Select("SELECT * FROM workspaces WHERE code = #{code} AND enabled = true")
    Workspace findByCode(@Param("code") String code);
    
    /**
     * 插入新工作空间
     * 
     * @param workspace 工作空间信息
     * @return 影响的行数
     */
    int insert(Workspace workspace);
    
    /**
     * 更新工作空间信息
     * 
     * @param workspace 工作空间信息
     * @return 影响的行数
     */
    int update(Workspace workspace);
}
