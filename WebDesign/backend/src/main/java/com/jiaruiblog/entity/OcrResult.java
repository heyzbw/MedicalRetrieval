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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private OcrPosition[] textResult;

    private String image;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public OcrPosition[] getTextResult() {
        return textResult;
    }

    public void setTextResult(OcrPosition[] textResult) {
        this.textResult = textResult;
    }
}
