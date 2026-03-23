package com.example.project.handler.aspect;

import com.example.project.utils.LogMaskUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Controller 请求日志切面
 * <p>记录每个接口的入参、耗时</p>
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* com.example.project.controller..*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String className  = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        Object[] args     = point.getArgs();

        long start = System.currentTimeMillis();
        log.info("[{}#{}] 入参: {}", className, methodName, LogMaskUtil.maskArgs(args));

        Object result = point.proceed();

        long cost = System.currentTimeMillis() - start;
        log.info("[{}#{}] 耗时: {}ms", className, methodName, cost);

        return result;
    }
}
