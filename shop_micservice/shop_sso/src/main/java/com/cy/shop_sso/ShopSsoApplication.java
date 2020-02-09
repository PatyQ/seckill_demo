package com.cy.shop_sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cy")
@MapperScan("com.cy.dao")
public class ShopSsoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopSsoApplication.class, args);
    }

}
