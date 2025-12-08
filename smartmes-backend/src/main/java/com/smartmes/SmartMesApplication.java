package com.smartmes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SmartMES Lite 主应用程序入口
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.smartmes.mapper")
public class SmartMesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMesApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("SmartMES Lite Backend Started Successfully");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("===========================================\n");
    }
}
