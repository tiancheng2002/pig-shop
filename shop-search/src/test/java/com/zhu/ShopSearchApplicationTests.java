package com.zhu;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
class ShopSearchApplicationTests {

    @Resource
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
    }

    @Test
    public void delete() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("shop");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
    }

}
