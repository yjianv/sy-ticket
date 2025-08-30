package com.syticket.mapper;

import com.syticket.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(@Param("id") Long id);
    
    /**
     * 查询所有启用的用户
     * 
     * @return 用户列表
     */
    List<User> findAllEnabled();
    
    /**
     * 插入新用户
     * 
     * @param user 用户信息
     * @return 影响的行数
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响的行数
     */
    int update(User user);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(@Param("email") String email);
}
