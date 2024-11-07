package com.zhu.rate;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    //限制对应的接口
    String key() default "rate_limit";

    //限流时间
    int time() default 60;

    //限流次数
    int count() default 100;

    //限流类型
    LimitType limitType() default LimitType.DEFAULT;

}
