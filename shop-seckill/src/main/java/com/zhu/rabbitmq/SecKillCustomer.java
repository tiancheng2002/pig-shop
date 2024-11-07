package com.zhu.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.constant.MQConstant;
import com.zhu.pojo.Order;
import com.zhu.pojo.SeckillGoods;
import com.zhu.pojo.SeckillOrder;
import com.zhu.pojo.User;
import com.zhu.service.ISeckillGoodsService;
import com.zhu.service.ISeckillOrderService;
import com.zhu.utils.OidUtils;
import com.zhu.vo.MessageVo;
import com.zhu.vo.SecKillGoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SecKillCustomer {

    @Autowired
    private ISeckillOrderService orderService;

    @Autowired
    private ISeckillGoodsService goodsService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MQConstant.SecKillQueue)
    public void secKill(MessageVo messageVo) {
        SecKillGoodsVo goodsVo = messageVo.getGoodsVo();
        long uid = messageVo.getUid();
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + uid + ":" + goodsVo.getId());
        if (seckillOrder != null) {
            return;
        }
        boolean update = goodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("id", goodsVo.getId())
                .gt("stock_count", 0));
        if (!update) {
            return;
        }
        String oid = OidUtils.oid();
        SeckillOrder secOrder = new SeckillOrder(null, uid, oid, goodsVo.getGid(),new Date());
        orderService.save(secOrder);
        //将订单数据添加到缓存当中
        redisTemplate.opsForValue().set("order:" + uid + ":" + goodsVo.getId(), secOrder);
        Order order = new Order();
        order.setOid(oid);
        order.setGid(goodsVo.getGid());
        order.setGname(goodsVo.getGname());
        order.setCount(1);
        order.setCreateTime(new Date(System.currentTimeMillis()));
        order.setEndTime(new Date(System.currentTimeMillis() + 15 * 60 * 1000));
        order.setStatus(0);
        order.setMoney(goodsVo.getSeckillPrice());
        order.setUid(uid);
        order.setAid(1);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        //向订单队列中发送消息
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic, MQConstant.OrderQueue, orders);
        //向延迟队列中发送消息
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic, MQConstant.OrderTimeQueue, orders);
    }

}
