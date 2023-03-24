package com.jiaruiblog.entity.ocrResult;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EsSearchContent {
    private int pageNum;
    private List<String> contentHighLight = new ArrayList<>();
}
