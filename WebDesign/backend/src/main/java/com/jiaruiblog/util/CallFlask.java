package com.jiaruiblog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.entity.PredictCaseOutcome;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallFlask {

    private RestTemplate restTemplate = GetRestTemplate(simpleClientHttpRequestFactory());

   private static final String SERVER_URL = "http://localhost:5000/";

    // private static final String SERVER_URL = "http://121.36.201.185:8083/";
    private static final String file2ocr_URL = SERVER_URL + "pdf2pic";
    private static final String toScanURL = SERVER_URL + "scanPDF";
    private static final String toPredictCase = SERVER_URL + "predictCase";
//    private static final String FILE_TEMP_SAVE_PATH = "C:/Users/22533/Desktop/notingDQX/";



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

        String savePath = System.getProperty("java.io.tmpdir") + File.separator + filename;
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

    public PredictCaseOutcome predict_case(String md5) {
        // 待输入的参数
        JSONObject param = new JSONObject();
        param.put("md5",md5);

        // 链接地址
        JSONObject result = restTemplate.postForEntity(toPredictCase,param,JSONObject.class).getBody();
        JSONObject dataObject = result.getJSONObject("data");

        PredictCaseOutcome outcome = new PredictCaseOutcome();

        // 设置PredictCaseOutcome对象的字段
        outcome.setDiagnosis((dataObject.getString("diagnosis")));
        outcome.setDisease(jsonArrayToList(dataObject.getJSONArray("disease")));
//        outcome.getDisease().add(0,"肺癌");
        outcome.setBody(jsonArrayToList(dataObject.getJSONArray("body")));
        outcome.setSymptom(jsonArrayToList(dataObject.getJSONArray("symptom")));
        outcome.setMedicalProcedure(jsonArrayToList(dataObject.getJSONArray("medicalProcedure")));
        outcome.setMedicalEquipment(jsonArrayToList(dataObject.getJSONArray("medicalEquipment")));
        outcome.setMedicine(jsonArrayToList(dataObject.getJSONArray("medicine")));
        outcome.setDepartment(jsonArrayToList(dataObject.getJSONArray("department")));
        outcome.setMicroorganism(jsonArrayToList(dataObject.getJSONArray("microorganism")));
        outcome.setMedicalExamination(jsonArrayToList(dataObject.getJSONArray("medicalExamination")));

        //现在，outcome对象包含了所有的解析数据

        return outcome;

    }

    // 辅助方法将JSONArray转换为List<String>
    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
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
