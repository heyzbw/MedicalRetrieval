package com.jiaruiblog.entity.ocrResult;

import lombok.Data;

@Data
public class OcrPosition {

    private int[] position;
    private String leftBottom;
    private String leftTop;
    private String  rightBottom;
    private String  rightTop;
    private String  text;

    public OcrPosition(String leftBottom, String leftTop, String rightBottom, String rightTop, String text) {

        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
        this.rightTop = rightTop;
        this.text = text;
    }
}
