package com.jiaruiblog.mongodb;

import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.service.impl.ElasticServiceImpl;
import com.jiaruiblog.service.impl.SearchOcrServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

import java.io.IOException;

public class TestForMongoDB {

    private SearchOcrService searchOcrService = new SearchOcrServiceImpl();
    @Test
    public void testForMongodb() throws IOException {
        searchOcrService.searchOcrByKeyword("URA");
    }

    @Test
    public void testForRPN() throws IOException {
//        ElasticServiceImpl elasticService = new ElasticServiceImpl();
//        elasticService.search_advance("研究 | 癌症");
        List<String> stringList = Arrays.asList("apple", "banana", "orange");

        String targetString = "bananas";

        boolean isMatched = stringList.stream()
                .anyMatch(targetString::contains);

        System.out.println("The target string contains any of the strings in the list: " + isMatched);
    }
}
