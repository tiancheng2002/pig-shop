package com.zhu.controller;

import com.alibaba.fastjson.JSON;
import com.zhu.config.RedissonConfig;
import com.zhu.constant.MQConstant;
import com.zhu.pojo.Order;
import com.zhu.pojo.SeckillGoods;
import com.zhu.pojo.SeckillOrder;
import com.zhu.pojo.User;
import com.zhu.service.ISeckillGoodsService;
import com.zhu.service.ISeckillOrderService;
import com.zhu.utils.OidUtils;
import com.zhu.vo.*;
import org.redisson.api.RBloomFilter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> script;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonConfig redissonConfig;

    private RBloomFilter<Integer> rbloomFilter;

    @PostConstruct
    public void init(){
        rbloomFilter = redissonConfig.bloomFilter();
    }

//    @RequestMapping("/")
//    public String show(){
//        return "index";
//    }

    @RequestMapping("/all")
    public RespBean seckill(){
        //由于秒杀商品页面一下子可能同时有很多请求，所以我们可以将返回的数据直接添加到缓存当中
        //这样子每当用户访问首页的时候，我们就可以从缓存中获取到数据返回给前端，而不需要再去查找数据库
        //如果缓存中不存在的话，就去数据库当中搜索并添加到缓存当中
        List<SecKillGoodsVo> secKillGoods = (List<SecKillGoodsVo>) redisTemplate.opsForValue().get("secKillList");
        if(secKillGoods==null){
            secKillGoods = seckillGoodsService.secKillGoods();
            redisTemplate.opsForValue().set("secKillList",secKillGoods);
        }
//        List<SecKillGoodsVo> secKillGoods = seckillGoodsService.secKillGoods();
//        System.out.println(secKillGoods);
        return RespBean.success(secKillGoods);
    }

    @RequestMapping("/detail")
    public RespBean detail(Integer id){
        //秒杀详情同一时刻也会有很多的请求，所以我们可以将用户访问的详情数据添加到缓存当中
        //和上面同理，如果缓存当中没有就去数据库中查找然后添加到缓存当中
        boolean contains = rbloomFilter.contains(id);
        if(contains){
            SecKillGoodsVo secKillGoodsVo = (SecKillGoodsVo) redisTemplate.opsForValue().get("secKillGoods:" + id);
            if(secKillGoodsVo==null){
                secKillGoodsVo = seckillGoodsService.detail(id);
                redisTemplate.opsForValue().set("secKillGoods:"+id,secKillGoodsVo);
            }
//        SecKillGoodsVo secKillGoodsVo = seckillGoodsService.detail(id);
            return RespBean.success(secKillGoodsVo);
        }else{
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
    }

    //获取秒杀的地址
    @RequestMapping("/secPath")
    public RespBean secPath(Integer id,@CookieValue(value = "userTicket",required = false) String ticket){
        //判断用户是否登录
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        //查询缓存中是否当前秒杀的商品，如果没有直接返回错误，如果有再进行下一步的操作（通过布隆过滤器来判断）
        boolean contains = rbloomFilter.contains(id);
        if(!contains){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
        SecKillGoodsVo secKillGoodsVo = seckillGoodsService.detail(id);
        if(secKillGoodsVo==null){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
        //如果当前时间超过秒杀商品的时间的话，那么就无法获取对应的地址，直接退出即可
        if(new Date().getTime()>secKillGoodsVo.getEndDate().getTime()){
            return RespBean.error(RespBeanEnum.SecKill_Time_Over);
        }
        //如果都满足以上条件，那么就生成对应的秒杀地址返回给前端，然后用户进行秒杀
        String path = seckillGoodsService.getSecPath(id,user.getUid());
        PathVo pathVo = new PathVo(user.getUid(), id, path);
        return RespBean.success(pathVo);
    }

    @RequestMapping("/getStock")
    public RespBean getSecStock(@RequestParam("id") Integer id){
        Integer stock = (Integer) redisTemplate.opsForValue().get("seckillGoodsCount:" + id);
        return RespBean.success(stock);
    }

    //秒杀操作
    @RequestMapping("/sec")
    public RespBean sec2(PathVo pathVo,@CookieValue("userTicket") String ticket){
        //判断用户是否登录
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }

        //查询缓存中是否当前秒杀的商品，如果没有直接返回错误，如果有再进行下一步的操作（通过布隆过滤器来判断）
        boolean contains = rbloomFilter.contains(pathVo.getId());
        if(!contains){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
        SecKillGoodsVo secKillGoodsVo = seckillGoodsService.detail(pathVo.getId());
        if(secKillGoodsVo==null){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
//        SecKillGoodsVo secKillGoodsVo = (SecKillGoodsVo) redisTemplate.opsForValue().get("secKillGoods:" + id);
//        if(secKillGoodsVo==null){
//            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
//        }
        //首先去缓存中查询用户是否秒杀过当前商品，如果秒杀过就直接返回错误，没有就执行下一步操作
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + pathVo.getUid() + ":" + pathVo.getId());
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.SecKill_Have);
        }
        //接着我们就需要去缓存中预减库存，将商品的库存减1，在这个操作当中，我们为了保证原子性，让减库存的操作一定能成功，我们可以的定义一个lua脚本
        //然后通过redisTemplate去执行这一段脚本，然后通过返回值来判断，如果减库存成功，就表示秒杀成功，执行下一步操作，否则就返回错误信息
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoodsCount:" + pathVo.getId()), Collections.EMPTY_LIST);
        System.out.println(stock);
        if(stock==-1){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }else if(stock==0){
            return RespBean.error(RespBeanEnum.SecKill_No_Count);
        }
        //向消息队列中发送消息，让mq来异步生成我们的订单，再订单生成的过程中，我们的为了防止消息的丢失，我们会使用ack确认机制，确保每一步都要成功
        //并且为了防止消息重复消费，我们可以去缓存当中查询用户是否秒杀过当前商品，如果有就直接返回，没有就生成对应的订单
        //并且也需要向死信队列中发送一条消息，十五分钟过后，如果没有付款，商品的库存加1，并且将订单的状态设置为取消
        //发送的参数为(秒杀商品信息以及用户信息)

        //向秒杀订单队列中发送消息
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.SecKillQueue,new MessageVo(secKillGoodsVo,user.getUid()));

        return RespBean.success("秒杀成功");
    }

    //秒杀操作
    @RequestMapping("/{path}/sec")
    public RespBean sec(PathVo pathVo,@PathVariable("path") String path){
//        //判断用户是否登录
//        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
//        if(user==null){
//            return RespBean.error(RespBeanEnum.Login_NO);
//        }
        //首选判断传递过来的秒杀地址是否和redis当中秒杀地址相等，如果相等的话就进行秒杀操作，如果不想等直接返回错误即可
        String secPath = (String) redisTemplate.opsForValue().get("path:" + pathVo.getUid() + ":" + pathVo.getId());
        if(!path.equals(secPath)){
            return RespBean.error(RespBeanEnum.SecKill_Path_ERROR);
        }

        //查询缓存中是否当前秒杀的商品，如果没有直接返回错误，如果有再进行下一步的操作（通过布隆过滤器来判断）
        boolean contains = rbloomFilter.contains(pathVo.getId());
        if(!contains){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
        SecKillGoodsVo secKillGoodsVo = seckillGoodsService.detail(pathVo.getId());
        if(secKillGoodsVo==null){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }
//        SecKillGoodsVo secKillGoodsVo = (SecKillGoodsVo) redisTemplate.opsForValue().get("secKillGoods:" + id);
//        if(secKillGoodsVo==null){
//            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
//        }
        //首先去缓存中查询用户是否秒杀过当前商品，如果秒杀过就直接返回错误，没有就执行下一步操作
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + pathVo.getUid() + ":" + pathVo.getId());
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.SecKill_Have);
        }
        //接着我们就需要去缓存中预减库存，将商品的库存减1，在这个操作当中，我们为了保证原子性，让减库存的操作一定能成功，我们可以的定义一个lua脚本
        //然后通过redisTemplate去执行这一段脚本，然后通过返回值来判断，如果减库存成功，就表示秒杀成功，执行下一步操作，否则就返回错误信息
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoodsCount:" + pathVo.getId()), Collections.EMPTY_LIST);
        System.out.println(stock);
        if(stock==-1){
            return RespBean.error(RespBeanEnum.SecKill_No_Goods);
        }else if(stock==0){
            return RespBean.error(RespBeanEnum.SecKill_No_Count);
        }
        //向消息队列中发送消息，让mq来异步生成我们的订单，再订单生成的过程中，我们的为了防止消息的丢失，我们会使用ack确认机制，确保每一步都要成功
        //并且为了防止消息重复消费，我们可以去缓存当中查询用户是否秒杀过当前商品，如果有就直接返回，没有就生成对应的订单
        //并且也需要向死信队列中发送一条消息，十五分钟过后，如果没有付款，商品的库存加1，并且将订单的状态设置为取消
        //发送的参数为(秒杀商品信息以及用户信息)

        //向秒杀订单队列中发送消息
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.SecKillQueue,new MessageVo(secKillGoodsVo,pathVo.getUid()));

        return RespBean.success(null);
    }

    @RequestMapping("/result")
    public String result(Integer id,@CookieValue("userTicket") String ticket){
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getUid() + ":" + id);
        if(seckillOrder!=null){
            return JSON.toJSONString(seckillOrder.getOid());
        }
        int stock = (int) redisTemplate.opsForValue().get("seckillGoodsCount:" + id);
        if(stock<1){
            return "-1";
        }
        return "0";
    }

    //获取所有的秒杀商品
    @RequestMapping("/getGoods")
    public RespBean getGoods(){
        List<SecKillGoodsVo> secKillGoodsVos = seckillGoodsService.secKillGoods();
        return RespBean.success(secKillGoodsVos);
    }

    @RequestMapping("/secOrder")
    public RespBean getSecOrder(){
        return seckillOrderService.getOrder();
    }

    //添加（修改）秒杀商品
    @PostMapping("/save")
    public RespBean saveSecKillGoods(@RequestBody SeckillGoods seckillGoods){
        System.out.println(seckillGoods);
        boolean action = seckillGoodsService.save(seckillGoods);
//        boolean action = false;
//        if(seckillGoods.getId()!=0&&seckillGoods.getId()!=null){
//            System.out.println("更新");
//            action = seckillGoodsService.updateById(seckillGoods);
//        }else{
//            System.out.println("保存");
//            action = seckillGoodsService.save(seckillGoods);
//        }
//        System.out.println(action);
        return action?RespBean.success(null):RespBean.error(RespBeanEnum.ERROR);
    }

    //获得所有秒杀的订单
    @RequestMapping("/getOrders")
    public RespBean getSecKillOrders(){
        List<SeckillOrder> seckillOrders = seckillOrderService.list();
        return RespBean.success(seckillOrders);
    }

    @RequestMapping("/ready")
    public void ready(){
        //当项目初始化的时候，将所有秒杀商品的库存都放到缓存当中
        List<SeckillGoods> seckillGoods = seckillGoodsService.list();
        System.out.println(seckillGoods);
        if(CollectionUtils.isEmpty(seckillGoods)){
            return;
        }
        seckillGoods.forEach(goods -> {
            redisTemplate.opsForValue().set("seckillGoodsCount:"+goods.getId(),goods.getStockCount());
            rbloomFilter.add(goods.getId());
        });
    }

    //初始化时将商品信息预热到缓存当中
    @Override
    public void afterPropertiesSet() throws Exception {
        //当项目初始化的时候，将所有秒杀商品的库存都放到缓存当中
        List<SeckillGoods> seckillGoods = seckillGoodsService.list();
        if(CollectionUtils.isEmpty(seckillGoods)){
            return;
        }
        seckillGoods.forEach(goods -> {
            redisTemplate.opsForValue().set("seckillGoodsCount:"+goods.getId(),goods.getStockCount());
            rbloomFilter.add(goods.getId());
        });
    }

}
