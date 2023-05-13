package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.Diagnosis;
import com.jiaruiblog.entity.PredictCaseOutcome;
import com.jiaruiblog.entity.vo.DocumentVO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class DiagnosisMongodbService {
    private static final String DOCUMENT_NAME_DIAGNOSIS = "diagnosis";

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveRecordToDB(HttpServletRequest request,
                               MultipartFile[] files,
                               List<DocumentVO> documentVos,
                               PredictCaseOutcome predictCaseOutcome){
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setUserId("ZBW");
        diagnosis.setCreateDate(new Date());
        diagnosis.setFiles(files);
        diagnosis.setDocumentVos(documentVos);
        diagnosis.setPredictCaseOutcome(predictCaseOutcome);

//        mongoTemplate.save(diagnosis, DOCUMENT_NAME);

    }
}
