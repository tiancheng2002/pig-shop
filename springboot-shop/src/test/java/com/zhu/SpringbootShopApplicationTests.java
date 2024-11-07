package com.zhu;

import com.zhu.mapper.GoodsMapper;
import com.zhu.pojo.Goods;
import com.zhu.service.IGoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootShopApplicationTests {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private IGoodsService goodsService;

    @Test
    public void getGoodsById() {
//        List<Goods> goods = goodsService.list();
//        for (Goods good:goods){
//            System.out.println(good);
//        }
        System.out.println(1);
    }


//
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;
//
//    @Test
//    void contextLoads() {
//        List<Goods> list = goodsService.list();
//        for (Goods goods:list){
//            System.out.println(goods);
//        }
//    }
//
//    @Test
//    public void delete() throws IOException {
//        DeleteIndexRequest request = new DeleteIndexRequest("shop");
//        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
//    }
//
//    @Test
//    public void Test1() throws IOException {
//        CreateIndexRequest request = new CreateIndexRequest("shop");
//        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
//        System.out.println(request);
//    }
//
//    @Test
//    public void insert() throws IOException {
//        //将数据库中的数据批量的插入到elasticSearch
//        BulkRequest request = new BulkRequest();
//        List<Goods> goods = goodsService.list();
//        //用for循环遍历的添加到bulkRequest请求当中然后进行插入
//        for (int i=0;i<goods.size();i++){
//            request.add(new IndexRequest("shop")
//                    .id(String.valueOf(goods.get(i).getGid()))
//                    .source(JSON.toJSONString(goods.get(i)), XContentType.JSON));
//        }
//        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
//        System.out.println(response);
//    }

}
