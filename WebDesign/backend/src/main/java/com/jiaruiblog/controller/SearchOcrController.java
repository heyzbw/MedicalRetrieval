package com.jiaruiblog.controller;

import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/searchOcr/")
public class SearchOcrController {
    @Autowired
    private SearchOcrService searchOcrService;
    @GetMapping("searchResult")
    public BaseApiResult Serach() throws IOException {
        System.out.println("aaaa");
        searchOcrService.searchOcrByKeyword("WRA");
        return null;
    }


}
