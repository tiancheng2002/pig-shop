package com.zhu.feign;

import com.zhu.vo.PageResult;
import com.zhu.vo.RequestParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("shop-search")
public interface SearchFeign {

    @RequestMapping("/mySearch/search")
    //传递对象的时候可以选择两边都加上@RequestBody注解
    //另一种方法是直接使用2.1.x版本后的@SpringQueryMap注解，这样服务端不需要加@RequestBody注解了
    PageResult search(@SpringQueryMap RequestParam param);

    @RequestMapping("/mySearch/detail")
    //参数传递需要加上@RequestParam，如果是路径后面跟参数的话需要加上@PathVariable
    PageResult detail(@org.springframework.web.bind.annotation.RequestParam("goodsId") Integer goodsId, @org.springframework.web.bind.annotation.RequestParam("userTicket") String userTicket);

}
