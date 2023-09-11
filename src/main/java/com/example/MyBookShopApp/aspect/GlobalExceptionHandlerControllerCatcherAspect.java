package com.example.MyBookShopApp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Aspect
@Component
public class GlobalExceptionHandlerControllerCatcherAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.example.MyBookShopApp.errors.controller.GlobalExceptionHandlerController)")
    public void globalExceptionControllerMethodsPointcut() {}

    @Around("globalExceptionControllerMethodsPointcut()")
    public Object globalExceptionControllerMethodsHandleAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length < 2 || !(args[1] instanceof RedirectAttributes)) return joinPoint.proceed();
        RedirectAttributes redirectAttributes = (RedirectAttributes) args[1];

        Exception ex = (Exception) args[0];
        redirectAttributes.addFlashAttribute("errorMessage", ex.getLocalizedMessage());
        log(ex, joinPoint);

        return joinPoint.proceed();
    }

    private void log(Object ex, ProceedingJoinPoint joinPoint) {
        logger.info(
            ex.getClass() + " is caught in " +
            joinPoint.getTarget().getClass().getPackage()
        );
    }
}
