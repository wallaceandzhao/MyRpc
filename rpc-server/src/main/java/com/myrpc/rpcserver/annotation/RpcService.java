package com.myrpc.rpcserver.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2020/3/7
 * created by chenpp
 * 引入Component注解，
 * 自定义注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

        Class ServiceClass();

        String serviceName ();
        }