package com.cy.feign;

import com.cy.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@FeignClient("MICSERVICE-GOODS")
public interface IFeignService {

    /**
     * 添加商品
     * @param goods
     * @return
     */
    @RequestMapping("goods/insert")
    int insertGoods(@RequestBody Goods goods);

    /**
     * 查询所有商品
     * @return
     */
    @RequestMapping("goods/selGoodsList")
    List<Goods> selGoodsList();

    /**
     * 根据时间查询秒杀商品
     * @param date
     * @return
     */
    @RequestMapping("goods/queryKillList")
    List<Goods> selGoodsSecondKill(@RequestBody Date date); //@RequestParam("date") 失败

}
