package com.jiaruiblog.service.impl;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.entity.ocrResult.*;
import com.jiaruiblog.service.ElasticService;
import com.jiaruiblog.util.InfixToRPN;
import com.jiaruiblog.util.ReadSynoDataFromTxt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.lucene.search.join.ScoreMode;
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
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String FILE_ID = "fileId";

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

//    content nested中的内容
    private static final String CONTENT_EACH_PAGE_LIST = "contentEachPageList";
    private static final String  CONTENT_EACH_PAGE_LIST_CONTENT = "contentEachPageList.content";
    private static final String  CONTENT_EACH_PAGE_LIST_PAGE_NUM = "contentEachPageList.pageNum";

//    ocrResult中的内容
    private static final String OCR_RESULT_NEW_LIST = "ocrResultNewList";
    private static final String OCR_RESULT_LIST_OCRTEXT = "ocrResultNewList.ocrText";
    private static final String OCR_RESULT_LIST_MONGODB_ID = "ocrResultNewList.mongodb_id";

//    contentEachPageList_syno中的内容
    private static final String CONTENT_EACH_PAGE_LIST_SYNO = "contentEachPageList_syno";
    private static final String  CONTENT_EACH_PAGE_LIST_CONTENT_SYNO = "contentEachPageList_syno.content";
    private static final String  CONTENT_EACH_PAGE_LIST_PAGE_NUM_SYNO = "contentEachPageList_syno.pageNum";

//    ocrResultNewList_syno
    private static final String OCR_RESULT_NEW_LIST_SYNO = "ocrResultNewList_syno";
    private static final String OCR_RESULT_LIST_OCRTEXT_SYNO = "ocrResultNewList_syno.ocrText";
    private static final String OCR_RESULT_LIST_MONGODB_ID_SYNO = "ocrResultNewList_syno.mongodb_id";

    private static final float NO_SYNO_WEIGHT = 3;
    private static final float SYNO_WEIGHT = 1;

    private List<List<String>> synonyms_lines = ReadSynoDataFromTxt.readDataFromTxt();

    @Resource
    private MongoTemplate mongoTemplate;

    public ElasticServiceImpl() throws IOException {
    }
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

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<EsSearch> search_new(String keyword) throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 要返回的字段的信息
        String[] include = {CLICK_RATE, LIKE_NUM,COLLECT_NUM, "name", "type","md5","fileId","id"};
        searchSourceBuilder.fetchSource(include,null);

        //创建bool查询条件
        BoolQueryBuilder boolQueryBuilder = createSingleBoolQueryBuilder(keyword);

        //查询并返回结果
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        return process_outcome(hits,keyword);
    }


//    根据bool表达式去创建一个查询
    private BoolQueryBuilder[] createBuilder(List<String> list){

        Stack<BoolQueryBuilder> boolQueryBuildersContent = new Stack<>();
        Stack<BoolQueryBuilder> boolQueryBuildersOcr = new Stack<>();
        for (String content : list) {
            if (!(content.equals("|") || content.equals("&"))) {

                MatchPhraseQueryBuilder matchContent_temp = QueryBuilders.matchPhraseQuery(CONTENT_EACH_PAGE_LIST_CONTENT, content);
                MatchPhraseQueryBuilder matchOcr_temp = QueryBuilders.matchPhraseQuery(OCR_RESULT_LIST_OCRTEXT, content);

                BoolQueryBuilder boolQueryBuilder1 = new BoolQueryBuilder().must(matchContent_temp);
                BoolQueryBuilder boolQueryBuilder2 = new BoolQueryBuilder().must(matchOcr_temp);

                boolQueryBuildersContent.push(boolQueryBuilder1);
                boolQueryBuildersOcr.push(boolQueryBuilder2);
            } else {
                BoolQueryBuilder boolContent_temp1 = boolQueryBuildersContent.pop();
                BoolQueryBuilder boolContent_temp2 = boolQueryBuildersContent.pop();
                BoolQueryBuilder boolOcr_temp1 = boolQueryBuildersOcr.pop();
                BoolQueryBuilder boolOcr_temp2 = boolQueryBuildersOcr.pop();

                BoolQueryBuilder boolContent_temp3;
                BoolQueryBuilder boolOcr_temp3;

                if (content.equals("|")) {
                    boolContent_temp3 = new BoolQueryBuilder().should(boolContent_temp1).should(boolContent_temp2);
                    boolOcr_temp3 = new BoolQueryBuilder().should(boolOcr_temp1).should(boolOcr_temp2);
                } else {
                    boolContent_temp3 = new BoolQueryBuilder().must(boolContent_temp1).must(boolContent_temp2);
                    boolOcr_temp3 = new BoolQueryBuilder().must(boolOcr_temp1).must(boolOcr_temp2);
                }
                boolQueryBuildersContent.push(boolContent_temp3);
                boolQueryBuildersOcr.push(boolOcr_temp3);
            }
        }

        BoolQueryBuilder[] result = new BoolQueryBuilder[2];
        result[0] = boolQueryBuildersContent.pop();
        result[1] = boolQueryBuildersOcr.pop();
        return result;
    }

