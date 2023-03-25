package com.jiaruiblog.mongodb;

import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.service.impl.ElasticServiceImpl;
import com.jiaruiblog.service.impl.SearchOcrServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class TestForMongoDB {

    private SearchOcrService searchOcrService = new SearchOcrServiceImpl();
    @Test
    public void testForMongodb() throws IOException {
        searchOcrService.searchOcrByKeyword("URA");
    }

    @Test
    public void testForRPN() throws IOException {
        ElasticServiceImpl elasticService = new ElasticServiceImpl();
        elasticService.search_high("(dqx | (( zbw & PYB )))");
    }
}
