package com.cy.shop_rureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ShopRurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopRurekaApplication.class, args);
    }

}