//    处理查询出来的结果
    public List<EsSearch> process_outcome(SearchHits hits,String keyword) throws IOException {

        List<String> syno_words = getSyno(keyword);

        List<EsSearch> esSearchList = new ArrayList<>();

        double max_content_score = getMaxScore(hits);
        double max_click_num = (double) getMaxValue(hits, CLICK_RATE);
        double max_like_num = (double) getMaxValue(hits, LIKE_NUM);

        for (SearchHit hit : hits) {
            EsSearch esSearch = new EsSearch();

            List<EsSearchContent> esSearchContentList = new ArrayList<>();
            List<EsSearchOcrOutcome> esSearchOcrOutcomeList = new ArrayList<>();
            List<EsSearchContent> esSearchContentList_syno = new ArrayList<>();

//            获取到content的hit内容
            SearchHits innerContentHits = hit.getInnerHits().get(CONTENT_EACH_PAGE_LIST);
//            获取到ocrList内容
            SearchHits innerOcrHits = hit.getInnerHits().get(OCR_RESULT_NEW_LIST);
//            获取同义词匹配到的内容
            SearchHits innerSynoContentHits = hit.getInnerHits().get(CONTENT_EACH_PAGE_LIST_SYNO);


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
                clickScore = ((int)hit.getSourceAsMap().get(CLICK_RATE)) / max_click_num * CLICK_RATE_WEIGHT;
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
            esSearch.setMd5((String) objectMap.get("id"));
            esSearch.setId((String) objectMap.get("fileId"));

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

//           从同义词中获取
            if(innerSynoContentHits.getTotalHits().value != 0){
                for(SearchHit syno_hit:innerSynoContentHits){
                    EsSearchContent esSearchContent = new EsSearchContent();
                    List<String> highLightList = new ArrayList<>();

                    Map<String, HighlightField> highlightFields = syno_hit.getHighlightFields();
                    HighlightField highlightedContent = highlightFields.get(CONTENT_EACH_PAGE_LIST_CONTENT_SYNO);

                    for(Text highlightedText:highlightedContent.fragments()){
                        String highlightedText_string = highlightedText.toString();

                        highlightedText_string = ReadSynoDataFromTxt.tokenNotSYno(highlightedText_string,syno_words);

                        List<String> bmSubStrs = extractEmTags(highlightedText_string);

//                        判断是否全部高亮均为<bm>keyword</bm>,如果是就没必要添加进去了，因为前面的已经有了，不全为keyword的才有返回的必要
                        boolean flag = false;
                        for(String bmSubStr:bmSubStrs){
                            if(!bmSubStr.equals("<bm>"+keyword+"</bm>")){
                                flag = true;
                                break;
                            }
                        }
//
                        if(flag){
                            highlightedText_string = highlightedText_string.replaceAll("<bm>"+keyword+"</bm>","<em>"+keyword+"</em>");
                            highLightList.add(highlightedText_string);
                        }

//                        有内容我才塞进入
                    }
                    if(highLightList.size()>0){
                        // 设置高亮
                        esSearchContent.setContentHighLight(highLightList);
                        esSearchContent.setPageNum((int)syno_hit.getSourceAsMap().get("pageNum"));
                        esSearchContentList_syno.add(esSearchContent);
                    }
                }
                esSearch.setEsSearchContentList_syno(esSearchContentList_syno);
            }
            else {
                esSearch.setEsSearchContentList_syno(null);
            }

            if(esSearch.getEsSearchOcrOutcomeList() != null || esSearch.getEsSearchContentList() != null)
            {
                esSearchList.add(esSearch);
            }
        }

        return esSearchList;
    }
    public List<EsSearch> process_outcome(SearchHits hits,List<String> keyword){
        List<EsSearch> esSearchList = new ArrayList<>();

        double max_content_score = getMaxScore(hits);
        double max_click_num =(double)getMaxValue(hits, CLICK_RATE);

        double max_like_num = (double) getMaxValue(hits, LIKE_NUM);

        for (SearchHit hit : hits) {
            EsSearch esSearch = new EsSearch();

            List<EsSearchContent> esSearchContentList = new ArrayList<>();
            List<EsSearchOcrOutcome> esSearchOcrOutcomeList = new ArrayList<>();

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

            System.out.println("click_rate_max:"+max_click_num);

            if(max_click_num > 0)
            {
                System.out.println("click_rate:"+hit.getSourceAsMap().get(CLICK_RATE));
                clickScore = ((double) hit.getSourceAsMap().get(CLICK_RATE)) / max_click_num * CLICK_RATE_WEIGHT;
            }
            else {
                clickScore = 0;
            }

            double likeScore = 0;

            if(max_like_num > 0)
            {
                likeScore = ((double) hit.getSourceAsMap().get(LIKE_NUM)) / max_like_num * LIKE_NUM_WEIGHT;
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
            esSearch.setMd5((String) objectMap.get("id"));
            esSearch.setId((String) objectMap.get("fileId"));

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

                    if(keyword.stream().anyMatch(ocrText::contains)){
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

    public List<EsSearch> search_advance(String keywords) throws IOException {

        String rpn = InfixToRPN.infixToRPN(keywords);
        System.out.println(rpn);  // 输出：dqx zbw PYB & |

        String[] tokens = rpn.split("\\s+"); // 将输入字符串按照空格分割成一个字符串数组
        List<String> list = new ArrayList<>(Arrays.asList(tokens)); // 将字符串数组转换为ArrayList

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 要返回的字段的信息
        String[] include = {CLICK_RATE, LIKE_NUM,COLLECT_NUM, "name", "type","md5","fileId","id"};
        searchSourceBuilder.fetchSource(include,null);

        BoolQueryBuilder[] boolQueryBuilders = createBuilder(list);

        BoolQueryBuilder boolQueryBuilderContent = boolQueryBuilders[0].boost(0.7f);
        BoolQueryBuilder boolQueryBuilderOcr = boolQueryBuilders[1].boost(0.3f);

        //创建bool查询条件
        //      加高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(CONTENT_EACH_PAGE_LIST_CONTENT);
        highlightBuilder.preTags("<em>").postTags("</em>");
        highlightBuilder.fragmentSize(150);

//        根据内容去匹配
        NestedQueryBuilder nestedContent = QueryBuilders.nestedQuery(CONTENT_EACH_PAGE_LIST, boolQueryBuilderContent,ScoreMode.Total);
        nestedContent.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(
                                true,new String[]{CONTENT_EACH_PAGE_LIST_PAGE_NUM},null)
                ).setHighlightBuilder(highlightBuilder)
        );

//        ocr文本匹配
        NestedQueryBuilder nestedOcrText = QueryBuilders.nestedQuery(OCR_RESULT_NEW_LIST, boolQueryBuilderOcr, ScoreMode.Total);
        nestedOcrText.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(true, new String[]{OCR_RESULT_LIST_OCRTEXT, OCR_RESULT_LIST_MONGODB_ID}, null)
                )
        );


        //        构建bool查询条件
        BoolQueryBuilder combinedQueryBuilder = QueryBuilders.boolQuery();
//        查询条件，二者满足一个
        combinedQueryBuilder.should(nestedContent);
        combinedQueryBuilder.should(nestedOcrText);
        combinedQueryBuilder.minimumShouldMatch(1);


        //查询并返回结果
        searchSourceBuilder.query(combinedQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();

        return process_outcome(hits,list);
    }

    private BoolQueryBuilder createSingleBoolQueryBuilder(String keyword){

        //      加高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(CONTENT_EACH_PAGE_LIST_CONTENT);
        highlightBuilder.preTags("<em>").postTags("</em>");
        highlightBuilder.fragmentSize(150);

        HighlightBuilder highlightBuilder_syno = new HighlightBuilder();
        highlightBuilder_syno.field(CONTENT_EACH_PAGE_LIST_CONTENT_SYNO);
        highlightBuilder_syno.preTags("<bm>").postTags("</bm>");
        highlightBuilder_syno.fragmentSize(150);

//        根据内容去匹配
        MatchPhraseQueryBuilder matchContent = QueryBuilders.matchPhraseQuery(CONTENT_EACH_PAGE_LIST_CONTENT, keyword).boost(NO_SYNO_WEIGHT);
        BoolQueryBuilder contentboolQueryBuilder = new BoolQueryBuilder().should(matchContent);

        matchContent.boost(0.7f);

        NestedQueryBuilder nestedContent = QueryBuilders.nestedQuery(CONTENT_EACH_PAGE_LIST, contentboolQueryBuilder,ScoreMode.Total);
        nestedContent.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(
                                true,new String[]{CONTENT_EACH_PAGE_LIST_PAGE_NUM},null)
                ).setHighlightBuilder(highlightBuilder)
        );

