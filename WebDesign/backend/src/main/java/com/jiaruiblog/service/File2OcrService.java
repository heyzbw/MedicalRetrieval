package com.jiaruiblog.service;

import com.jiaruiblog.entity.PredictCaseOutcome;
import com.jiaruiblog.entity.ocrResult.OcrResultNew;

import java.util.List;

public interface File2OcrService {
    public List<OcrResultNew> getOcrByPY(String md5);

    PredictCaseOutcome getPredictCase(String md5);
}
