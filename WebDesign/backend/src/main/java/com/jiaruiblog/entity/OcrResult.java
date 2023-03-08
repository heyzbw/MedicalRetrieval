package com.jiaruiblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResult {
    private String _id;
    private String ocrText;
    private String pdfURL;
    private int pdfPage;
    private OcrPosition[] textResult;
    private String image;

}
