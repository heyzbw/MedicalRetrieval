package com.jiaruiblog.service.impl;

import com.jiaruiblog.service.File2OcrService;
import com.jiaruiblog.util.CallFlask;
import org.springframework.stereotype.Service;

@Service
public class File2OcrServiceImpl implements File2OcrService {
    private CallFlask callFlask = new CallFlask();

    @Override
    public void getOcrByPY(String md5) {
        callFlask.doUpload(md5);
    }
}
