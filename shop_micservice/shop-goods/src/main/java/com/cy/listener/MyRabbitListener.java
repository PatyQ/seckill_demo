package com.cy.listener;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyRabbitListener {

    @Autowired
    private Configuration configuration;

    @Autowired
    private StringRedisTemplate redisTemplate;


}
