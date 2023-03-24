package com.jiaruiblog.service;

import org.apache.http.HttpHost;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

import org.elasticsearch.index.query.QueryBuilders;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class CollectServiceTest {

//    @Test
//    public void insert() {
//
//        // 创建客户端对象
//        RestHighLevelClient ESclient = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")));
//        //高亮查询
//        // 创建搜索请求对象
//        SearchRequest request1 = new SearchRequest();
//        request1.indices("docwrite1");
//
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//
//        //MatchQueryBuilder termQueryBuilder = QueryBuilders.matchQuery("attachment.content","树");
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("attachment.content", "net");
//        //sourceBuilder.query(QueryBuilders.termQuery("content","China"));
//
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//
//        highlightBuilder.field().fragmentSize(50).numOfFragments(50);//.noMatchSize(150);//设置高亮字段
//
//
//        builder.highlighter(highlightBuilder);
//        builder.query(termQueryBuilder);
//
//
//        request1.source(builder);
//        SearchResponse search = ESclient.search(request1, RequestOptions.DEFAULT);
//
//
//        SearchHits hits = search.getHits();
//        System.out.println("took:" + search.getTook());
//        System.out.println("timeout:" + search.isTimedOut());
//        System.out.println("total:" + hits.getTotalHits());
//        System.out.println("MaxScore:" + hits.getMaxScore());
//        System.out.println("hits========>>");
//        for (SearchHit hit : hits) {
//            String singourceAsStr = hit.getSourceAsString();
//            //System.out.println(sourceAsString);
//            //打印高亮结果
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//
//            HighlightField highlightField = highlightFields.get("attachment.content");
//            // System.out.println(highlightField);
//            StringBuilder stringBuilder1 = new StringBuilder();
//            for (Text fragment : highlightField.getFragments()) {
//                stringBuilder1.append(fragment.toString());
//            }
//            //下面的索引是实例，可以变换输出内容
//            System.out.println(highlightField.getFragments()[1]);
//        }
//        System.out.println("<<========");
//
//    }

    @Test
    public void remove() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest("docwrite");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String nestedPath = "contentEachPageList";
        String keyword = "癌症";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQuery1 =
                QueryBuilders.matchQuery("contentEachPageList.content", "ruby");

        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(nestedPath,boolQueryBuilder.must(matchQuery1), ScoreMode.None);

        searchSourceBuilder.query(nestedQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        Map<String, Object> map = null;
        try{
            SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
            if (searchResponse.getHits().getTotalHits().value > 0) {
                SearchHit[] searchHit = searchResponse.getHits().getHits();
                for(SearchHit hit:searchHit){
                    map = hit.getSourceAsMap();
                    System.out.println("output::"+ Arrays.toString(map.entrySet().toArray()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }




//        for (SearchHit hit : searchResponse.getHits()) {
//            int pageNum = (int) hit.getSourceAsMap().get("pageNum");
//            String content = (String) hit.getSourceAsMap().get("content");
//            System.out.println("pageNum: " + pageNum + ", content: " + content);
//        }

        client.close();
    }
}