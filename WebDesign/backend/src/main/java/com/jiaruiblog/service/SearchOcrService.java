package com.jiaruiblog.service;

import com.jiaruiblog.util.BaseApiResult;

import java.io.IOException;

public interface SearchOcrService {
    public BaseApiResult searchOcrByKeyword(String keyword) throws IOException;
}
