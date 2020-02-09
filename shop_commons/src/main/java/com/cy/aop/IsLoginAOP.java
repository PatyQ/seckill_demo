package com.cy.aop;

import com.cy.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

@Component
@Aspect
public class IsLoginAOP {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取到cookie:有,无
     * 判断redis中该用户是否存在:有,无
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("@annotation(IsLogin)")
    public Object isLoginAop(ProceedingJoinPoint proceedingJoinPoint){

        /**
         * 拿到当前的request请求
         * 通过当前的request请求获取到当前的cookie数组
         * 判断cookie中是否有与需求相符的值
         *根据cookie中的值取出redis中的用户对象
         *根据反射获得注解
         */
        String loginToken = null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //拿到一个httpservletrequest对象
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        //通过拿到的request请求对象获得cookie
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("loginToken")){
                    loginToken = cookie.getValue();
                }
            }
        }

        //判断redis中是否存在该用户
        User user = null;
        if (loginToken!=null){
            user =  (User) redisTemplate.opsForValue().get(loginToken);
        }

        if (user == null){
            //未登录
            //判断@IsLogin注解的方法返回值
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            //增强方法的反射对象
            Method method = methodSignature.getMethod();
            IsLogin isLogin = method.getAnnotation(IsLogin.class);
            boolean mustLogin = isLogin.mustLogin();

            //必须登录为true,获取到的用户对象为空,未登录,返回登录页面
            if (mustLogin){
    //==========================根据当前页面,登录后跳转到对应的页面=============================//
                //强制跳转到登录页面
                String returnLogin = "http://localhost/sso/gologin";
                String requestURI = httpServletRequest.getRequestURI();
                StringBuffer requestURL = httpServletRequest.getRequestURL();
                String queryString = httpServletRequest.getQueryString();
                String URLQuery = requestURL.toString()+"?"+queryString;//拼接为完整的URL
                String encode = null;
                try {
                    encode = URLEncoder.encode(URLQuery,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Object resultUrl = "http://localhost/sso/gologin?returnUrl="+encode;

                return "redirect:"+resultUrl;
            }
        }

        //如果用户不为空,则将该用户存入该线程中
        //如果登录user就不为空，如果不登录user就为空-----前置增强
        UserHolder.setUser(user);

        /**
         * 环绕增强返回本该返回的数据
         * 没有强制要求登录,该干嘛干嘛
         */
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        //清空threadlocal-----后置增强回收资源,防止再有人拿到当前线程的资源
        UserHolder.setUser(null);

        return result;
    }

}
