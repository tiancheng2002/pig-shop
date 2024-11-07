package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.zhu.constant.MQConstant;
import com.zhu.mapper.GoodsMapper;
import com.zhu.pojo.Goods;
import com.zhu.pojo.User;
import com.zhu.service.IGoodsService;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import com.zhu.vo.StarVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-03-29
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public RespBean star(Integer goodsId, String status, String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.ERROR);
        }
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods==null){
            return RespBean.error(RespBeanEnum.Goods_NO);
        }else{
            String msg = "";
            if(status.equals("detail")){
                msg = DetailStar(user, goodsId);
            }else{
                msg = OrderStar(user,goodsId);
            }
            return RespBean.success(msg);
        }
    }

    public String DetailStar(User user,Integer goodsId){
        //首先判断该用户是否给该商品点赞
        Double score = redisTemplate.opsForZSet().score("star:"+user.getUid(), goodsId);
        String msg = "";
        String action = "";
        if(score!=null){
            //如果存在的话就从redis当中删除
            redisTemplate.opsForZSet().remove("star:"+user.getUid(), goodsId);
            //通过mq更改该商品的总收藏数
            action = "down";
            msg = "取消收藏成功";
        }else{
            //如果不存在就往里面添加该商品的id
            redisTemplate.opsForZSet().add("star:"+user.getUid(), goodsId,System.currentTimeMillis());
            //通过mq更改该商品的总收藏数
            action = "in";
            msg = "收藏成功!";
        }
        StarVo starVo = new StarVo(goodsId, action);
        rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.StarQueue,new HashMap<String, StarVo>(){
            {put("starVo",starVo);}
        });
        return msg;
    }

    public String OrderStar(User user,Integer goodsId){
        //首先判断该用户是否给该商品点赞
        Double score = redisTemplate.opsForZSet().score("star:"+user.getUid(), goodsId);
        String msg = "";
        if(score!=null){
            msg = "已收藏";
        }else{
            //如果不存在就往里面添加该商品的id
            redisTemplate.opsForZSet().add("star:"+user.getUid(), goodsId,System.currentTimeMillis());
            //通过mq更改该商品的总收藏数
            msg = "收藏成功!";
            StarVo starVo = new StarVo(goodsId, "in");
            rabbitTemplate.convertAndSend(MQConstant.ShopTopic,MQConstant.StarQueue,new HashMap<String, StarVo>(){
                {put("starVo",starVo);}
            });
        }
        return msg;
    }

    @Override
    public RespBean getStar(String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.ERROR);
        }
        Set<Integer> stars = redisTemplate.opsForZSet().range("star:"+user.getUid(),0,-1);
        List<Goods> goods = null;
        if(stars.size()!=0){
            String str = Joiner.on(",").join(stars);
            goods = goodsMapper.selectList(new QueryWrapper<Goods>()
                    .in("gid", stars)
                    .last("ORDER BY FIELD(gid," + str + ")"));
        }

//        List<Goods> goods = goodsMapper.selectBatchIds(stars);
        return RespBean.success(goods);
    }

    @Override
    public boolean goodsStar(Integer goodsId,String ticket) {
        System.out.println(ticket);
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return false;
        }
        Double score = redisTemplate.opsForZSet().score("star:"+user.getUid(), goodsId);
        System.out.println(score);
        return score!=null;
    }


}
