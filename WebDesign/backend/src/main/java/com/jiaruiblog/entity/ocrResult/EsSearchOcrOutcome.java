package com.jiaruiblog.entity.ocrResult;

import lombok.Data;

@Data
public class EsSearchOcrOutcome {
    private String ocrText;
    private String mongoDbId;
}
