package com.zhu.feign;

import com.zhu.vo.RespBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("shop-comment")
public interface CommentFeign {

    @RequestMapping("/comment/detail")
    RespBean getCommentByGoods(@RequestParam("goodsId") Integer goodsId);

}
