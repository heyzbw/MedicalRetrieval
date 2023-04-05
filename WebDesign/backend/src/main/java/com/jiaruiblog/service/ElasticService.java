package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

import java.io.IOException;
import java.util.List;

/**
 * @author jiarui.luo
 */
public interface ElasticService {


    /**
     * search
     * @param keyword String
     * @return result
     * @throws IOException
     */
    List<FileDocument> search(String keyword) throws IOException;

    //加入了OCR后的结构
//    @Override
//    public List<FileDocument> search(String keyword) throws IOException {
//
//        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.matchQuery(OCR_RESULT_LIST_TEXT, keyword));
////        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(OCR_RESULT_LIST_TEXT,keyword);
//
//
////        NestedQueryBuilder queryBuilders = QueryBuilders.nestedQuery(OCR_RESULT_LIST, matchQueryBuilder, ScoreMode.None);
//
//        searchSourceBuilder.query(boolQueryBuilder);
//        searchRequest.source(searchSourceBuilder);
//
//        // 执行查询请求
//        SearchResponse searchResponse = client.search(
//                searchRequest,RequestOptions.DEFAULT);
////        System.out.println("searchResponse:"+searchResponse);
//
//
////        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
////        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
////        MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("attachment.syn",keyword).boost(3);
////        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("attachment.nosyn",keyword);
////        boolQueryBuilder.should(matchQueryBuilder1).should(matchQueryBuilder2);
////        sourceBuilder.query(boolQueryBuilder);
////
////        searchRequest.source(sourceBuilder);
////                .query(QueryBuilders.termQuery("fileId", keyword));
//
//
//        // 处理查询结果
//        SearchHit[] hits = searchResponse.getHits().getHits();
////        System.out.println("hits:"+hits);
//        System.out.println("hits.length:"+hits.length);
////        for (SearchHit hit : hits) {
////            System.out.println(hit.getSourceAsString());
////        }
//        return null;

//        List<FileDocument> fileDocumentList = new ArrayList<>();
//        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
//        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
//        SearchSourceBuilder srb = new SearchSourceBuilder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        MatchQueryBuilder synQuery = QueryBuilders.matchQuery(PIPELINE_NAME, keyword).boost(3);
//        MatchQueryBuilder noSynQuery = QueryBuilders.matchQuery("content_nosyno", keyword);
//        boolQueryBuilder.should(synQuery).should(noSynQuery);
//
//        srb.query(boolQueryBuilder);
//        searchRequest.source(srb);
//        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
//
//        SearchHits hits = searchResponse.getHits();
//        System.out.println("ES一共查询到了"+hits.getTotalHits().value+"条文档");
//
//        //hits是一个迭代器，所以需要迭代返回每一个hits
//        Iterator<SearchHit> iterator = hits.iterator();
//        int count = 0;
//        StringBuilder stringBuilder = new StringBuilder();
//        Set<String> idSet = Sets.newHashSet();
//
//
//        while (iterator.hasNext()) {
//            SearchHit hit = iterator.next();
//            System.out.println("Search Hit hit score:"+hit.getScore());
//            System.out.println("Search Hit hit filename:"+hit.getId());
//
//        }
//
//        return null;

//    }


    void deleteByDocId(String docId);

    /**
     * RemoveCollect
     * @param docId String
     * @return boolean
     */
    public boolean RemoveCollect(String docId) throws IOException;

    /**
     * addCollect
     * @param docId String
     * @return boolean
     */
    public boolean addCollect(String docId);

}
