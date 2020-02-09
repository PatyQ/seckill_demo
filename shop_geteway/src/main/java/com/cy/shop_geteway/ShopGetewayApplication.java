package com.cy.shop_geteway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ShopGetewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopGetewayApplication.class, args);
    }

}
