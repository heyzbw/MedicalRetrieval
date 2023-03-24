package com.jiaruiblog.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.service.impl.CategoryServiceImpl;
import com.jiaruiblog.service.impl.ElasticServiceImpl;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class CategoryServiceTest {

    @Resource
    CategoryServiceImpl categoryServiceImpl;

    @Test
    public void insert() throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        ); // 连接到Elasticsearch集群

        GetResponse response = client.get(
                new GetRequest("synonym_test", "_doc", "Hskg0oYBtyzfqskMJwlG"), RequestOptions.DEFAULT); // 根据文档ID查询

        System.out.println(response.getSourceAsString()); // 输出查询结果

        client.close(); // 关闭客户端连接
    }

    @Test
    public void update() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));


    }

    @Test
    public void remove() {
    }

    @Test
    public void queryById() {
    }

    @Test
    public void search() {
    }

    @Test
    public void list() {
    }

    @Test
    public void addRelationShip() throws IOException {
        ElasticServiceImpl elasticService = new ElasticServiceImpl();
        elasticService.search_new("研究");

    }

    @Test
    public void cancelCategoryRelationship() {
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 无参数查找
     * @Date 21:45 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest() {
        String cateId = "";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }


    /**
     * @return void
     * @Author luojiarui
     * @Description 通过标签id进行查找
     * @Date 21:45 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest1() {
        String cateId = "";
        String tagId = "62b68b4fb7859f613263e83d";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 通过分类id进行查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest2() {
        String cateId = "62b68278b7251d2c780e37d7";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest3() {
        String cateId = "62b6814377914c7fa8fa959b";
        String tagId = "636f52d21d19a36d975850ad";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest4() {
        String cateId = "62b6814377914c7fa8fa959b";
        String tagId = "636f52d21d19a36d975850ad";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "白皮书";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest5() {
        String cateId = "";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "白皮书";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }
}