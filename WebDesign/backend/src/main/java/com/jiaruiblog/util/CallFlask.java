package com.jiaruiblog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.entity.ocrResult.OcrPosition;
import com.jiaruiblog.entity.ocrResult.OcrResult;
import com.jiaruiblog.entity.ocrResult.OcrResultNew;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CallFlask {

    private RestTemplate restTemplate = GetRestTemplate(simpleClientHttpRequestFactory());

    String SERVER_URL = "http://localhost:5000/";

    public List<OcrResultNew> doUpload(String md5){
//        待输入的参数
        JSONObject param = new JSONObject();
        param.put("md5",md5);
//        链接地址
        String file2ocr_URL = SERVER_URL + "pdf2pic";
        JSONObject result = restTemplate.postForEntity(file2ocr_URL,param,JSONObject.class).getBody();
        JSONArray dataArray = result.getJSONArray("data");
        List<OcrResultNew> ocrResultNewArrayList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String ocrText = dataObj.getString("ocrText");
            String mongoDB_id = dataObj.getString("recordId");

            OcrResultNew ocrResultNew = new OcrResultNew();
            ocrResultNew.setOcrText(ocrText);
            ocrResultNew.setMongodb_id(mongoDB_id);

            ocrResultNewArrayList.add(ocrResultNew);

        }
        return ocrResultNewArrayList;
    }

    public String toScan(String path){
        return null;
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
