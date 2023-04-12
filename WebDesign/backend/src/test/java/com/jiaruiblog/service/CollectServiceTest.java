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

import java.util.*;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class CollectServiceTest {

    public static void main(String[] args){

        // 创建 MongoDB 的客户端对象
        MongoClient mongoClient = new MongoClient("localhost", 27017);

// 获取要插入的集合对象
        MongoDatabase database = mongoClient.getDatabase("sqrrow");
        MongoCollection<Document> collection = database.getCollection("likeCollection");

    // 创建要插入的文档对象
            Document doc = new Document("userId", "6435ce9f34f8213de25f824e")
                    .append("docId", "12345");
//                    .append("liked_at", new Date());


    }
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

    @Test
    public void remove_111(){
        //表达式
        String suffixExpression = "1+((2+3)*4)-5";
        //中缀表达式对应的List
        System.out.println("中缀表达式对应的List");
        List<String> infixExpressionList = toInfixExpressionList(suffixExpression);
        System.out.println(infixExpressionList);
        //后缀表达式对应的List
        System.out.println("后缀表达式对应的List");
        List<String> suffixExpressionList = parseSuffixExpressionList(infixExpressionList);
        System.out.println(suffixExpressionList);

//        计算逆波兰表达式
//        System.out.printf("suffixExpression=%d", calculate(suffixExpressionList));
    }

    private static boolean isOperator(String token) {
        return token.equals("OR") || token.equals("AND");
    }

    //将中缀表达式转换成list
    public static List<String> toInfixExpressionList(String s) {
        List<String> ls = new ArrayList<String>();
        int i = 0;
        String str;  //多位数
        char c;
        do {
            //非数字
            if ((c = s.charAt(i)) < 48 || (c = s.charAt(i)) > 57) {
                ls.add("" + c);
                i++;
            } else { //数字，但是考虑到多位数
                str = "";
                while (i < s.length() && (c = s.charAt(i)) >= 48 && (c = s.charAt(i)) <= 57) {
                    str += c;
                    i++;
                }
                ls.add(str);
            }
        } while (i < s.length());
        return ls;
    }


    public static List<String> parseSuffixExpressionList(List<String> ls) {
        //定义两个栈
        Stack<String> s1 = new Stack<String>();  //符号栈
        List<String> s2 = new ArrayList<String>();  //结果

        for (String item : ls) {
            //如果是一个数
            if (item.matches("\\d+")) {
                s2.add(item);
            } else if (item.equals("(")) {
                s1.push(item);
            } else if (item.equals(")")) {
                while (!s1.peek().equals("(")) {
                    s2.add(s1.pop());
                }
                s1.pop();
            } else {
                while (s1.size() != 0 && Operation.getValue(s1.peek()) >= Operation.getValue(item)) {
                    s2.add(s1.pop());
                }
                s1.push(item);
            }
        }
        while (s1.size() != 0) {
            s2.add(s1.pop());
        }
        return s2;
    }

}

