package com.syticket.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码生成器工具类
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
public class PasswordGenerator {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 生成密码hash
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码hash
     */
    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码hash
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 获取密码编码器实例
     * 
     * @return 密码编码器
     */
    public static PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
    
    /**
     * 主方法 - 用于命令行测试
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 测试密码生成
        String password = "admin123";
        String encoded = encode(password);
        
        System.out.println("=== 密码生成器测试 ===");
        System.out.println("原始密码: " + password);
        System.out.println("生成的hash: " + encoded);
        System.out.println("验证结果: " + matches(password, encoded));
        
        // 测试数据库中的现有hash
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM0m.EQZY8Ev9V/8j3ZK";
        System.out.println("\n=== 现有密码验证 ===");
        System.out.println("数据库中的hash: " + existingHash);
        System.out.println("admin123验证结果: " + matches("admin123", existingHash));
        
        // 生成几个常用密码的hash
        System.out.println("\n=== 常用密码hash生成 ===");
        String[] passwords = {"admin123", "123456", "password", "admin"};
        for (String pwd : passwords) {
            System.out.println(pwd + " -> " + encode(pwd));
        }
    }
}
