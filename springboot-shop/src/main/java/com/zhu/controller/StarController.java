package com.zhu.controller;

import com.zhu.service.IGoodsService;
import com.zhu.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/star")
public class StarController {

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("/all")
    public RespBean getStar(@CookieValue("userTicket") String ticket){
        return goodsService.getStar(ticket);
    }

}
