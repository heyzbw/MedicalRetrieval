package com.jiaruiblog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.entity.OcrPosition;
import com.jiaruiblog.entity.OcrResult;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CallFlask {

    private RestTemplate restTemplate = GetRestTemplate(simpleClientHttpRequestFactory());

    String SERVER_URL = "http://localhost:8083/";

    public List<OcrResult> doUpload(String md5){
//        待输入的参数
        JSONObject param = new JSONObject();
        param.put("md5",md5);
//        链接地址
        String file2ocr_URL = SERVER_URL + "pdf2pic";
        JSONObject result = restTemplate.postForEntity(file2ocr_URL,param,JSONObject.class).getBody();
        JSONArray dataArray = result.getJSONArray("data");
        List<OcrResult> ocrResultList = new ArrayList<>();

        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String ocrText = dataObj.getString("ocrText");
            int pdfPage = dataObj.getInteger("pdfPage");
            String pdfURL = dataObj.getString("pdfURL");
            String image = dataObj.getString("image");

            JSONArray textResultArray = dataObj.getJSONArray("textResult");
//            OcrPosition[] ocrPositions = new OcrPosition[];
            List<OcrPosition> textResults = new ArrayList<>();
            for (int j = 0; j < textResultArray.size(); j++) {
                JSONObject textResultObj = textResultArray.getJSONObject(j);
                int charNum = textResultObj.getInteger("charNum");
                boolean isHandwritten = textResultObj.getBoolean("isHandwritten");
                String leftBottom = textResultObj.getString("leftBottom");
                String leftTop = textResultObj.getString("leftTop");
                String rightBottom = textResultObj.getString("rightBottom");
                String rightTop = textResultObj.getString("rightTop");
                String text = textResultObj.getString("text");

                OcrPosition textResult = new OcrPosition(charNum, isHandwritten, leftBottom, leftTop, rightBottom, rightTop, text);
                textResults.add(textResult);
            }

            OcrResult ocrResult = new OcrResult(ocrText, pdfURL,pdfPage, textResults,image);
            ocrResultList.add(ocrResult);
        }
        return ocrResultList;
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
