package com.zhu.controller;

import com.qiniu.util.Json;
import com.zhu.feign.CommentFeign;
import com.zhu.feign.SearchFeign;
import com.zhu.pojo.Goods;
import com.zhu.pojo.Order;
import com.zhu.service.IGoodsService;
import com.zhu.service.UploadService;
import com.zhu.vo.*;
import com.zhu.vo.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-03-29
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private SearchFeign searchFeign;

    @Autowired
    private CommentFeign commentFeign;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private UploadService uploadService;

//    @RequestMapping("/all")
//    public PageResult getGoods() throws IOException {
//        System.out.println("all");
//        SearchRequest searchRequest = new SearchRequest("shop");
//        searchRequest.source().sort("price", SortOrder.DESC);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        //解析响应
//        SearchHits searchHits = searchResponse.getHits();
//        //获取总条数
//        long total = searchHits.getTotalHits().value;
//        //文档数组
//        SearchHit[] hits = searchHits.getHits();
//        //创建商品集合
//        List<Goods> list = new ArrayList<>();
//        for (SearchHit hit:hits){
//            //获取文档的source
//            String s = hit.getSourceAsString();
//            //反序列化source
//            Goods goods = JSON.parseObject(s, Goods.class);
//            list.add(goods);
//        }
//        return new PageResult(total,list);
//    }

    @RequestMapping("/search")
    public RespBean search(RequestParam param) {
        System.out.println(param);
        return RespBean.success(searchFeign.search(param));
    }

    @RequestMapping("/detail")
    public RespBean getDetail(Integer goodsId,@CookieValue(value = "userTicket",required = false) String userTicket) {
//        System.out.println(goodsId);
        System.out.println(userTicket);
        //通过远程调用评论服务来获取对应商品的评论
        Object comments = commentFeign.getCommentByGoods(goodsId).getObj();
        //通过远程调用搜索服务来获取对应商品的信息
        PageResult detail = searchFeign.detail(goodsId, userTicket);
        DetailVo detailVo = new DetailVo(comments,detail);
        return RespBean.success(detailVo);
    }

    @RequestMapping("/all")
    public List<Goods> getGoods(){
        return goodsService.list();
    }

    @RequestMapping("/goodsStar")
    public boolean goodsStar(Integer goodsId,String userTicket){
        return goodsService.goodsStar(goodsId,userTicket);
    }

    @RequestMapping("/star")
    public RespBean star(Integer goodsId, String status, @CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NOUSER);
        }
        return goodsService.star(goodsId,status,ticket);
    }

    @RequestMapping("/img/upload")
    public RespBean upload(@org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) throws IOException {
        String url = uploadService.upload(file.getInputStream(), file.getOriginalFilename());
        return url==null?RespBean.error(RespBeanEnum.Image_UPLOAD_FAIL): RespBean.success(url);
    }

    @PostMapping("/add")
    public RespBean addGoods(@RequestBody Goods goods){
        goods.setCreateTime(new Date());
        goods.setSprice(goods.getPrice());
        boolean save = goodsService.save(goods);
        if(!save){
            return RespBean.error(RespBeanEnum.Goods_ADD_FAIL);
        }
        return RespBean.success(null);
    }

}
