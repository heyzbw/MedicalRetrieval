package com.jiaruiblog.service;

import com.jiaruiblog.util.BaseApiResult;

public interface AskGptService {
    public BaseApiResult askGpt(String question);
    public BaseApiResult summarize(String content);
}
