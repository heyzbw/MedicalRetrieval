package com.jiaruiblog.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.entity.ocrResult.*;
import com.jiaruiblog.service.ElasticService;
import com.jiaruiblog.util.InfixToRPN;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;

import javax.annotation.Resource;
//import org.apache.lucene.search.join;

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

    //    private static final String INDEX_NAME = "synonym_test";
    private static final String PIPELINE_NAME = "attachment.content";

    //ocr结果的字段
//    private static final String OCR_RESULT_LIST = "ocrResultList";
    private static final String OCR_RESULT_LIST_TEXT = "ocrResultList.ocrText";


    //    点击率字段
    private static final String CLICK_RATE = "click_rate";

    //    点赞量字段
    private static final String LIKE_NUM = "collect_num";
    private static final double CONTENT_WEIGHT = 60;
    private static final double CLICK_RATE_WEIGHT = 30;
    private static final double LIKE_NUM_WEIGHT = 10;

    private static final String COLLECT_NUM = "like_num";

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private FileServiceImpl fileServiceImpl;

    private static int FRAGMENTSIZE = 80;

    private static int FRAGMENTNUMS = 5;
    private static String em_front = "<em>";
    private static String em_last = "</em>";
    private static String zhuanyizfu = "/r";

    private static String CONTENT_EACH_PAGE_LIST = "contentEachPageList";
    private static String  CONTENT_EACH_PAGE_LIST_CONTENT = "contentEachPageList.content";
    private static String  CONTENT_EACH_PAGE_LIST_PAGE_NUM = "contentEachPageList.pageNum";
    private static String OCR_RESULT_NEW_LIST = "ocrResultNewList";
    private static String OCR_RESULT_LIST_OCRTEXT = "ocrResultNewList.ocrText";
    private static String OCR_RESULT_LIST_MONGODB_ID = "ocrResultNewList.mongodb_id";

    @Resource
    private MongoTemplate mongoTemplate;
//    private static float CONTENT_WEIGHT = 0.7f;


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
//        indexRequest.setPipeline("attachment");

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
//        String id = indexResponse.getId();
//         立即使用相同的 ID 搜索刚插入的文档
//        GetResponse response = client.get(
//                new GetRequest(INDEX_NAME, "_doc", id), RequestOptions.DEFAULT); // 根据文档ID查询
//        System.out.println("插入到es中"+response.getSourceAsString()); // 输出查询结果
//        JSONObject jsonObject = JSON.parseObject(response.getSourceAsString());
//        JSONObject attachment = jsonObject.getJSONObject("attachment");

//        String content = attachment.getString("content");

//        UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, "_doc", id);
//
//        Map<String, Object> updateFields = new HashMap<>();
//        updateFields.put("nosyn", content);
//        updateFields.put("syn", content);

//        updateRequest.doc(updateFields);

//        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

    }

    public List<EsSearch> search_new(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//      加高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(CONTENT_EACH_PAGE_LIST_CONTENT);
        highlightBuilder.preTags("<em>").postTags("</em>");
        highlightBuilder.fragmentSize(150);
        searchSourceBuilder.highlighter(highlightBuilder);

//        要返回的字段的名称
        String[] include = {CLICK_RATE, LIKE_NUM,COLLECT_NUM, "name", "type","md5","fileId","id"};
        searchSourceBuilder.fetchSource(include,null);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

//        根据内容去匹配
        MatchPhraseQueryBuilder matchContent = QueryBuilders.matchPhraseQuery(CONTENT_EACH_PAGE_LIST_CONTENT, keyword);
        matchContent.boost(0.7f);

        NestedQueryBuilder nestedContent = QueryBuilders.nestedQuery(CONTENT_EACH_PAGE_LIST, matchContent,ScoreMode.Total);
        nestedContent.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(
                                true,new String[]{CONTENT_EACH_PAGE_LIST_PAGE_NUM},null
                        )
                ).setHighlightBuilder(highlightBuilder)
        );

