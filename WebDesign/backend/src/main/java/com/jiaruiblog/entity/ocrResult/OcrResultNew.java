package com.jiaruiblog.entity.ocrResult;

import lombok.Data;

import java.io.Serializable;

@Data
public class OcrResultNew implements Serializable {
    private String ocrText;
    private String mongodb_id;
}
