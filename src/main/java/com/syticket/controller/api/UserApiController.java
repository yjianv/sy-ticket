package com.syticket.controller.api;

import com.syticket.entity.User;
import com.syticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 API 控制器
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<User> users = userService.searchUsers(keyword, limit);
            
            result.put("success", true);
            result.put("users", users);
            result.put("total", users.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索用户失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(result);
        }
    }
}
