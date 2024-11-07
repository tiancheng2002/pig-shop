package com.zhu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhu.feign.GoodsFeign;
import com.zhu.vo.Goods;
import com.zhu.vo.PageResult;
import com.zhu.vo.RequestParam;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ISearchService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    private GoodsFeign goodsFeign;

    public PageResult Search(RequestParam param) throws IOException {
        SearchRequest request = new SearchRequest("shop");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(param.getKey()!=null&& !StringUtils.isEmpty(param.getKey())){
            //对搜索关键字进行高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置要高亮的字段
            highlightBuilder.field("gname");
            //不设置全部匹配高亮，只设置第一个查找到的进行高亮
            highlightBuilder.requireFieldMatch(false);
            //添加高亮标签的前缀
            highlightBuilder.preTags("<span style='color:#c42c2c;font-weight:bold;'>");
            //添加高亮标签的后缀
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
            boolQueryBuilder.must(QueryBuilders.matchQuery("gname",param.getKey()));
        }

        if(param.getTid()!=null&&param.getTid()!=0){
            boolQueryBuilder.must(QueryBuilders.termQuery("tid",param.getTid()));
        }
        if(param.getTpid()!=null&&param.getTpid()!=0){
            boolQueryBuilder.must(QueryBuilders.termQuery("tpid",param.getTpid()));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        if(param.getSort()!=null&&!StringUtils.isEmpty(param.getSort())){
            searchSourceBuilder.sort(param.getSort(), SortOrder.DESC);
        }
        request.source(searchSourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        return getResponse(search);
    }

    public PageResult Detail(int goodsId,String ticket) throws IOException {
        SearchRequest searchRequest = new SearchRequest("shop");
        searchRequest.source().query(QueryBuilders.termQuery("gid",goodsId));
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        return getStarResponse(search,ticket);
    }

    public PageResult getResponse(SearchResponse response){
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        //获取总条数
        long total = searchHits.getTotalHits().value;
        List<Goods> list = new ArrayList<>();
        for (SearchHit hit:hits){
            Goods goods = null;
            //获取高亮的信息
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //获取高亮的字段
            HighlightField highlightTitle = highlightFields.get("gname");
            //如果不为空我们就进行拼接并且替换掉原来的字段
            if(highlightTitle!=null){
                //获取原来的数据
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Text[] fragments = highlightTitle.getFragments();
                String newTitle = "";
                for (Text fragment : fragments){
                    newTitle = newTitle+fragment;
                }
                sourceAsMap.put("gname",newTitle);
                Object map = JSONObject.toJSON(sourceAsMap);
                goods = JSON.parseObject(map.toString(), Goods.class);
            }else{
                //获取文档的source
                String s = hit.getSourceAsString();
                //反序列化source
                goods = JSON.parseObject(s, Goods.class);
            }
            list.add(goods);
        }
        return new PageResult(total,list);
    }

    public PageResult getStarResponse(SearchResponse response,String ticket){
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        //获取总条数
        long total = searchHits.getTotalHits().value;
        List<Goods> list = new ArrayList<>();
        for (SearchHit hit:hits){
            //获取文档的source
            String s = hit.getSourceAsString();
            //反序列化source
            Goods goods = JSON.parseObject(s, Goods.class);
            if(ticket!=null){
                boolean b = goodsFeign.goodsStar(goods.getGid(),ticket);
                goods.setStar(b);
            }
            list.add(goods);
        }
        return new PageResult(total,list);
    }

    public PageResult getNew() throws IOException {
        //根据日期来获取新品推荐
        SearchRequest request = new SearchRequest("shop");
        request.source().sort("createTime",SortOrder.DESC);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return getResponse(response);
    }

    public PageResult discount() throws IOException {
        //获取正在优惠的商品
        SearchRequest request = new SearchRequest("shop");
        request.source().query(QueryBuilders.termQuery("sale","true"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return getResponse(response);
    }
}
