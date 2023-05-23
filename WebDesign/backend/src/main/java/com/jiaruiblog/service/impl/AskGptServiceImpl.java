package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.AskOptional;
import com.jiaruiblog.service.AskGptService;
import com.jiaruiblog.util.AskGpt;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class AskGptServiceImpl implements AskGptService {

    private final AskGpt askGpt = new AskGpt();
    @Override
    public BaseApiResult askGpt(String question) {
        if(StringUtils.hasText(question)){
            return BaseApiResult.success(askGpt.ask(AskOptional.ASK,question));
        }
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.PARAMS_IS_NOT_NULL);

    }

    @Override
    public BaseApiResult summarize(String content) {
        if(StringUtils.hasText(content)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.PARAMS_IS_NOT_NULL);
        }
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.PARAMS_IS_NOT_NULL);
    }
}
