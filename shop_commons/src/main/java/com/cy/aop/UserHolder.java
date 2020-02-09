package com.cy.aop;


import com.cy.User;

public class UserHolder {

    //当作一个线程的容器
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static boolean isLogin(){
        return threadLocal.get()!=null;
    }

    public static User getUser(){
        return threadLocal.get();
    }

    public static void setUser(User user){
        UserHolder.threadLocal.set(user);
    }

}
