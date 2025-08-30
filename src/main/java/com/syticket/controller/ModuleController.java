package com.syticket.controller;

import com.github.pagehelper.PageInfo;
import com.syticket.entity.Module;
import com.syticket.entity.User;
import com.syticket.entity.Workspace;
import com.syticket.service.ModuleService;
import com.syticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 模块管理控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Controller
@RequestMapping("/modules")
public class ModuleController {
    
    @Autowired
    private ModuleService moduleService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 模块列表页面
     */
    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "1") int page,
                      @RequestParam(value = "size", defaultValue = "20") int size,
                      @RequestParam(value = "name", required = false) String name,
                      @RequestParam(value = "enabled", required = false) Boolean enabled,
                      Model model) {
        
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理）
        Workspace currentWorkspace = (Workspace) model.getAttribute("currentWorkspace");
        Long workspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        
        // 分页查询模块
        PageInfo<Module> pageInfo = moduleService.getModulePage(page, size, workspaceId, name, enabled);
        model.addAttribute("pageInfo", pageInfo);
        
        // 查询参数
        model.addAttribute("currentPage", page);
        model.addAttribute("name", name);
        model.addAttribute("enabled", enabled);
        
        return "modules/list";
    }
    
    /**
     * 模块详情页面
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Module module = moduleService.getById(id);
        if (module == null) {
            return "error/404";
        }
        
        model.addAttribute("module", module);
        
        return "modules/detail";
    }
    
    /**
     * 创建模块页面
     */
    @GetMapping("/create")
    public String create(Model model) {
        // 获取当前工作空间（由WorkspaceControllerAdvice统一处理）
        Workspace currentWorkspace = (Workspace) model.getAttribute("currentWorkspace");
        
        // 获取用户列表（用于选择负责人）
        List<User> users = userService.getAllEnabled();
        model.addAttribute("users", users);
        
        // 设置默认工作空间ID（用于表单隐藏字段）
        Long defaultWorkspaceId = currentWorkspace != null ? currentWorkspace.getId() : null;
        model.addAttribute("defaultWorkspaceId", defaultWorkspaceId);
        
        return "modules/create";
    }
    
    /**
     * 提交创建模块
     */
    @PostMapping("/create")
    public String submitCreate(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Long ownerId,
                              @RequestParam Long workspaceId,
                              @RequestParam(value = "enabled", defaultValue = "true") Boolean enabled,
                              @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder,
                              RedirectAttributes redirectAttributes) {
        
        try {
            Module module = new Module();
            module.setName(name);
            module.setDescription(description);
            module.setOwnerId(ownerId);
            module.setWorkspaceId(workspaceId);
            module.setEnabled(enabled);
            module.setSortOrder(sortOrder);
            
            Module createdModule = moduleService.create(module);
            
            redirectAttributes.addFlashAttribute("message", "模块创建成功");
            return "redirect:/modules/" + createdModule.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "模块创建失败: " + e.getMessage());
            return "redirect:/modules/create";
        }
    }
    
    /**
     * 编辑模块页面
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Module module = moduleService.getById(id);
        if (module == null) {
            return "error/404";
        }
        
        model.addAttribute("module", module);
        
        // 获取用户列表（用于选择负责人）
        List<User> users = userService.getAllEnabled();
        model.addAttribute("users", users);
        
        return "modules/edit";
    }
    
    /**
     * 提交编辑模块
     */
    @PostMapping("/{id}/edit")
    public String submitEdit(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Long ownerId,
                            @RequestParam(value = "enabled", defaultValue = "true") Boolean enabled,
                            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder,
                            RedirectAttributes redirectAttributes) {
        
        try {
            Module module = new Module();
            module.setId(id);
            module.setName(name);
            module.setDescription(description);
            module.setOwnerId(ownerId);
            module.setEnabled(enabled);
            module.setSortOrder(sortOrder);
            
            moduleService.update(module);
            redirectAttributes.addFlashAttribute("message", "模块更新成功");
            return "redirect:/modules/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "模块更新失败: " + e.getMessage());
            return "redirect:/modules/" + id + "/edit";
        }
    }
    
    /**
     * 删除模块
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "模块删除成功");
            return "redirect:/modules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "模块删除失败: " + e.getMessage());
            return "redirect:/modules";
        }
    }
    
    /**
     * 启用模块
     */
    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.enable(id);
            redirectAttributes.addFlashAttribute("message", "模块启用成功");
            return "redirect:/modules/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "模块启用失败: " + e.getMessage());
            return "redirect:/modules/" + id;
        }
    }
    
    /**
     * 禁用模块
     */
    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduleService.disable(id);
            redirectAttributes.addFlashAttribute("message", "模块禁用成功");
            return "redirect:/modules/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "模块禁用失败: " + e.getMessage());
            return "redirect:/modules/" + id;
        }
    }
}
