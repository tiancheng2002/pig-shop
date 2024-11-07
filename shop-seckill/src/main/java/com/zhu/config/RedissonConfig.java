package com.zhu.config;


import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    //从application.properties中获取spring.redis.host配置
    @Value("${spring.redis.host}")
    private String redisHost;
    //从application.properties中获取spring.redis.port配置
    @Value("${spring.redis.port}")
    private int redisPort;
//    //从application.properties中获取bloomfilter.v1参数
//    @Value("${bloomfilter.v1}")
//    private Long v1;
//    //从application.properties中获取bloomfilter.var3容错率参数
//    @Value("${bloomfilter.var3}")
//    private double var3;

    //将redissonClient配置为springBean后可以重新根据自己需要配置loomFilter
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort);
        return Redisson.create(config);
    }

    //配置默认的loomFilter
    @Bean
    public RBloomFilter<Integer> bloomFilter(){
        RBloomFilter<Integer> bloomFilter=redissonClient().getBloomFilter("seckill-goods");
        bloomFilter.tryInit(100,0.01);
        return bloomFilter;
    }
}