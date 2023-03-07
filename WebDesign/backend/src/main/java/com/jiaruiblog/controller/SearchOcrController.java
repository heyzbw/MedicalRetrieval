package com.jiaruiblog.controller;

import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/searchOcr/")
public class SearchOcrController {
    @Autowired
    private SearchOcrService searchOcrService;
//    @GetMapping("searchResult")
//    public BaseApiResult Serach() throws IOException {
//        System.out.println("aaaa");
//        searchOcrService.searchOcrByKeyword("WRA");
//        return null;
//    }


    @PostMapping("searchResult")
    public BaseApiResult hello(@RequestParam String keyword) throws IOException {
//        String keyword = (String) data.get("keyword");
        System.out.println("keyword"+keyword);
        return searchOcrService.searchOcrByKeyword(keyword);
    }
//    @PostMapping("searchResult")
//    public BaseApiResult hello(@RequestBody Map<String, Object> data) throws IOException {
//        String keyword = (String) data.get("keyword");
//        System.out.println("keyword"+keyword);
//        return searchOcrService.searchOcrByKeyword(keyword);
//    }
}
