package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.service.ElasticService;
import com.jiaruiblog.util.FieldChoice;
import com.jiaruiblog.util.NumberOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;

import javax.naming.directory.SearchResult;

/**
 * @ClassName ElasticServiceImpl
 * @Description ElasticServiceImpl
 * @Author luojiarui
 * @Date 2022/7/12 10:54 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class ElasticServiceImpl implements ElasticService {

    private static final String INDEX_NAME = "docwrite";

    private static final String PIPELINE_NAME = "attachment.content";

//    点击率字段
    private static final String CLICK_RATE = "click_rate";

//    点赞量字段
    private static final String LIKE_NUM = "like_num";

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private FileServiceImpl fileServiceImpl;


    /**
     * 有三种类型
     * 1.文件的名字
     * 2.文件type
     * 3.文件的data 64编码
     */
    public void upload(FileObj file) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        //上传同时，使用attachment pipeline 进行提取文件
        indexRequest.source(JSON.toJSONString(file), XContentType.JSON);
        indexRequest.setPipeline("attachment");
        client.index(indexRequest, RequestOptions.DEFAULT);
    }

//    public


    /**
     * 根据关键词，搜索对应的文件信息
     * 查询文件中的文本内容
     * 默认会search出所有的东西来
     * SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
     * <p>
     * // srb.query(QueryBuilders.matchQuery("attachment.content", keyword).analyzer("ik_smart"));
     *
     * @param keyword String
     * @return list
     * @throws IOException ioexception
     */
    @Override
    public List<FileDocument> search(String keyword) throws IOException {

        List<FileDocument> fileDocumentList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
        SearchSourceBuilder srb = new SearchSourceBuilder();

        srb.query(QueryBuilders.matchQuery(PIPELINE_NAME, keyword));

//        自定义得分函数，其中内容相关的的得分为60%，点击率的得分为30%，点赞量的得分为10%
        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = new ScriptScoreFunctionBuilder(new Script(
                "double contentScore = _score * 0.6; " +
                        "long clicks = doc['"+CLICK_RATE+"'].value; " +
                        "long likes = doc['"+LIKE_NUM+"'].value; " +
                        "double clickScore = Math.log1p(clicks) * 0.3; double likeScore = Math.log1p(likes) * 0.1; " +
                        "return contentScore + clickScore + likeScore;")
        );

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
                srb.query(), scriptScoreFunctionBuilder
        );

        functionScoreQueryBuilder.boostMode(CombineFunction.SUM);