//        ocr文本匹配
        MatchPhraseQueryBuilder matchOcrText = QueryBuilders.matchPhraseQuery(OCR_RESULT_LIST_OCRTEXT, keyword);
        matchOcrText.boost(0.3f);

        NestedQueryBuilder nestedOcrText = QueryBuilders.nestedQuery(OCR_RESULT_NEW_LIST, matchOcrText, ScoreMode.Total);
        nestedOcrText.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(true, new String[]{OCR_RESULT_LIST_OCRTEXT, OCR_RESULT_LIST_MONGODB_ID}, null
                        )
                )
        );

//        查询条件，二者满足一个
        boolQueryBuilder.should(nestedContent);
        boolQueryBuilder.should(nestedOcrText);
        boolQueryBuilder.minimumShouldMatch(1);

//        查询并返回结果
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();

        List<EsSearch> esSearchList = new ArrayList<>();



        double max_content_score = getMaxScore(hits);
        int max_click_num = getMaxValue(hits, CLICK_RATE);
        int max_like_num = getMaxValue(hits, LIKE_NUM);

        for (SearchHit hit : hits) {
            EsSearch esSearch = new EsSearch();

            List<EsSearchContent> esSearchContentList = new ArrayList<>();
            List<EsSearchOcrOutcome> esSearchOcrOutcomeList = new ArrayList<>();

            List<Map<String, Object>> contentResultList = new ArrayList<>();
//            获取到content的hit内容
            SearchHits innerContentHits = hit.getInnerHits().get(CONTENT_EACH_PAGE_LIST);
//            获取到ocrList内容
            SearchHits innerOcrHits = hit.getInnerHits().get(OCR_RESULT_NEW_LIST);

            Map<String,Object> objectMap = hit.getSourceAsMap();

            float score = hit.getScore();

            double contentScore = 0;

            if(max_content_score > 0){
                contentScore = score / max_content_score * CONTENT_WEIGHT;
            }
            else {
                contentScore = 0;
            }

            double clickScore = 0;


            if(max_click_num > 0)
            {
                clickScore = ((int) hit.getSourceAsMap().get(CLICK_RATE)) / max_click_num * CLICK_RATE_WEIGHT;
            }
            else {
                clickScore = 0;
            }

            double likeScore = 0;

            if(max_like_num > 0)
            {
                likeScore = ((int) hit.getSourceAsMap().get(LIKE_NUM)) / max_like_num * LIKE_NUM_WEIGHT;
            }
            else {
                likeScore = 0;
            }


//           查询返回的字段为： "click_rate", "collect_num", "collect_num","name", "type","md5","fileId","id",

            esSearch.setClickScore(clickScore);
            esSearch.setLikeScore(likeScore);
            esSearch.setContentScore(contentScore);
            esSearch.setName((String) objectMap.get("name"));
            esSearch.setType((String) objectMap.get("type"));
            esSearch.setFileId((String) objectMap.get("fileId"));
            esSearch.setId((String) objectMap.get("id"));

//            从innerhit中获取文本内容
            if(innerContentHits.getTotalHits().value != 0){
                for (SearchHit contentHit : innerContentHits){
                    EsSearchContent  esSearchContent = new EsSearchContent();
//                    保存高亮内容的
                    List<String> highLightList = new ArrayList<>();

                    Map<String, HighlightField> highlightFields = contentHit.getHighlightFields();
                    HighlightField highlightedContent = highlightFields.get(CONTENT_EACH_PAGE_LIST_CONTENT);
                    for(Text highlightedText:highlightedContent.fragments()){
                        String highlightedText_string = highlightedText.toString();
                        highLightList.add(highlightedText_string);
                    }
//                    设置高亮
                    esSearchContent.setContentHighLight(highLightList);
//                    设置页码
                    esSearchContent.setPageNum((int)contentHit.getSourceAsMap().get("pageNum"));

                    esSearchContentList.add(esSearchContent);
                }
                esSearch.setEsSearchContentList(esSearchContentList);
            }
            else {
                esSearch.setEsSearchContentList(null);
            }


//            从ocr中获取图片内容
            if(innerOcrHits.getTotalHits().value != 0){
                boolean flag = false;
                for(SearchHit ocrHit:innerOcrHits){
                    EsSearchOcrOutcome esSearchOcrOutcome = new EsSearchOcrOutcome();
                    String ocrText = (String) ocrHit.getSourceAsMap().get("ocrText");

                    if(ocrText.contains(keyword)){
                        flag = true;
                        esSearchOcrOutcome.setOcrText(ocrText);
                        esSearchOcrOutcome.setMongoDbId((String) ocrHit.getSourceAsMap().get("mongodb_id"));
                        esSearchOcrOutcomeList.add(esSearchOcrOutcome);
                    }
                }
                if(flag)
                {
                    esSearch.setEsSearchOcrOutcomeList(esSearchOcrOutcomeList);
                }
            }
            else {
                esSearch.setEsSearchOcrOutcomeList(null);
            }
            if(esSearch.getEsSearchOcrOutcomeList() != null || esSearch.getEsSearchContentList() != null)
            {
                esSearchList.add(esSearch);
            }
        }
        return esSearchList;
    }

    public List<EsSearch> search_high(String keyword) throws IOException{
        String rpn = InfixToRPN.infixToRPN(keyword);
        System.out.println(rpn);  // 输出：dqx zbw PYB & |

        String[] tokens = rpn.split("\\s+"); // 将输入字符串按照空格分割成一个字符串数组
        List<String> list = new ArrayList<>(Arrays.asList(tokens)); // 将字符串数组转换为ArrayList

        for(String str:list){
            System.out.println("str:"+str+"\"");
        }
        System.out.println(list);


        return null;
    }

    @Override
    public List<FileDocument> search(String keyword) throws IOException {

        List<FileDocument> fileDocumentList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
        SearchSourceBuilder srb = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder_ocr = QueryBuilders.matchQuery(OCR_RESULT_LIST_TEXT,keyword);
        MatchPhraseQueryBuilder matchQueryBuilder_content = QueryBuilders.matchPhraseQuery(PIPELINE_NAME, keyword);
        boolQueryBuilder.should(matchQueryBuilder_ocr).should(matchQueryBuilder_content);

        srb.query(boolQueryBuilder);
        // 每页10个数据
        srb.size(10);
        // 起始位置从0开始
        srb.from(0);

        //设置highlighting
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field(PIPELINE_NAME);
        highlightContent.highlighterType();
        highlightBuilder.field(highlightContent).fragmentSize(FRAGMENTSIZE).numOfFragments(FRAGMENTNUMS);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //highlighting会自动返回匹配到的文本，所以就不需要再次返回文本了
        String[] includeFields = new String[]{"name", "id", LIKE_NUM, CLICK_RATE, COLLECT_NUM};
        String[] excludeFields = new String[]{PIPELINE_NAME};
        srb.fetchSource(includeFields, excludeFields);

        //把刚才设置的值导入进去
        srb.highlighter(highlightBuilder);

        //查询
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

        //hits是一个迭代器，所以需要迭代返回每一个hits
        Iterator<SearchHit> iterator = hits.iterator();
        int count = 0;

        StringBuilder stringBuilder = new StringBuilder();

        Set<String> idSet = Sets.newHashSet();

//        三个值分别代表最大的内容得分、点击量得分与点赞量得分
        double max_content_score = getMaxScore(hits);
        int max_click_num = getMaxValue(hits, CLICK_RATE);
        int max_like_num = getMaxValue(hits, LIKE_NUM);

        System.out.println("max_content_score:"+max_content_score);
        System.out.println("max_click_num:"+max_click_num);
        System.out.println("max_like_num:"+max_like_num);

        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            List<Map<String, Object>> ocrResultListMaps = (List<Map<String, Object>>) hit.getSourceAsMap().get("ocrResultList");

            //从es中读取ocr结果
            List<OcrResult> ocrResultList = readFromES(ocrResultListMaps,keyword);

            //根据内容去检索，得到的得分
            float score = hit.getScore();

//            分别以60、30、10来计算三个得分
//            后期可能会加上源自于图片的得分与源自于content的得分
            double contentScore = score / max_content_score * CONTENT_WEIGHT;
            double clickScore = 0;
            double likeScore = 0;

            if(max_click_num > 0)
            {
                clickScore = ((int) hit.getSourceAsMap().get(CLICK_RATE)) / max_click_num * CLICK_RATE_WEIGHT;
            }
            else {
                clickScore = 0;
            }

            if(max_like_num > 0)
            {
                likeScore = ((int) hit.getSourceAsMap().get(LIKE_NUM)) / max_like_num * LIKE_NUM_WEIGHT;
            }
            else {
                likeScore = 0;
            }

            System.out.println("score:"+contentScore);
            System.out.println("clickScore:"+clickScore);
            System.out.println("likeScore:"+likeScore);

            //统计找到了几条
            count++;

            //这个就会把匹配到的文本返回，而且只返回匹配到的部分文本docId = -1
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            System.out.println("highlightFields:"+highlightFields);
            HighlightField highlightField = highlightFields.get(PIPELINE_NAME);
//            float[] scores = highlightField.
            StringBuilder stringBuilder1 = new StringBuilder();
//            有很多条，然后每一条
            List<String> stringList = new ArrayList<>();
            if(highlightField!=null)
            {
                for (Text fragment : highlightField.getFragments()) {
                    String fragmentString = fragment.toString();
                    fragmentString = fragmentString.replace(em_front,"");
                    fragmentString = fragmentString.replace(em_last,"");
                    stringList.add(fragmentString);
                    stringBuilder1.append(fragment.toString());
                }
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

                    // 得分
                    fileDocument.setContentScore(contentScore);
                    fileDocument.setClickScore(clickScore);
                    fileDocument.setLikeScore(likeScore);

                    if (fileDocument == null) {
                        System.out.println("mongodb没有这个md5对应的文件信息");
                        //从redis中剔除该doc，并跳过循环
                        continue;
                    }
                    fileDocument.setDescription(abstractString);
                    fileDocument.setDescription_highLighter(stringList);
//                    fileDocument.setOcrResultNewList();
                    fileDocument.setOcrResultList(ocrResultList);

                    fileDocumentList.add(fileDocument);
                }
            }

            stringBuilder.append(highlightFields);
        }

        stringBuilder.append("查询到").append(count).append("条记录");
        return fileDocumentList;
    }

