package com.jiaruiblog.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

public class CorrectContent {
    private static final String API_KEY = "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"; // 替换为你的API密钥
    private static final String PROXY_HOST = "localhost";
    private static final int PROXY_PORT = 10809; // 将这里替换为你找到的代理端口号
    private static final String API_URL = "https://api.openai.com/v1/completions";

    private final CloseableHttpClient client;

    public CorrectContent() {
        HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http"); // 如果使用的是SOCKS代理，请将"http"替换为"socks"
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        client = HttpClients.custom().setRoutePlanner(routePlanner).build();
    }


    private String generateRequestBody(String content) {
        JSONObject json = new JSONObject();
        json.put("model", "text-davinci-003");
        json.put("prompt", "给你一个医学检索文本，帮我对其中的内容进行纠错，" +
                "错误的类型可能有：拼写错误、混淆音同字、词形混淆、空格错误、语法错误、错位错误、键盘布局错误、词汇错误、语境不相关的词，" +
                "只考虑医学与生物学的部分，检索内容content：" + content + "。" +
                "如果有错误，则按照如下的格式给出：你的文本好像有误，你是否想检索：${纠错内容content}。否者你就说：检索内容无误，数据集中没有相关的文献。");
        json.put("max_tokens", 1024);
        json.put("temperature", 0);
        return json.toString();
    }


//    correctContent
    public String correctMedicalContent(String content) {
        String responseContent = null;
        String requestBodyString = generateRequestBody(content);
        HttpPost request = new HttpPost(API_URL);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + API_KEY);

        // 设置HTTP请求的实体
        StringEntity stringEntity = new StringEntity(requestBodyString, "UTF-8");
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.setEntity(stringEntity);

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("responseContent: " + responseContent);
        // 使用 Fastjson 解析 JSON
        JSONObject responseObject = JSON.parseObject(responseContent);
        JSONArray choicesArray = responseObject.getJSONArray("choices");
        JSONObject firstChoiceObject = choicesArray.getJSONObject(0);
        String textContent = firstChoiceObject.getString("text");

        return textContent; // 返回 choice 中的 text 内容
    }
}
