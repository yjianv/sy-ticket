package com.syticket.mapper;

import com.syticket.entity.Module;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块Mapper接口
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface ModuleMapper {
    
    /**
     * 插入模块
     */
    int insert(Module module);
    
    /**
     * 根据ID删除模块
     */
    int deleteById(Long id);
    
    /**
     * 更新模块
     */
    int update(Module module);
    
    /**
     * 根据ID查询模块
     */
    Module selectById(Long id);
    
    /**
     * 根据工作空间ID查询模块列表
     */
    List<Module> selectByWorkspaceId(@Param("workspaceId") Long workspaceId);
    
    /**
     * 根据工作空间ID查询启用的模块列表
     */
    List<Module> selectEnabledByWorkspaceId(@Param("workspaceId") Long workspaceId);
    
    /**
     * 查询所有模块
     */
    List<Module> selectAll();
    
    /**
     * 根据条件查询模块列表
     */
    List<Module> selectByCondition(@Param("workspaceId") Long workspaceId,
                                  @Param("name") String name,
                                  @Param("enabled") Boolean enabled);
}
