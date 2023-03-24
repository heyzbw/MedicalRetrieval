package com.jiaruiblog.entity.ocrResult;

import lombok.Data;

@Data
public class OcrPosition {
    private int charNum;
    private boolean isHandwritten;
    private String leftBottom;
    private String leftTop;
    private String  rightBottom;
    private String  rightTop;
    private String  text;

    public OcrPosition(int charNum, boolean isHandwritten, String leftBottom, String leftTop, String rightBottom, String rightTop, String text) {
        this.charNum = charNum;
        this.isHandwritten = isHandwritten;
        this.leftBottom = leftBottom;
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
        this.rightTop = rightTop;
        this.text = text;
    }
}
