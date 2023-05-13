package com.jiaruiblog.entity;

import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.vo.DocumentVO;
import lombok.Data;

import java.util.List;

@Data
public class PredictionCaseEntity {
    private PredictCaseOutcome predictCaseOutcome;
    private List<DocumentVO> documentVos;
//    private DocumentDTO documentDTO;
}
