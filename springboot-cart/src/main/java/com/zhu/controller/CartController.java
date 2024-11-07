package com.zhu.controller;

import com.alibaba.fastjson.JSONArray;
import com.zhu.constant.MQConstant;
import com.zhu.feign.SearchFeign;
import com.zhu.pojo.Cart;
import com.zhu.pojo.Order;
import com.zhu.pojo.OrdersVo;
import com.zhu.pojo.User;
import com.zhu.service.ICartService;
import com.zhu.utils.OidUtils;
import com.zhu.rate.RateLimiter;
import com.zhu.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired()
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private SearchFeign searchFeign;

    @RequestMapping("/test")
    @RateLimiter(time = 5,count = 3)
    public String test(){
        return "test=>"+new Date();

    }

    //查询所有的购物车
    @RequestMapping("/all")
    public RespBean getCart(@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        return cartService.getCart(ticket);
    }

    //添加购物车
    @RequestMapping("/add")
    public RespBean addCart(Cart cart, @CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        return cartService.addCart(cart,ticket);
    }

    @PostMapping("/del")
    public RespBean delete(@RequestBody String carts,@CookieValue("userTicket") String ticket) throws UnsupportedEncodingException {
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        List<Cart> cart = Decode(carts);
        cartService.del(cart,ticket);
        return RespBean.success("删除成功");
    }

    @RequestMapping("/action")
    public RespBean actionCount(CountVo countVo,@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.CartQueue,countVo);
        return RespBean.success(null);
    }

    @PostMapping("/buy")
    public RespBean buy(@RequestBody Map map, @CookieValue("userTicket") String ticket) throws UnsupportedEncodingException {
        //收到订单之后首先需要判断是不是从购物车当中提交订单的
        //如果是从购物车中提交订单的话首先需要删除购物车当中的信息，接着通过mq异步生成订单
        //如果不是购物车中的信息的话就直接生成订单即可
        //生成完订单返回给前端一个响应然后跳转页面进行订单的显示，然后提示用户进行付款
        //并且会显示剩余付款的时间以及购买商品的对应一系列的信息
        //对应商品的库存也会减一，购买人数会加一
        //用户的消费金额也会增加
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        String action = (String) map.get("action");
        //获得对应的购物车信息，并将它们通过mq异步进行删除
        List<Cart> cart = Decode((String) map.get("carts"));
        for (Cart c:cart){
            PageResult result = searchFeign.detail(c.getGid(), ticket);
            if(c.getCount()>result.getGoods().get(0).getCount()||result.getGoods().get(0).getCount()==0){
                return RespBean.error(RespBeanEnum.Goods_Count_NO);
            }
        }
        if(action.equals("cart")){
            rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.CartDelQueue,cart);
        }
        //生成对应的订单信息，然后添加到数据库当中，通过mq异步生成
        List<Order> orders = DecodeOrder((String) map.get("carts"));
        long newTime = System.currentTimeMillis();
        long endTime = newTime+15*60*1000;
        for (Order order:orders){
            order.setOid(OidUtils.oid());
            order.setCreateTime(new Date(newTime));
            order.setEndTime(new Date(endTime));
            order.setStatus(0);
            order.setUid(user.getUid());
        }
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.OrderQueue,orders);
        //给延迟队列中发送消息
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.OrderTimeQueue,orders);
        log.info("消息已发出");
        //减少库存以及增长销售量
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.GoodsQueue,orders);
        //向缓存中添加对应的购物信息
        redisTemplate.opsForValue().set("buy:"+user.getUid(),new OrdersVo((String)map.get("name"),(Long) map.get("phone"),(String) map.get("addressName"),orders));
//        System.out.println(cart);
//        System.out.println(orders);
//        return cartService.buy(cart,ticket);
        return RespBean.success(null);
    }

    @PostMapping("/check")
    public RespBean checkCart(@RequestBody String cart) throws UnsupportedEncodingException {
        List<Cart> carts = Decode(cart);
        for (Cart c:carts){
            Cart getCart = cartService.getById(c.getCid());
            if(getCart==null){
                return RespBean.error(RespBeanEnum.Cart_DATA_Change);
            }
        }
        return RespBean.success(null);
    }

    public List<Cart> Decode(String carts) throws UnsupportedEncodingException {
        //将获取到的字符串转码
        String decode = URLDecoder.decode(carts, "utf-8");
        //去掉字符串当中的\
        decode = decode.replace("\\", "");
        //截取字符串当中的[]之间的内容
        String cartString = decode.substring(decode.indexOf("["),decode.indexOf("]")+1);
        //将字符串转换成json数组
        JSONArray cartArray = JSONArray.parseArray(cartString);
        //将jason数组转换成对应的对象集合
        List<Cart> cart = cartArray.toJavaList(Cart.class);
        return cart;
    }

    public List<Order> DecodeOrder(String order) throws UnsupportedEncodingException {
        //将获取到的字符串转码
        String decode = URLDecoder.decode(order, "utf-8");
        //去掉字符串当中的\
        decode = decode.replace("\\", "");
        //截取字符串当中的[]之间的内容
        String cartString = decode.substring(decode.indexOf("["),decode.indexOf("]")+1);
        //将字符串转换成json数组
        JSONArray cartArray = JSONArray.parseArray(cartString);
        //将jason数组转换成对应的对象集合
        List<Order> orders = cartArray.toJavaList(Order.class);
        return orders;
    }

}
