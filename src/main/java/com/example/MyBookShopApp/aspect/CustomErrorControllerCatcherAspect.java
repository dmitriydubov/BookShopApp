package com.example.MyBookShopApp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class CustomErrorControllerCatcherAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.aspect.annotations.CustomErrorControllerCatchable)")
    public void customErrorControllerCatcherPointcut() {}

    @After("customErrorControllerCatcherPointcut()")
    public void customErrorControllerCatcherAdvice(JoinPoint joinPoint) {
        logger.info(
            "incorrect url entered exception caught in method " +
            joinPoint.getTarget().getClass().getSimpleName() +
            " in " +
            joinPoint.getTarget().getClass().getPackage()
        );
    }
}
