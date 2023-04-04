package com.jiaruiblog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.entity.ocrResult.OcrResultNew;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CallFlask {

    private RestTemplate restTemplate = GetRestTemplate(simpleClientHttpRequestFactory());

    private static final String SERVER_URL = "http://localhost:8083/";
    private static final String file2ocr_URL = SERVER_URL + "pdf2pic";
    private static final String toScanURL = SERVER_URL + "scanPDF";
    private static final String FILE_TEMP_SAVE_PATH = "C:/Users/22533/Desktop/notingDQX/";



    public List<OcrResultNew> doUpload(String md5){
//        待输入的参数
        JSONObject param = new JSONObject();
        param.put("md5",md5);
//        链接地址

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

    public String toScan(MultipartFile file, String filename) throws IOException {

        String savePath = FILE_TEMP_SAVE_PATH + filename;
        Path path = Paths.get(savePath);
        Files.write(path, file.getBytes());

        JSONObject param = new JSONObject();
        param.put("filePath",savePath);
        param.put("filename",filename);

        JSONObject result = restTemplate.postForEntity(toScanURL, param, JSONObject.class).getBody();
        String pathScan = (String) result.get("data");
        System.out.println("pathScan:"+pathScan);
        return pathScan;
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