//    3.18版本
//    @Override
//    public List<FileDocument> search(String keyword) throws IOException {
//
//        List<FileDocument> fileDocumentList = new ArrayList<>();
//        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
//        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
//        SearchSourceBuilder srb = new SearchSourceBuilder();
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        MatchQueryBuilder matchQueryBuilder_ocr = QueryBuilders.matchQuery(OCR_RESULT_LIST_TEXT,keyword);
//        MatchPhraseQueryBuilder matchQueryBuilder_content = QueryBuilders.matchPhraseQuery(PIPELINE_NAME, keyword);
//        boolQueryBuilder.should(matchQueryBuilder_ocr).should(matchQueryBuilder_content);
//
//        srb.query(boolQueryBuilder);
//        // 每页10个数据
//        srb.size(10);
//        // 起始位置从0开始
//        srb.from(0);
//
//        //设置highlighting
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field(PIPELINE_NAME);
//        highlightContent.highlighterType();
//        highlightBuilder.field(highlightContent).fragmentSize(FRAGMENTSIZE).numOfFragments(FRAGMENTNUMS);
//        highlightBuilder.preTags("<em>");
//        highlightBuilder.postTags("</em>");
//
//        //highlighting会自动返回匹配到的文本，所以就不需要再次返回文本了
//        String[] includeFields = new String[]{"name", "id", LIKE_NUM, CLICK_RATE, COLLECT_NUM, OCR_RESULT_LIST};
//        String[] excludeFields = new String[]{PIPELINE_NAME};
//        srb.fetchSource(includeFields, excludeFields);
//
//        //把刚才设置的值导入进去
//        srb.highlighter(highlightBuilder);
//
//        //查询
//        searchRequest.source(srb);
//        SearchResponse res;
//        try {
//            res = client.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (ConnectException e) {
//            log.error("连接es失败！", e.getCause());
//            res = null;
//        }
//
//        if (res == null || res.getHits() == null) {
//            return Lists.newArrayList();
//        }
//        //获取hits，这样就可以获取查询到的记录了
//        SearchHits hits = res.getHits();
//
//        //hits是一个迭代器，所以需要迭代返回每一个hits
//        Iterator<SearchHit> iterator = hits.iterator();
//        int count = 0;
//
//        StringBuilder stringBuilder = new StringBuilder();
//
//        Set<String> idSet = Sets.newHashSet();
//
////        三个值分别代表最大的内容得分、点击量得分与点赞量得分
//        double max_content_score = getMaxScore(hits);
//        int max_click_num = getMaxValue(hits, CLICK_RATE);
//        int max_like_num = getMaxValue(hits, LIKE_NUM);
//
//        System.out.println("max_content_score:"+max_content_score);
//        System.out.println("max_click_num:"+max_click_num);
//        System.out.println("max_like_num:"+max_like_num);
//
//        while (iterator.hasNext()) {
//            SearchHit hit = iterator.next();
//
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            List<Map<String, Object>> ocrResultListMaps = (List<Map<String, Object>>) hit.getSourceAsMap().get("ocrResultList");
//
//            //从es中读取ocr结果
//            List<OcrResult> ocrResultList = readFromES(ocrResultListMaps,keyword);
//
//            //根据内容去检索，得到的得分
//            float score = hit.getScore();
//
////            System.out.println("click_num"+hit.getSourceAsMap().get(CLICK_RATE));
////            System.out.println("like_num"+hit.getSourceAsMap().get(LIKE_NUM));
////            分别以60、30、10来计算三个得分
////            后期可能会加上源自于图片的得分与源自于content的得分
//            double contentScore = score / max_content_score * CONTENT_WEIGHT;
//            double clickScore = 0;
//            double likeScore = 0;
//
//            if(max_click_num > 0)
//            {
//                clickScore = ((int) hit.getSourceAsMap().get(CLICK_RATE)) / max_click_num * CLICK_RATE_WEIGHT;
//            }
//            else {
//                clickScore = 0;
//            }
//
//            if(max_like_num > 0)
//            {
//                likeScore = ((int) hit.getSourceAsMap().get(LIKE_NUM)) / max_like_num * LIKE_NUM_WEIGHT;
//            }
//            else {
//                likeScore = 0;
//            }
//
//            System.out.println("score:"+contentScore);
//            System.out.println("clickScore:"+clickScore);
//            System.out.println("likeScore:"+likeScore);
//
//            //获取返回的字段
//            //Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//
//            //统计找到了几条
//            count++;
//
//            //这个就会把匹配到的文本返回，而且只返回匹配到的部分文本docId = -1
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            System.out.println("highlightFields:"+highlightFields);
//            HighlightField highlightField = highlightFields.get(PIPELINE_NAME);
////            float[] scores = highlightField.
//            StringBuilder stringBuilder1 = new StringBuilder();
////            有很多条，然后每一条
//            List<String> stringList = new ArrayList<>();
//            if(highlightField!=null)
//            {
//                for (Text fragment : highlightField.getFragments()) {
//                    String fragmentString = fragment.toString();
//                    fragmentString = fragmentString.replace(em_front,"");
//                    fragmentString = fragmentString.replace(em_last,"");
//                    stringList.add(fragmentString);
////                System.out.println("fragmentString长度为："+fragmentString.length());
//                    stringBuilder1.append(fragment.toString());
//                }
//            }
//            String abstractString = stringBuilder1.toString();
//            if (abstractString.length() > 500) {
//                abstractString = abstractString.substring(0, 500);
//            }
//
//            if (sourceAsMap.containsKey("id")) {
//                String id = (String) sourceAsMap.get("id");
//                if (id != null && !idSet.contains(id)) {
//                    idSet.add(id);
//                    FileDocument fileDocument = fileServiceImpl.getByMd5(id);
//
//                    // 得分
//                    fileDocument.setContentScore(contentScore);
//                    fileDocument.setClickScore(clickScore);
//                    fileDocument.setLikeScore(likeScore);
//
//                    if (fileDocument == null) {
//                        System.out.println("mongodb没有这个md5对应的文件信息");
//                        //从redis中剔除该doc，并跳过循环
//                        continue;
//                    }
//                    fileDocument.setDescription(abstractString);
//                    fileDocument.setDescription_highLighter(stringList);
//                    fileDocument.setOcrResultList(ocrResultList);
//
//                    fileDocumentList.add(fileDocument);
//                }
//            }
//
//            stringBuilder.append(highlightFields);
//        }
//
//        stringBuilder.append("查询到").append(count).append("条记录");
//        return fileDocumentList;
//    }

