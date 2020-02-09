package com.cy.controller;

import com.cy.Goods;
import com.cy.GoodsSecondkill;
import com.cy.ResultData;
import com.cy.feign.IFeignService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("goods")
public class GoodsController {

    //图片上传路径
    private String uploadPath = "D:/Test/testimg";

    @Autowired
    private IFeignService feignService;

    @RequestMapping("list")
    public String selGoodsList(ModelMap map){

        List<Goods> goodsList = feignService.selGoodsList();
        map.put("goodsList",goodsList);
        return "goodslist";
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @RequestMapping("uploader")
    @ResponseBody
    public ResultData<String> uploader(MultipartFile file){

        //存放路径加随机的uuid名
        String fileName = UUID.randomUUID().toString();
        String path = uploadPath + "/" + fileName;

        try(
                InputStream inputStream = file.getInputStream();
                OutputStream outputStream = new FileOutputStream(path);
                ) {
            IOUtils.copy(inputStream, outputStream);
            return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setData(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR);
    }

    /**
     * 图片回显
     * @param imgPath
     * @param response
     */
    @RequestMapping("showimg")
    @ResponseBody
    public void showImage(String imgPath,HttpServletResponse response){

        try(
                InputStream inputStream = new FileInputStream(imgPath);
                OutputStream outputStream = response.getOutputStream();
        ) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加商品
     * @return
     */
    @RequestMapping("insert")
    public String insertGoods(Goods goods, GoodsSecondkill goodsSecondkill){
        goods.setGoodsKill(goodsSecondkill);
        int i = feignService.insertGoods(goods);
        System.out.println(i);
        return "redirect:http://localhost/back/goods/list";
    }

}
