package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.OcrPosition;
import com.jiaruiblog.entity.OcrResult;
import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SearchOcrServiceImpl implements SearchOcrService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public BaseApiResult searchOcrByKeyword(String keyword) throws IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("ocrText").regex(Pattern.compile(keyword)));
        List<OcrResult> ocrResults = mongoTemplate.find(query, OcrResult.class,"Ocr_result");
//        System.out.println("ocrResults"+ocrResults);
        for(int i=0;i<ocrResults.size();i++)
        {
//            OcrPosition ocrPosition = ocrResults.get(i);
            byte[] imageBytes = Base64.getDecoder().decode(ocrResults.get(i).getImage());
//            FileOutputStream fos = new FileOutputStream("D:/test/image"+i+".png");
//            fos.write(imageBytes);
//            fos.close();
        }
        return BaseApiResult.success(ocrResults.get(0));
    }
}
