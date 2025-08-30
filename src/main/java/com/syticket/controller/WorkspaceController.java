package com.syticket.controller;

import com.syticket.service.UserService;
import com.syticket.service.WorkspaceService;
import com.syticket.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作空间控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 切换工作空间
     * 
     * @param workspaceId 工作空间ID
     * @return 响应结果
     */
    @PostMapping("/switch/{workspaceId}")
    public ResponseEntity<Map<String, Object>> switchWorkspace(@PathVariable Long workspaceId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取当前用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证工作空间是否存在且启用
            var workspace = workspaceService.getById(workspaceId);
            if (workspace == null || !workspace.getEnabled()) {
                response.put("success", false);
                response.put("message", "工作空间不存在或已禁用");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 更新用户的默认工作空间
            boolean updated = userService.updateDefaultWorkspace(userId, workspaceId);
            if (updated) {
                response.put("success", true);
                response.put("message", "工作空间切换成功");
                response.put("workspace", workspace);
            } else {
                response.put("success", false);
                response.put("message", "工作空间切换失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
