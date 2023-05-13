package com.jiaruiblog.entity;

import com.jiaruiblog.entity.vo.DocumentVO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class Diagnosis {

    private String userId;
    private Date createDate;
    private List<ImageInfo> imageInfos;
    private MultipartFile[] files;
    private List<DocumentVO> documentVos;
    private PredictCaseOutcome predictCaseOutcome;


}
