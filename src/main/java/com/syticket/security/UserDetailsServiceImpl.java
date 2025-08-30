package com.syticket.security;

import com.syticket.entity.User;
import com.syticket.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务实现类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (!user.getEnabled()) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        // 所有用户都拥有相同的角色权限
        return new CustomUserDetails(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRealName(),
            user.getEmail(),
            user.getAvatar(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
