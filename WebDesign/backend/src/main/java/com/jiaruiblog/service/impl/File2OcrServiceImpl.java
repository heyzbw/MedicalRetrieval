package com.jiaruiblog.service.impl;

import com.jiaruiblog.service.File2OcrService;
import com.jiaruiblog.util.CallFlask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class File2OcrServiceImpl implements File2OcrService {
    private CallFlask callFlask = new CallFlask();

    @Override
    public void getOcrByPY(String md5) {
        callFlask.doUpload(md5);
    }
}