//    原作者的版本
//    @Override
//    public List<FileDocument> search(String keyword) throws IOException {
//
//        List<FileDocument> fileDocumentList = new ArrayList<>();
//        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
//        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
//        SearchSourceBuilder srb = new SearchSourceBuilder();
//        srb.query(QueryBuilders.matchQuery(PIPELINE_NAME, keyword));
//
//        // 每页10个数据
//        srb.size(10);
//        // 起始位置从0开始
//        srb.from(0);
//
//        //设置highlighting
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field(PIPELINE_NAME);
//        highlightContent.highlighterType();
//        highlightBuilder.field(highlightContent).fragmentSize(FRAGMENTSIZE).numOfFragments(FRAGMENTNUMS);
//        highlightBuilder.preTags("<em>");
//        highlightBuilder.postTags("</em>");
////        highlightBuilder.highlighterOptions(
////
////        );
//
//        //highlighting会自动返回匹配到的文本，所以就不需要再次返回文本了
//        String[] includeFields = new String[]{"name", "id"};
//        String[] excludeFields = new String[]{PIPELINE_NAME};
//        srb.fetchSource(includeFields, excludeFields);
//
//        //把刚才设置的值导入进去
//        srb.highlighter(highlightBuilder);
//
//        //查询
//        searchRequest.source(srb);
//        SearchResponse res;
//        try {
//            res = client.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (ConnectException e) {
//            log.error("连接es失败！", e.getCause());
//            res = null;
//        }
//
//        if (res == null || res.getHits() == null) {
//            return Lists.newArrayList();
//        }
//        //获取hits，这样就可以获取查询到的记录了
//        SearchHits hits = res.getHits();
//
//        //hits是一个迭代器，所以需要迭代返回每一个hits
//        Iterator<SearchHit> iterator = hits.iterator();
//        int count = 0;
//
//        StringBuilder stringBuilder = new StringBuilder();
//
//        Set<String> idSet = Sets.newHashSet();
//
//        while (iterator.hasNext()) {
//            SearchHit hit = iterator.next();
//
//            //获取返回的字段
//            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//            //统计找到了几条
//            count++;
//
//            //这个就会把匹配到的文本返回，而且只返回匹配到的部分文本docId = -1
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//
//            HighlightField highlightField = highlightFields.get(PIPELINE_NAME);
////            float[] scores = highlightField.
//            StringBuilder stringBuilder1 = new StringBuilder();
//            for (Text fragment : highlightField.getFragments()) {
//                System.out.println(fragment.toString());
//                stringBuilder1.append(fragment.toString());
//            }
//            String abstractString = stringBuilder1.toString();
//            if (abstractString.length() > 500) {
//                abstractString = abstractString.substring(0, 500);
//            }
//
//            if (sourceAsMap.containsKey("id")) {
//                String id = (String) sourceAsMap.get("id");
//                if (id != null && !idSet.contains(id)) {
//                    idSet.add(id);
//                    FileDocument fileDocument = fileServiceImpl.getByMd5(id);
//                    if (fileDocument == null) {
//                        System.out.println("mongodb没有这个md5对应的文件信息");
//                        //从redis中剔除该doc，并跳过循环
//                        continue;
//                    }
//                    fileDocument.setDescription(abstractString);
//                    fileDocumentList.add(fileDocument);
//                }
//            }
//
//            stringBuilder.append(highlightFields);
//        }
//
//        stringBuilder.append("查询到").append(count).append("条记录");
//        return fileDocumentList;
//    }


    /**
     * 根据关键词，搜索对应的文件信息
     * 查询文件中的文本内容
     * 默认会search出所有的东西来
     * SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
     * <p>
     * // srb.query(QueryBuilders.matchQuery("attachment.content", keyword).analyzer("ik_smart"));
     *
     //     * @param keyword String
     * @return list
     * @throws IOException ioexception
     *
     */

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

            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, hit.getId());
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

        return false;
    }
    public List<OcrResult> readFromES(List<Map<String, Object>> ocrResultListMaps,String keyword){

        List<OcrResult> ocrResultList = new ArrayList<>();

        if (ocrResultListMaps != null) {
            for (Map<String, Object> ocrResultMap : ocrResultListMaps) {
                String ocrText = (String) ocrResultMap.get("ocrText");
                if(!ocrText.contains(keyword))
                {
                    continue;
                }
                String pdfURL = (String) ocrResultMap.get("pdfUrl");
                int pdfPage = (Integer) ocrResultMap.get("pdfPage");

                String image = (String) ocrResultMap.get("image");

                List<OcrPosition> textResult = new ArrayList<>();
                List<Map<String, Object>> textResultMaps = (List<Map<String, Object>>) ocrResultMap.get("textResult");
                if (textResultMaps != null) {
                    for (Map<String, Object> textResultMap : textResultMaps) {
                        String text = (String) textResultMap.get("text");
                        if(!text.contains(keyword))
                        {
                            continue;
                        }
                        String leftTop = (String) textResultMap.get("leftTop");
                        String rightBottom = (String) textResultMap.get("rightBottom");
                        String leftBottom = (String) textResultMap.get("leftBottom");
                        String rightTop = (String) textResultMap.get("rightTop");
                        boolean handwritten = (Boolean) textResultMap.get("handwritten");
                        int charNum = (Integer) textResultMap.get("charNum");

                        OcrPosition ocrPosition = new OcrPosition(charNum, handwritten, leftBottom, leftTop, rightBottom, rightTop, text);
                        textResult.add(ocrPosition);
                    }
                }
                OcrResult ocrResult = new OcrResult(ocrText, pdfURL, pdfPage, textResult, image);
                ocrResultList.add(ocrResult);
            }
        }
        return ocrResultList;
    }

    @Override
    public boolean addCollect(String docId) {
        return false;
    }


    //    private double getScore()
    private int getMaxValue(SearchHits hits,String FieldName){

        //hits是一个迭代器，所以需要迭代返回每一个hits
        Iterator<SearchHit> iterator = hits.iterator();
        int max_value = 0;
        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();
            int click_num = (Integer) hit.getSourceAsMap().get(FieldName);
            if (click_num>max_value){
                max_value = click_num;
            }
        }
        return max_value;

    }

    private double getMaxScore(SearchHits hits){
        Iterator<SearchHit> iterator = hits.iterator();
        double max_score = 0;

        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();
            float score = hit.getScore();
            if (score>max_score){
                max_score = score;
            }
        }
        return max_score;
    }
}


