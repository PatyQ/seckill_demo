package com.cy.service;

import com.cy.Goods;

import java.util.Date;
import java.util.List;

public interface IGoodsService {

    List<Goods> selGoodsList();

    List<Goods> queryKillList(Date date);

    Integer insertGoods(Goods goods);
}
