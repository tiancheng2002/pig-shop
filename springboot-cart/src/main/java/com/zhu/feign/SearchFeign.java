package com.zhu.feign;

import com.zhu.vo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("shop-search")
public interface SearchFeign {

    @RequestMapping("/mySearch/detail")
    //参数传递需要加上@RequestParam，如果是路径后面跟参数的话需要加上@PathVariable
    PageResult detail(@RequestParam("goodsId") Integer goodsId, @RequestParam("userTicket") String userTicket);

}
