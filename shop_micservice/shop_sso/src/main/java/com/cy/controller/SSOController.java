package com.cy.controller;

import com.cy.ResultData;
import com.cy.User;
import com.cy.service.ISSOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//单点登录
@Controller
@RequestMapping("sso")
public class SSOController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ISSOServer ssoServer;

    @RequestMapping("toregister")
    public String toregister(){
        return "register";
    }

    @RequestMapping("gologin")
    public String gologin(){
        return "login";
    }

    /**
     * 判断用户是否登录
     * @param loginToken
     * @param callback
     * @return
     */
    @RequestMapping("islogin")
    @ResponseBody
    public ResultData<User> isLogin(@CookieValue(value = "login_token",required = false)String loginToken, String callback){
        System.out.println("获得的token的值"+loginToken);
        System.out.println("callback的值"+callback);

        ResultData<User> resultData = new ResultData<>();
        if (loginToken != null){
            User user= (User) redisTemplate.opsForValue().get(loginToken);
            if (user != null){
                resultData.setCode(ResultData.ResultCodeList.OK).setMsg("成了").setData(user);
            }else{
                resultData.setCode("500");
            }
        }
        return resultData;
    }

    /**
     * 用户登录
     * @param user
     * @param response
     * @param returnUrl
     * @return
     */
    @RequestMapping("login")
    public String userLogin(User user, HttpServletResponse response,String returnUrl){
        System.out.println("登录接收到的用户信息"+user);
        if (returnUrl == null || returnUrl.equals("")){
            returnUrl = "http://localhost/";
        }
        if (user != null){
            User user2 = ssoServer.isThisUser(user.getUsername());
            boolean tf = user2.getPassword().equals(user.getPassword());
            if (tf){
                //登录成功--将用户信息放入cookie和redis中
                String token = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set(token,user2);
                redisTemplate.expire(token, 7, TimeUnit.DAYS);//忘了设置redis时间

                Cookie cookie = new Cookie("login_token",token);
//                cookie.setDomain();
                cookie.setMaxAge(60 * 60 * 24 * 7);
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:" + returnUrl;
            }
        }
        return "login";
    }

}
