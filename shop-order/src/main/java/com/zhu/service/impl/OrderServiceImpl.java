package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.OrderMapper;
import com.zhu.pojo.Order;
import com.zhu.pojo.OrdersVo;
import com.zhu.pojo.User;
import com.zhu.service.IOrderService;
import com.zhu.vo.OrderVo;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-12
 */
@Service
public class OrderServiceImpl extends  ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public RespBean takeOrder(List<Order> orders) {
        return null;
    }

    @Override
    public RespBean getOrder(Long uid) {
        List<OrderVo> orders = orderMapper.getOrder(uid);
//        System.out.println(orders);
        return RespBean.success(orders);
    }

    @Override
    public RespBean delOrder(String oid) {
        Order order = orderMapper.selectById(oid);
        if(order==null){
            return RespBean.error(RespBeanEnum.Order_No);
        }
        int del = orderMapper.deleteById(oid);
        if(del<0){
            return RespBean.error(RespBeanEnum.Order_Del_Error);
        }
        return RespBean.success(null);
    }

    @Override
    public RespBean getOrderById(Long uid,String oid) {
        Order order = orderMapper.selectById(oid);
        if(order==null){
            return RespBean.error(RespBeanEnum.Order_No);
        }
        System.out.println(order);
        OrderVo orderVo = orderMapper.getOrderById(uid,oid);
        System.out.println(orderVo);
        return RespBean.success(orderVo);
    }

    @Override
    public RespBean pay(String oid,String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        if(oid==null|| StringUtils.isEmpty(oid)){
            System.out.println(redisTemplate.opsForValue().get("buy:" + user.getUid()));
            OrdersVo orders = (OrdersVo) redisTemplate.opsForValue().get("buy:" + user.getUid());
            return RespBean.success(orders);
        }else{
            OrderVo order = orderMapper.getOrderById(user.getUid(),oid);
            if(order==null){
                return RespBean.error(RespBeanEnum.Order_No);
            }else{
                List<Order> orders = new ArrayList<>();
                orders.add(new Order(oid,order.getGid(),order.getGname(),order.getCount(),order.getCreateTime(),order.getEndTime(),order.getStatus(),null,null,order.getMoney(),order.getUid(),order.getAid()));
                OrdersVo ordersVo = new OrdersVo(order.getName(), order.getPhone(), order.getAddressName(), orders);
                redisTemplate.opsForValue().set("buy:"+user.getUid(),ordersVo);
                return RespBean.success(ordersVo);
            }
        }
    }

    @Override
    public RespBean goPay(String ticket,Integer method) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        OrdersVo ordersVo = (OrdersVo) redisTemplate.opsForValue().get("buy:" + user.getUid());
        for (Order order:ordersVo.getOrders()){
            Order getOrder = orderMapper.selectById(order.getOid());
            if(getOrder.getStatus()!=0){
                return RespBean.error(RespBeanEnum.Order_Status_Change);
            }
            order.setPayTime(new Date());
            order.setStatus(1);
            order.setMethod(method);
            orderMapper.updateById(order);
            //用户提交支付后就删除redis当中的缓存
            redisTemplate.delete("buy:"+user.getUid());
//            orderMapper.update(order, new UpdateWrapper<Order>().setSql("status = 1, pay_time = "+new Date()).eq("oid", order.getOid()));
        }
        return RespBean.success(null);
    }

    @Override
    public RespBean userComment(long uid) {
        List<Order> orders = orderMapper.userComment(uid);
        return RespBean.success(orders);
    }

}
