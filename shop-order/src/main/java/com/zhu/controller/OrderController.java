package com.zhu.controller;

import com.alibaba.fastjson.JSONArray;
import com.zhu.pojo.Order;
import com.zhu.pojo.User;
import com.zhu.service.IOrderService;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-12
 */
//提交订单的时候我们需要往一个没有消费者的队列中发送一条消息
//并且给这个消息设置一个有效的时间，如果超出了这个时间的话就会成为死信
//然后将该消息发送到死信交换机当中被消费
//当订单的时间超过15分钟的时候，会将该订单的状态设置为取消状态
//如果用户已经支付完成了，直接返回就行
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @RequestMapping("/all")
    public RespBean getOrder(@CookieValue(value = "userTicket",required = false) String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        return orderService.getOrder(user.getUid());
    }

    @RequestMapping("/detail")
    public RespBean OrderDetail(String oid,@CookieValue(value = "userTicket",required = false) String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        System.out.println(user);
        return orderService.getOrderById(user.getUid(),oid);
    }

    @RequestMapping("/del")
    public RespBean delOrder(String oid,@CookieValue(value = "userTicket",required = false) String ticket){
//        if(StringUtils.isEmpty(ticket)){
//            return RespBean.error(RespBeanEnum.Login_NO);
//        }
        System.out.println(oid);
        return orderService.delOrder(oid);
    }

    @RequestMapping("/save")
    public RespBean saveOrder(List<Order> orders){
        boolean saveBatch = orderService.saveBatch(orders);
        if(saveBatch){
            return RespBean.success(null);
        }else{
            return RespBean.error(RespBeanEnum.Order_ADD_Error);
        }
    }

    @RequestMapping("/getPay")
    public RespBean getPay(String oid,@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        return orderService.pay(oid,ticket);
    }

    @RequestMapping("/pay")
    public RespBean pay(@CookieValue("userTicket") String ticket,@RequestParam("method") Integer method){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        return orderService.goPay(ticket,method);
    }

    @RequestMapping("/get")
    public List<Order> showOrder(){
        return orderService.list();
    }

    //查询自己的已评价和未评价
    @RequestMapping("/myComment")
    public RespBean getMyComment(@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        return orderService.userComment(user.getUid());
    }

//    @PostMapping("/take")
//    public RespBean takeOrder(@RequestBody String order, @CookieValue("userTicket") String ticket) throws UnsupportedEncodingException {
//
//        if(StringUtils.isEmpty(ticket)){
//            return RespBean.error(RespBeanEnum.Login_NO);
//        }
//        List<Order> orders = Decode(order);
//        return orderService.takeOrder(orders);
//    }
//
//    public List<Order> Decode(String carts) throws UnsupportedEncodingException {
//        //将获取到的字符串转码
//        String decode = URLDecoder.decode(carts, "utf-8");
//        //去掉字符串当中的\
//        decode = decode.replace("\\", "");
//        //截取字符串当中的[]之间的内容
//        String cartString = decode.substring(decode.indexOf("["),decode.indexOf("]")+1);
//        //将字符串转换成json数组
//        JSONArray cartArray = JSONArray.parseArray(cartString);
//        //将jason数组转换成对应的对象集合
//        List<Order> cart = cartArray.toJavaList(Order.class);
//        return cart;
//    }

}
