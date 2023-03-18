package com.jiaruiblog.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;


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
    public void remove() {

// 创建查询
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("content", "dqx"))
                .should(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("content", "zbw"))
                        .must(QueryBuilders.matchQuery("content", "PYB")));

// 执行查询
        SearchResponse response = client.prepareSearch("my-index")
                .setQuery(query)
                .get();

// 处理结果
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            String content = hit.getSource().get("content").toString();
            // 处理匹配的文档
        }

        System.out.println("score:"+Math.log1p(100));
    }
}