package com.jiaruiblog.service;

import com.jiaruiblog.entity.OcrResult;

import java.util.List;

public interface File2OcrService {
    public List<OcrResult> getOcrByPY(String md5);
}
