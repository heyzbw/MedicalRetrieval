package com.jiaruiblog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.entity.AskOptional;
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
import java.util.Objects;

public class AskGpt {
    private static final String API_KEY = "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"; // 替换为你的API密钥
    private static final String PROXY_HOST = "localhost";
    private static final int PROXY_PORT = 10809; // 将这里替换为你找到的代理端口号
    private static final String API_URL = "https://api.openai.com/v1/completions";

    private final CloseableHttpClient client;

    public AskGpt() {
        HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http"); // 如果使用的是SOCKS代理，请将"http"替换为"socks"
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        client = HttpClients.custom().setRoutePlanner(routePlanner).build();
    }

    private String generateRequestBody(String prompt) {
        JSONObject json = new JSONObject();
        json.put("model", "text-davinci-003");
        json.put("prompt", prompt);
        json.put("max_tokens", 1024);
        json.put("temperature", 0);
        return json.toString();
    }

    private String generateCorrectMedicalContent(String content){
        return new String("给你一个包含中英文医学检索文本，帮我对其中的内容进行纠错，我给你的内容大概率是有误的，大概率是英文词汇的拼写错误、对于中文词汇格外注意同音字输入错误（比如脑魔癌应该修正为脑膜癌）" +
                "中英文混合纠错时优先考虑中文纠错中文，英文纠错英文，比如（爱正病就纠错为艾滋病，不要纠错为AIDS;carcar就纠错为cancer，不要纠错为癌症）" +
                "错误的类型可能有：拼写错误（重点关注英文词汇的拼写，中文词汇的同音字输入错误）、混淆音同字、词形混淆、空格错误、错位错误、键盘布局错误、词汇错误，" +
                "只考虑医学与生物学的部分，检索内容content：" + content + "。" +
                "如果有错误，则按照如下的格式给出：你的文本好像有误，你是否想检索：<em>${纠错内容content}</em>。否者你就说：检索内容无误，数据集中没有相关的文献。");
    }

    private String generateSummarizedParagraph(String content){
        return new String("这里是一个医学文献的段落:"+content+"分要点给出段落总结，要精简一些。你的输出应该遵循如下的格式：1. {$要点1}  2. {$要点2} ...");
    }

    private String generateAskQuestion(String content){
        return new String("这里是一个医学与生物学相关的问题，根据你的知识库回答:"+content);
    }

    private String generateAdvice(String content){
        return new String("我给你一份关于我从病例中识别出来（通过机器学习实现的）的关于病人的" +
                "疾病: disease,身体: body,症状: symptom,医疗程序: medicalProcedure,医疗设备: medicalEquipment,药物: medicine,科室: department,微生物类: microorganism,医学检验项目: medicalExamination的信息，你根据你的知识库，给我一些治疗建议"+
                "信息："+content+"。你的回答应该按照如下的格式：”对于这份病例，经过运算，提供如下的治疗建议：{$你的治疗建议}"
        );
    }

    public String ask(AskOptional askOptional, String content) {
        String responseContent = null;
        String requestBodyPrompt;
        if(Objects.equals(askOptional.getValue(), "correct")){
            requestBodyPrompt = generateCorrectMedicalContent(content);
        }
        else if(Objects.equals(askOptional.getValue(), "summarize")){
            requestBodyPrompt = generateSummarizedParagraph(content);
        }
        else if(Objects.equals(askOptional.getValue(), "advice")){
            requestBodyPrompt = generateAdvice(content);
        }
        else{
            requestBodyPrompt = generateAskQuestion(content);
        }

        String requestBodyString = generateRequestBody(requestBodyPrompt);

        HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http"); // 如果使用的是SOCKS代理，请将"http"替换为"socks"
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        try (CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);

            // 设置HTTP请求的实体
            StringEntity stringEntity = new StringEntity(requestBodyString, "UTF-8");
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(stringEntity);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("responseContent: " + responseContent);
        // 使用 Fastjson 解析 JSON
        JSONObject responseObject = JSON.parseObject(responseContent);
        JSONArray choicesArray = responseObject.getJSONArray("choices");
        JSONObject firstChoiceObject = choicesArray.getJSONObject(0);
        String textContent = firstChoiceObject.getString("text");

        return textContent.replace("\n",""); // 返回 choice 中的 text 内容
    }

}