package com.jiaruiblog.controller;

import com.jiaruiblog.entity.dto.AskGptDTO;
import com.jiaruiblog.service.AskGptService;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "GPT聊天模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/askGpt")
public class AskGptController {
    @Resource
    AskGptService askGptService;

    @ApiOperation(value = "GPT普通问答", notes = "GPT普通问答")
    @PostMapping(value = "/askGPT")
    public BaseApiResult askGPT(@RequestBody AskGptDTO AskGptDTO) {
        return askGptService.askGpt(AskGptDTO.getQuestion());
    }
    @ApiOperation(value = "GPT文本摘要", notes = "GPT文本摘要")
    @PostMapping(value = "/summarize")
    public BaseApiResult summarize(String content) {
        return askGptService.summarize(content);
    }

}
