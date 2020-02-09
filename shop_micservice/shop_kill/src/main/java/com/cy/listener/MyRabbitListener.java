package com.cy.listener;

import com.cy.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyRabbitListener {

    @Autowired
    private Configuration configuration;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "kill_queue")
    public void msgHandler(Goods goods){
        //同步商品的库存到redis中
       redisTemplate.opsForValue().set("gsave_"+goods.getId(),goods.getGoodsKill().getKillSave()+"");

       //生成的静态页面内容
        System.out.println("生成的静态页面内容 :"+ goods);

        //获得存放静态页面的路径   ---   不能直接找到/static/html文件,未被编译
        String path = MyRabbitListener.class.getResource("/").getPath()+"static/html";
//        String pathTEST = MyRabbitListener.class.getClassLoader().getResource("/").getPath()+"static/html";

        System.out.println("classpath :"+ path);
//        System.out.println("测试的classpath :"+ pathTEST);

        File file = new File(path);
        if (!file.exists()){//如该路径不存在,就创建该路径
            file.mkdirs();
        }

        file = new File(file, goods.getId()+".html");

        try (Writer out = new FileWriter(file)){
            //获得模板
            Template template = configuration.getTemplate("kill.ftlh");

            //准备数据
            Map<String,Object> map = new HashMap<>();
            map.put("goods",goods);
            template.process(map,out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
