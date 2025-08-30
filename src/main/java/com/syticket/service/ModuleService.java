package com.syticket.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syticket.entity.Module;
import com.syticket.mapper.ModuleMapper;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 模块服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class ModuleService {
    
    @Autowired
    private ModuleMapper moduleMapper;
    
    /**
     * 创建模块
     */
    @Transactional
    public Module create(Module module) {
        if (module == null) {
            throw new IllegalArgumentException("模块信息不能为空");
        }
        
        if (module.getName() == null || module.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("模块名称不能为空");
        }
        
        if (module.getWorkspaceId() == null) {
            throw new IllegalArgumentException("工作空间ID不能为空");
        }
        
        moduleMapper.insert(module);
        return module;
    }
    
    /**
     * 根据ID删除模块
     */
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        
        // TODO: 检查是否有工单关联此模块，如果有则不允许删除
        moduleMapper.deleteById(id);
    }
    
    /**
     * 更新模块
     */
    @Transactional
    public void update(Module module) {
        if (module == null) {
            throw new IllegalArgumentException("模块信息不能为空");
        }
        
        if (module.getId() == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        
        if (module.getName() != null && module.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("模块名称不能为空");
        }
        
        moduleMapper.update(module);
    }
    
    /**
     * 根据ID查询模块
     */
    public Module getById(Long id) {
        if (id == null) {
            return null;
        }
        return moduleMapper.selectById(id);
    }
    
    /**
     * 根据工作空间ID查询模块列表
     */
    public List<Module> getByWorkspaceId(Long workspaceId) {
        if (workspaceId == null) {
            throw new IllegalArgumentException("工作空间ID不能为空");
        }
        return moduleMapper.selectByWorkspaceId(workspaceId);
    }
    
    /**
     * 根据工作空间ID查询启用的模块列表
     */
    public List<Module> getEnabledByWorkspaceId(Long workspaceId) {
        if (workspaceId == null) {
            throw new IllegalArgumentException("工作空间ID不能为空");
        }
        return moduleMapper.selectEnabledByWorkspaceId(workspaceId);
    }
    
    /**
     * 分页查询模块
     */
    public PageInfo<Module> getModulePage(int page, int size, Long workspaceId, String name, Boolean enabled) {
        PageHelper.startPage(page, size);
        List<Module> modules = moduleMapper.selectByCondition(workspaceId, name, enabled);
        return new PageInfo<>(modules);
    }
    
    /**
     * 获取所有模块
     */
    public List<Module> getAllModules() {
        return moduleMapper.selectAll();
    }
    
    /**
     * 启用模块
     */
    @Transactional
    public void enable(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        
        Module module = new Module();
        module.setId(id);
        module.setEnabled(true);
        moduleMapper.update(module);
    }
    
    /**
     * 禁用模块
     */
    @Transactional
    public void disable(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        
        Module module = new Module();
        module.setId(id);
        module.setEnabled(false);
        moduleMapper.update(module);
    }
    
    /**
     * 更新模块排序
     */
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        if (id == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        
        if (sortOrder == null) {
            sortOrder = 0;
        }
        
        Module module = new Module();
        module.setId(id);
        module.setSortOrder(sortOrder);
        moduleMapper.update(module);
    }
}
