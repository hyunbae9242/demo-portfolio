package com.sample.system.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.sample.system.application..*)")
    public void applicationLayer() {}

    @Pointcut("within(com.sample.system.domain..*)")
    public void domainLayer() {}

    @Around("applicationLayer() || domainLayer()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("Executing {}.{}() with args: {}",
                className,
                methodName,
                Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("Completed {}.{}() in {}ms",
                    className,
                    methodName,
                    executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("Failed {}.{}() after {}ms: {}",
                    className,
                    methodName,
                    executionTime,
                    e.getMessage());

            throw e;
        }
    }
}
