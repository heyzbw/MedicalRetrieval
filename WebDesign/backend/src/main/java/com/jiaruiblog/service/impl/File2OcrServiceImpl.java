package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.OcrResult;
import com.jiaruiblog.service.File2OcrService;
import com.jiaruiblog.util.CallFlask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class File2OcrServiceImpl implements File2OcrService {
    private CallFlask callFlask = new CallFlask();

    @Override
    public List<OcrResult> getOcrByPY(String md5) {
        List<OcrResult> ocrResultList = callFlask.doUpload(md5);
        return ocrResultList;
    }
}