//        functionScoreQueryBuilder.boostMode(CombineFunction.valueOf("sum"));
        srb.query(functionScoreQueryBuilder);


        // 每页10个数据
        srb.size(10);
        // 起始位置从0开始
        srb.from(0);

        //设置highlighting
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field(PIPELINE_NAME);
        highlightContent.highlighterType();
        highlightBuilder.field(highlightContent);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //highlighting会自动返回匹配到的文本，所以就不需要再次返回文本了
        String[] includeFields = new String[]{"name", "id"};
        String[] excludeFields = new String[]{PIPELINE_NAME};
        srb.fetchSource(includeFields, excludeFields);

        //把刚才设置的值导入进去
        srb.highlighter(highlightBuilder);
        searchRequest.source(srb);
        SearchResponse res;
        try {
            res = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ConnectException e) {
            log.error("连接es失败！", e.getCause());
            res = null;
        }

        if (res == null || res.getHits() == null) {
            return Lists.newArrayList();
        }
        //获取hits，这样就可以获取查询到的记录了
        SearchHits hits = res.getHits();
        System.out.println("ES一共查询到了"+hits.getTotalHits().value+"条文档");

        //hits是一个迭代器，所以需要迭代返回每一个hits
        Iterator<SearchHit> iterator = hits.iterator();
        int count = 0;

        StringBuilder stringBuilder = new StringBuilder();

        Set<String> idSet = Sets.newHashSet();

        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();
            System.out.println("Search Hit hit score:"+hit.getScore());
            //获取返回的字段
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //统计找到了几条
            count++;

            //这个就会把匹配到的文本返回，而且只返回匹配到的部分文本
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField highlightField = highlightFields.get(PIPELINE_NAME);

            StringBuilder stringBuilder1 = new StringBuilder();
            for (Text fragment : highlightField.getFragments()) {
                stringBuilder1.append(fragment.toString());
            }
            String abstractString = stringBuilder1.toString();
            if (abstractString.length() > 500) {
                abstractString = abstractString.substring(0, 500);
            }

            if (sourceAsMap.containsKey("id")) {
                String id = (String) sourceAsMap.get("id");
                if (id != null && !idSet.contains(id)) {
                    idSet.add(id);
                    FileDocument fileDocument = fileServiceImpl.getByMd5(id);
                    if (fileDocument == null) {
                        //从redis中剔除该doc，并跳过循环
                        continue;
                    }
                    fileDocument.setDescription(abstractString);
                    fileDocumentList.add(fileDocument);
                }
            }

            stringBuilder.append(highlightFields);
        }

        stringBuilder.append("查询到").append(count).append("条记录");
        return fileDocumentList;
    }
    public boolean NumberOperation(String fileId, String operation, String field) throws IOException {

        int increment = 0;
        if(operation.equals("ADD")) {
            increment = 1;
        }
        else if (operation.equals("MINI")) {
            increment = -1;
        }
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("fileId", fileId));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        if (searchHits.getTotalHits().value > 0) {
            SearchHit hit = searchHits.getAt(0);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            int Num = (int) sourceAsMap.get(field);
            Num += increment;

            UpdateRequest updateRequest = new UpdateRequest("docwrite", hit.getId());
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put(field, Num);
            updateRequest.doc(jsonMap);

            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            System.out.println(updateResponse.getResult().toString());
        }
        else {
            System.out.println("No documents found for fileId " + fileId);
        }


        return false;
    }
    @Override
    public boolean RemoveCollect(String fileId) throws IOException {

        int increment = 1;

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("fileId", fileId));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        if (searchHits.getTotalHits().value > 0) {
            SearchHit hit = searchHits.getAt(0);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            int collectNum = (int) sourceAsMap.get("collect_num");
            collectNum += increment;

            UpdateRequest updateRequest = new UpdateRequest("docwrite", hit.getId());
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("collect_num", collectNum);
            updateRequest.doc(jsonMap);

            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            System.out.println(updateResponse.getResult().toString());
        }
        else {
            System.out.println("No documents found for fileId " + fileId);
        }
//        // 按照fileId查询文档
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.termQuery("fileId", fileId));
//        searchSourceBuilder.from(0);
//        searchSourceBuilder.size(1);
//        searchSourceBuilder.sort("collect_num", SortOrder.DESC);
//        searchSourceBuilder.fetchSource(new FetchSourceContext(true, new String[]{"collect_num"}, null));
//        String index = "docwrite";
//        String type = "_doc";
//        String id = null;
//        int num ;
//
//        try {
//            // 执行查询操作
//            SearchResponse result = client.search(new SearchRequest(index).types(type).source(searchSourceBuilder),RequestOptions.DEFAULT);
//
//            // 获取查询结果中的文档ID
//            if (result.getHits().getHits().length > 0) {
//                id = result.getHits().getHits()[0].getId();
//                System.out.println("查出来的id:"+id);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (id != null) {
//            // 构建更新请求
//            UpdateRequest updateRequest = new UpdateRequest(index, type, id);
//            String jsonString = "{\"doc\":{\"collect_num\":3}}";
//            updateRequest.doc(jsonString, XContentType.JSON);
//
//            // 执行更新请求
//            client.update(updateRequest,RequestOptions.DEFAULT);
//        }


        return false;
    }

    @Override
    public boolean addCollect(String docId) {
        return false;
    }

}


