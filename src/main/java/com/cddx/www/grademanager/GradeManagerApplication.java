package com.cddx.www.grademanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用入口。
 *
 * 功能项：
 * - 启动 Web 服务（内嵌 Tomcat）
 * - 扫描并注册 Spring 组件（Controller/Service 等）
 * - 扫描 MyBatis Mapper 接口，用于数据库访问
 */
@SpringBootApplication
@MapperScan("com.cddx.www.grademanager.mapper")
public class GradeManagerApplication {
    public static void main(String[] args) {
        // 启动 Spring Boot 应用并初始化 IOC 容器
        SpringApplication.run(GradeManagerApplication.class, args);
    }
}
