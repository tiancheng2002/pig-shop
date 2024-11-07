package com.zhu.feign;

import com.zhu.vo.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("shop-goods")
public interface GoodsFeign {

    @RequestMapping("/goods/all")
    List<Goods> getGoods();

    @RequestMapping("/goods/goodsStar")
    boolean goodsStar(@RequestParam("goodsId") Integer goodsId,@RequestParam("userTicket") String userTicket);

}
