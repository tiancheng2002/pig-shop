package com.zhu.config;

import com.zhu.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue starQueue(){
        return new Queue(MQConstant.StarQueue);
    }

    @Bean
    public TopicExchange shopTopic(){
        return new TopicExchange(MQConstant.ShopTopic);
    }

    @Bean
    public Queue cartQueue(){
        return new Queue(MQConstant.CartQueue);
    }

    @Bean
    public Queue cartDelQueue(){
        return new Queue(MQConstant.CartDelQueue);
    }

    @Bean
    public Queue orderQueue(){
        return new Queue(MQConstant.OrderQueue);
    }

    @Bean
    public Queue goodsQueue(){
        return new Queue(MQConstant.GoodsQueue);
    }

    @Bean
    public Queue OrderStatusQueue(){
        return new Queue(MQConstant.OrderStatus);
    }

    @Bean
    public Queue OrderTimeQueue(){
        return QueueBuilder
                .durable(MQConstant.OrderTimeQueue)
                .ttl(900000)
                .deadLetterExchange(MQConstant.DLDirect)
                .deadLetterRoutingKey("dl")
                .build();
    }

    @Bean
    public Binding DLBinding(){
        return BindingBuilder.bind(OrderTimeQueue()).to(shopTopic()).with("shop.orderTime.#");
    }

    @Bean
    public Binding cartBinding(){
        return BindingBuilder.bind(cartQueue()).to(shopTopic()).with("shop.cartAction.#");
    }

    @Bean
    public Binding cartDelBinding(){
        return BindingBuilder.bind(cartDelQueue()).to(shopTopic()).with("shop.cartDel.#");
    }

    @Bean
    public Binding starBinding(){
        return BindingBuilder.bind(starQueue()).to(shopTopic()).with("shop.star.#");
    }

    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(orderQueue()).to(shopTopic()).with("shop.order.#");
    }

    @Bean
    public Binding goodsBinding(){
        return BindingBuilder.bind(goodsQueue()).to(shopTopic()).with("shop.goods.#");
    }

    @Bean
    public Queue SecKillQueue(){
        return new Queue(MQConstant.SecKillQueue);
    }

    @Bean
    public Binding SecKillBinding(){
        return BindingBuilder.bind(SecKillQueue()).to(shopTopic()).with("shop.secKill.#");
    }

    @Bean
    public Binding OrderStatusBinding(){
        return BindingBuilder.bind(OrderStatusQueue()).to(shopTopic()).with("shop.orderStatus.#");
    }

}
