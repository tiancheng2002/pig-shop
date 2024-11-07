package com.zhu.controller;

import com.alibaba.fastjson.JSON;
import com.zhu.feign.GoodsFeign;
import com.zhu.vo.Goods;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shopGoods")
public class GoodsController {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    private GoodsFeign goodsFeign;

    private final String INDEX = "shop";

    @RequestMapping("/insert")
    public boolean insert() throws IOException {
        exist();
        BulkRequest bulkRequest = new BulkRequest();
        List<Goods> goods = goodsFeign.getGoods();
        for (int i=0;i<goods.size();i++){
            bulkRequest.add(new IndexRequest("shop")
                        .id(""+goods.get(i).getGid())
                        .source(JSON.toJSONString(goods.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    public void exist() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDEX);
        boolean b = client.indices().exists(request, RequestOptions.DEFAULT);
        if(b){
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX);
            client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        }else{
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
            client.indices().create(createIndexRequest,RequestOptions.DEFAULT);
        }
    }

}
