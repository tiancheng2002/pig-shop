package com.zhu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
//每个RabbitTemplate只能配置一个ReturnCallback
//消息到达了交换机但是没有到达队列
//bean工厂的通知
public class CommonConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.error("消息发送到队列失败，响应码{}，失败原因{}，交换机{}，路由key{}，消息{}",
                    returnedMessage.getReplyCode(), returnedMessage.getMessage(), returnedMessage.getExchange(), returnedMessage.getRoutingKey(), returnedMessage.getReplyText());
            //这里可以进行重发消息的业务
        });
    }
}