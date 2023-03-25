package com.jiaruiblog.entity.ocrResult;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class EsSearch {

    @Id
    private String id;

    private String fileId;

    private String md5;

    private String type;

    private String name;


    private String gridfsId;

    //    来自文本的检索分数
    private double contentScore;
    //    来做点击率的分数
    private double clickScore;
    //    来做点赞数量的分数
    private double likeScore;


    private String thumbId;

    private Date date;

    private List<EsSearchOcrOutcome> esSearchOcrOutcomeList = new ArrayList<>();
    private List<EsSearchContent> esSearchContentList = new ArrayList<>();

    private static final String DB_NAME = "ocr_result";

//    最终展示时使用的ocrResult
    private List<OcrResult> ocrResultList;



    public void readOcrResultFromDB(){
        for(EsSearchOcrOutcome esSearchOcrOutcome:esSearchOcrOutcomeList){
            if(esSearchOcrOutcome.getOcrText() != null)
            {
                String mongoDB_id = esSearchOcrOutcome.getMongoDbId();
            }
        }
    }


}
