package com.cy.controller;

import com.cy.Goods;
import com.cy.ResultData;
import com.cy.aop.IsLogin;
import com.cy.aop.UserHolder;
import com.cy.feign.IFeignService;
import com.cy.util.DateUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("kill")
public class KillController {

    @Autowired
    private IFeignService feignService;

    @Autowired  //验证码的实现
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String lua = "--获得参数\n" +
            "local gid = KEYS[1]\n" +
            "local gnumber = tonumber(ARGV[1])\n" +
            "local uid = ARGV[2]\n" +
            "local now = tonumber(ARGV[3])\n" +
            "\n" +
            "--获得库存\n" +
            "local gsave = tonumber(redis.call('get', 'gsave_'..gid))\n" +
            "--判断库存\n" +
            "if gsave < gnumber then\n" +
            "\t--库存不足\n" +
            "\treturn -1\n" +
            "end\n" +
            "\n" +
            "--修改库存\n" +
            "local newSave = tonumber(redis.call('decrby', 'gsave_'..gid, gnumber))\n" +
            "--记录排队位置\n" +
            "redis.call('zadd', 'paidui_'..gid, now, uid)\n" +
            "\n" +
            "--抢购成功\n" +
            "return 1";

    /**
     * 查询当前的秒杀场次
     * @return
     */
    @RequestMapping("queryKillTimes")
    @ResponseBody
    public ResultData<List<Date>> queryKillTimes(){

        List<Date> dates = new ArrayList<>();

        //获得当前时间
        Date date1 = DateUtil.getNextNDate(0);

        Date date2 = DateUtil.getNextNDate(1);

        Date date3 = DateUtil.getNextNDate(2);
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);

        return new ResultData<List<Date>>().setCode("200").setData(dates);
    }

    /**
     * 获取当前时间的秒杀商品
     * @return
     */
    @ResponseBody
    @RequestMapping("queryKillList")
    public ResultData<List<Goods>> queryKillList(Integer n){
        //获取对应的时间
        Date date = DateUtil.getNextNDate(n);
        //根据对应的时间查询当前时间的秒杀信息
        List<Goods> goodsList = feignService.selGoodsSecondKill(date);
        System.out.println(date+"时间的商品信息为"+goodsList);
        return new ResultData<List<Goods>>().setCode("200").setData(goodsList);
    }


    /**
     * 获取当前时间
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryNow")
    public ResultData<Date> queryNow(){
        return new ResultData<Date>().setCode(ResultData.ResultCodeList.OK).setData(new Date());
    }


    /**
     * 抢购
     * @return
     */
    @IsLogin(mustLogin = true)
    @RequestMapping(value = "qiangGou")
    public String rushToBuy(Integer gid ,Integer gnumber){
        if (gnumber == null || gnumber.equals("")){
            gnumber = 1;
        }
        String nickname= UserHolder.getUser().getNickname();
        System.out.println(nickname + "抢到了"+gid);
        return "success";
    }

    /**
     * 刷新并返回验证码
     * @param response
     */
    @RequestMapping("code")
    public void showCode(HttpServletResponse response){

    }


}