//        同义词字段的匹配
        MatchPhraseQueryBuilder matchContentSyno = QueryBuilders.matchPhraseQuery(CONTENT_EACH_PAGE_LIST_CONTENT_SYNO, keyword).boost(SYNO_WEIGHT);
        BoolQueryBuilder contentBoolQueryBuilder_syno = new BoolQueryBuilder().should(matchContentSyno);

        matchContentSyno.boost(0.3f);

        NestedQueryBuilder nestedContentSyno = QueryBuilders.nestedQuery(CONTENT_EACH_PAGE_LIST_SYNO, contentBoolQueryBuilder_syno,ScoreMode.Total);
        nestedContentSyno.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(
                                true,new String[]{CONTENT_EACH_PAGE_LIST_PAGE_NUM_SYNO},null)
                ).setHighlightBuilder(highlightBuilder_syno)
        );

//        ocr文本匹配
        MatchPhraseQueryBuilder matchOcrText = QueryBuilders.matchPhraseQuery(OCR_RESULT_LIST_OCRTEXT, keyword).boost(NO_SYNO_WEIGHT);
//        MatchPhraseQueryBuilder matchOcrTextSyno = QueryBuilders.matchPhraseQuery(OCR_RESULT_LIST_OCRTEXT_SYNO, keyword).boost(NO_SYNO_WEIGHT);
        BoolQueryBuilder ocrBoolQueryBuilder = new BoolQueryBuilder().should(matchOcrText);
        ocrBoolQueryBuilder.boost(0.3f);

        NestedQueryBuilder nestedOcrText = QueryBuilders.nestedQuery(OCR_RESULT_NEW_LIST, ocrBoolQueryBuilder, ScoreMode.Total);
        nestedOcrText.innerHit(
                new InnerHitBuilder().setFetchSourceContext(
                        new FetchSourceContext(true, new String[]{OCR_RESULT_LIST_OCRTEXT, OCR_RESULT_LIST_MONGODB_ID}, null)
                )
        );


        //        构建bool查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        查询条件，二者满足一个
        boolQueryBuilder.should(nestedContent);
        boolQueryBuilder.should(nestedOcrText);
        boolQueryBuilder.should(nestedContentSyno);
        boolQueryBuilder.minimumShouldMatch(1);

        return boolQueryBuilder;
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


    @Override
    public void deleteByDocId(String docId){
        System.out.println("进入了删除es的方法中 ");
        DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX_NAME);
        request.setQuery(QueryBuilders.termQuery(FILE_ID,docId));
        try{
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            e.printStackTrace();
        }finally {

        }
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

                        OcrPosition ocrPosition = new OcrPosition(leftBottom, leftTop, rightBottom, rightTop, text);
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

    private static List<String> extractEmTags(String input) {
        List<String> matches = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<bm>[^<]*</bm>");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    private List<String> getSyno(String keyword){
        for(List<String>synonyms_line:synonyms_lines){
            if(synonyms_line.contains(keyword)){
                return synonyms_line;
            }
        }
        return null;
    }
}


