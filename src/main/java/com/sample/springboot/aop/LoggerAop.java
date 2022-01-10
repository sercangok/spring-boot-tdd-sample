package com.sample.springboot.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggerAop {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.sample.springboot.service..*(..))")
    public void logBeforeExecution(JoinPoint joinPoint) {
        String log = new StringBuilder()
                .append(joinPoint.getSignature().toString()).append("->")
                .append(Arrays.stream(joinPoint.getArgs()).collect(Collectors.toList()))
                .toString();
        logger.info(log);
    }

    @AfterThrowing(value = "execution(* com.sample.springboot.service..*(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        String log = new StringBuilder()
                .append(joinPoint.getSignature().toString()).append("->")
                .append(Arrays.stream(joinPoint.getArgs()).collect(Collectors.toList()))
                .toString();
        logger.error(log, exception);
    }
}
