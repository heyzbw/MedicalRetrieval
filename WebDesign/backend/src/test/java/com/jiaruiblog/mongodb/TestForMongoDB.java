package com.jiaruiblog.mongodb;

import com.jiaruiblog.service.SearchOcrService;
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
}
