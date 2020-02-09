package com.cy.controller;

import com.cy.Goods;
import com.cy.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 添加商品
     * @param goods
     * @return
     */
    @RequestMapping("insert")
    public int insertGoods(@RequestBody Goods goods){
        System.out.println("添加-接收到的商品"+goods);
        Integer result = goodsService.insertGoods(goods);
        return result;
    }

    /**
     * 查询商品列表
     * @return
     */
    @RequestMapping("selGoodsList")
    public List<Goods> selGoodsList(){
        return goodsService.selGoodsList();
    }

    /**
     * 查询秒杀商品
     * @param date
     * @return
     */
    @RequestMapping("queryKillList")
    public List<Goods> queryKillList(@RequestBody Date date){
        System.out.println("根据时间查询秒杀商品"+date);
        return goodsService.queryKillList(date);
    }



}
