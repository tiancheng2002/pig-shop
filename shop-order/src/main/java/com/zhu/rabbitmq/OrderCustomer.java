package com.zhu.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.constant.MQConstant;
import com.zhu.pojo.Order;
import com.zhu.service.IOrderService;
import com.zhu.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderCustomer {

    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = MQConstant.OrderQueue)
    public void order(List<Order> orders) {
        System.out.println(orders);
        orderService.saveBatch(orders);
//        return orderService.list(new QueryWrapper<Order>().orderByDesc("oid").last("limit "+orders.size()));
    }

    //监听订单状态变更
    @RabbitListener(queues = MQConstant.OrderStatus)
    public void orderStatus(OrderVo orderVo) {
        //修改订单的状态为指定值
        orderService.update(new UpdateWrapper<Order>()
                .setSql("status = " + orderVo.getStatus())
                .eq("oid", orderVo.getOid()));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.DLQueue, durable = "true"),
            exchange = @Exchange(name = MQConstant.DLDirect),
            key = "dl"
    ))
    public void orderTime(List<Order> orders) {
        log.info("消息收到了");
        //判断订单是否已经超过对应的时间
        for (Order order : orders) {
            Order orderById = orderService.getById(order.getOid());
            if (orderById.getStatus() == 0) {
                orderService.update(new UpdateWrapper<Order>()
                        .setSql("status = 5")
                        .eq("oid", order.getOid()));
            }
        }
    }

}
