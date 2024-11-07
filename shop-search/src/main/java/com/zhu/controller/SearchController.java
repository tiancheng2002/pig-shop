package com.zhu.controller;

import com.zhu.service.ISearchService;
import com.zhu.vo.PageResult;
import com.zhu.vo.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/mySearch")
public class SearchController {

    @Autowired
    private ISearchService searchService;

    @RequestMapping("/search")
    public PageResult search(RequestParam param) throws IOException {
//        System.out.println(param);
        return searchService.Search(param);
    }

    @RequestMapping("/detail")
    public PageResult detail(Integer goodsId,String userTicket) throws IOException {
//        System.out.println(goodsId);
//        System.out.println(userTicket);
        return searchService.Detail(goodsId,userTicket);
    }

    @RequestMapping("/newGoods")
    public PageResult getNewGoods() throws IOException {
        return searchService.getNew();
    }

    @RequestMapping("/discount")
    public PageResult getDiscount() throws IOException {
        return searchService.discount();
    }

}
