package com.syticket.service;

import com.syticket.entity.User;
import com.syticket.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    /**
     * 获取所有启用的用户
     * 
     * @return 用户列表
     */
    public List<User> getAllEnabled() {
        return userMapper.findAllEnabled();
    }
    
    /**
     * 创建新用户
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    public User create(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新后的用户
     */
    public User update(User user) {
        // 如果密码不为空，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        userMapper.update(user);
        return findById(user.getId());
    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    public boolean isUsernameExists(String username) {
        return findByUsername(username) != null;
    }
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    public boolean changePassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        
        return userMapper.update(user) > 0;
    }
}
