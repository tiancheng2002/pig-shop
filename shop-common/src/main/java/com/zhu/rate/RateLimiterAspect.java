package com.zhu.rate;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisScript<Long> limitScript;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        String key = rateLimiter.key();
        int count = rateLimiter.count();
        int time = rateLimiter.time();

        String combineKey = getCombineKey(point, rateLimiter);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (number == null || number.intValue() > count) {
                throw new RuntimeException("访问过于频繁，请稍后再试！");
            }
            log.info("限流请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), key);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("服务器限流异常，请稍后再试！");
        }

    }

    public String getCombineKey(JoinPoint point, RateLimiter rateLimiter) {
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());
        //如果是对IP进行限制，就需要一些操作
//        if(rateLimiter.limitType()== LimitType.IP){
//            stringBuffer.append()
//        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName());
        return stringBuffer.toString();
    }

}
