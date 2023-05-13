package com.jiaruiblog.entity.vo;

import com.jiaruiblog.entity.PredictCaseOutcome;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
@Data
public class DiagnosisVO {
    private String userId;
    private Date createTime;
    private MultipartFile[] files;
    private List<String> UUID;
    private List<String> ills;
    private String illType;
    private PredictCaseOutcome predictCaseOutcome;
    private List<DocumentVO> documentVos;
}
