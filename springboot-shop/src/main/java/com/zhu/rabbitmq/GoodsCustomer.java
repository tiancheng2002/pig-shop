package com.zhu.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.constant.MQConstant;
import com.zhu.pojo.Goods;
import com.zhu.pojo.Order;
import com.zhu.service.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GoodsCustomer {

    @Autowired
    private IGoodsService goodsService;

    @RabbitListener(queues = MQConstant.GoodsQueue)
    public void action(List<Order> orders){
        for (Order order:orders){
            goodsService.update(new UpdateWrapper<Goods>()
                    .eq("gid",order.getGid())
                    .setSql("count = count - "+order.getCount())
                    .setSql("buy = buy + "+order.getCount()));
        }
    }

}
