package com.syticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 工单系统主应用程序入口
 * 
 * @author sy-ticket
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.syticket.mapper")
public class TicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }
}
