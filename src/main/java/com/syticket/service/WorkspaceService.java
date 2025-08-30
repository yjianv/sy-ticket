package com.syticket.service;

import com.syticket.entity.Workspace;
import com.syticket.mapper.WorkspaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作空间服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class WorkspaceService {
    
    @Autowired
    private WorkspaceMapper workspaceMapper;
    
    /**
     * 获取所有启用的工作空间
     * 
     * @return 工作空间列表
     */
    public List<Workspace> getAllEnabled() {
        return workspaceMapper.findAllEnabled();
    }
    
    /**
     * 根据ID获取工作空间
     * 
     * @param id 工作空间ID
     * @return 工作空间信息
     */
    public Workspace getById(Long id) {
        return workspaceMapper.findById(id);
    }
    
    /**
     * 根据代码获取工作空间
     * 
     * @param code 工作空间代码
     * @return 工作空间信息
     */
    public Workspace getByCode(String code) {
        return workspaceMapper.findByCode(code);
    }
    
    /**
     * 创建工作空间
     * 
     * @param workspace 工作空间信息
     * @return 创建的工作空间
     */
    public Workspace create(Workspace workspace) {
        workspace.setEnabled(true);
        workspaceMapper.insert(workspace);
        return workspace;
    }
    
    /**
     * 更新工作空间
     * 
     * @param workspace 工作空间信息
     * @return 更新后的工作空间
     */
    public Workspace update(Workspace workspace) {
        workspaceMapper.update(workspace);
        return getById(workspace.getId());
    }
    
    /**
     * 获取默认工作空间（测试环境）
     * 
     * @return 默认工作空间
     */
    public Workspace getDefaultWorkspace() {
        return getByCode("TEST");
    }
}
