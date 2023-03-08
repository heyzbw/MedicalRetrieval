package com.jiaruiblog.controller;

import com.jiaruiblog.entity.dto.SearchOcrDTO;
import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/searchOcr")
public class SearchOcrController {
    @Autowired
    private SearchOcrService searchOcrService;
    @GetMapping("/searchResult")
//    public BaseApiResult Serach(@RequestBody SearchOcrDTO searchOcrDTO) throws IOException {

    public BaseApiResult Serach(@RequestParam String keyword) throws IOException {

        return searchOcrService.searchOcrByKeyword(keyword);
    }

}
