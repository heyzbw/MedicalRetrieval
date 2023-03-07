package com.jiaruiblog.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class CallFlask {

    private RestTemplate restTemplate = GetRestTemplate(simpleClientHttpRequestFactory());

    String SERVER_URL = "http://localhost:8083/";

    public void doUpload(String md5){
//        待输入的参数
        JSONObject param = new JSONObject();
        param.put("md5",md5);
//        链接地址
        String file2ocr_URL = SERVER_URL + "pdf2pic";
        JSONObject result = restTemplate.postForEntity(file2ocr_URL,param,JSONObject.class).getBody();
        System.out.println("result:"+result);
    }

    public RestTemplate GetRestTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }

    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1500000000);
        factory.setReadTimeout(1000000000);
        return factory;
    }
}
