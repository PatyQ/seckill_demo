package com.cy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.Goods;
import com.cy.GoodsImages;
import com.cy.GoodsSecondkill;
import com.cy.dao.IGoodsDao;
import com.cy.dao.IGoodsImagesDao;
import com.cy.dao.IGoodsSecondKillDao;
import com.cy.util.DateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "goods")
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private IGoodsDao goodsDao;
    @Autowired
    private IGoodsImagesDao goodsImagesDao;
    @Autowired
    private IGoodsSecondKillDao goodsSecondKillDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public List<Goods> forAddGoods(List<Goods> goods){

        for (Goods g : goods) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("gid",g.getId());
            List<GoodsImages> list = goodsImagesDao.selectList(queryWrapper);

            for (GoodsImages img : list) {
                //判断是否为封面
                if (img.getIsfengmian() == 1){
                    g.setFmUrl(img.getUrl());
                }else {
                    g.addOtherUrl(img.getUrl());
                }
            }

            //处理秒杀信息
            if (g.getType() == 2){
                GoodsSecondkill goodsSecondkill = goodsSecondKillDao.selectOne(queryWrapper);
                g.setGoodsKill(goodsSecondkill);
            }
        }

        return goods;
    }

    @Override
    @Transactional(readOnly = true)//只开启一次连接
    public List<Goods> selGoodsList() {
        List<Goods> goods = goodsDao.selectList(null);
        //添加图片之后
        List<Goods> goodsList = forAddGoods(goods);

        return goodsList;
    }


    @Override
    @Cacheable(key = "'kill_' + #date.time")
    public List<Goods> queryKillList(Date date) {

        QueryWrapper<GoodsSecondkill> secondkillQueryWrapper = new QueryWrapper<>();
        secondkillQueryWrapper.eq("start_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        List<GoodsSecondkill> goodsSecondkillList = goodsSecondKillDao.selectList(secondkillQueryWrapper);

        List<Goods> goods = new ArrayList<>();

        for (GoodsSecondkill gs : goodsSecondkillList) {
            Goods byId = goodsDao.selectById(gs.getGid());
            byId.setGoodsKill(gs);

            //查询相关图片
            QueryWrapper<GoodsImages> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("gid",gs.getGid());
            List<GoodsImages> goodsImages = goodsImagesDao.selectList(queryWrapper);
            for (GoodsImages gim : goodsImages) {
                if(gim.getIsfengmian() == 1){
                    //是封面
                    byId.setFmUrl(gim.getUrl());
                }else {
                    byId.addOtherUrl(gim.getUrl());
                }
            }
            goods.add(byId);
        }
        return goods;
    }




    @Override
    @Transactional
    @CacheEvict(key = "'kill_' + #goods.goodsKill.startTime.time", condition = "#goods.type == 2")
    public Integer insertGoods(Goods goods) {
        //添加商品
        int insert = goodsDao.insert(goods);
        //添加图片
        GoodsImages goodsImages = new GoodsImages()
                .setGid(goods.getId())
                .setUrl(goods.getFmUrl())
                .setIsfengmian(1);
        goodsImagesDao.insert(goodsImages);

        //循环添加其他图片
        for (String s : goods.getOtherUrl()) {
            GoodsImages goodsImages1 = new GoodsImages()
                    .setGid(goods.getId())
                    .setIsfengmian(0)
                    .setUrl(s);
            goodsImagesDao.insert(goodsImages1);
        }

        //TODO 添加秒杀信息
        if (goods.getType() == 2){
            //说明是秒杀商品 - 添加秒杀信息
            GoodsSecondkill goodsKill = goods.getGoodsKill();
            goodsKill.setGid(goods.getId());
            goodsSecondKillDao.insert(goodsKill);

            //将秒杀商品id放入redis集合中
            String timeSuffix = DateUtil.date2String(goodsKill.getStartTime(), "yyMMddHH");
            stringRedisTemplate.opsForSet().add("killgoods_" + timeSuffix, goods.getId() + "");
        }

        //==============通过rabbitmq添加索引库和生成静态页面==================//
        //TODO 将商品信息放入rabbitmq， 同步到索引库中,以及生成静态页面
        rabbitTemplate.convertAndSend("goods_exchange", "", goods);

        //只可成功,不可失败
        return 1;
    }


}
