package com.cy.aop;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsLogin {

    @AliasFor("mustLogin")
    boolean value()default false;

    @AliasFor("value")
    boolean mustLogin()default false;
}
