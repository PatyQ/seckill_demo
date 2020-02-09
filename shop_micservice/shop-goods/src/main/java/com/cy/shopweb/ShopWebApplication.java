package com.cy.shopweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.cy")
@EnableEurekaClient
//配置mapper和事务驱动
@MapperScan("com.cy.dao")
@EnableTransactionManagement
@EnableCaching
public class ShopWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopWebApplication.class, args);
    }

}
