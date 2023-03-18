package com.jiaruiblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResult {
    private String _id;
    private String ocrText;
    private String pdfURL;
    private int pdfPage;
    private List<OcrPosition> textResult;
    private String image;

    public OcrResult(String ocrText, String pdfURL, int pdfPage, List<OcrPosition> textResult, String image) {
//        this._id = _id;
        this.ocrText = ocrText;
        this.pdfURL = pdfURL;
        this.pdfPage = pdfPage;
        this.textResult = textResult;
        this.image = image;
    }
}
