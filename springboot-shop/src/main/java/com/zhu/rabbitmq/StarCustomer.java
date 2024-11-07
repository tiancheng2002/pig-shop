package com.zhu.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.constant.MQConstant;
import com.zhu.mapper.GoodsMapper;
import com.zhu.pojo.Goods;
import com.zhu.service.IGoodsService;
import com.zhu.vo.StarVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class StarCustomer {

    @Autowired
    private IGoodsService goodsService;

    @RabbitListener(queues = MQConstant.StarQueue)
    public void star(Map<String, StarVo> map){
        //用来异步修改收藏数量
        StarVo starVo = map.get("starVo");
        if(starVo.getAction().equals("down")){
            goodsService.update(new UpdateWrapper<Goods>()
                    .setSql("stars = stars - 1")
                    .eq("gid",starVo.getGoodsId()));
        }else{
            goodsService.update(new UpdateWrapper<Goods>()
                    .setSql("stars = stars + 1")
                    .eq("gid",starVo.getGoodsId()));
        }
    }

}
