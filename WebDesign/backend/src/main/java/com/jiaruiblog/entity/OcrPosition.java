package com.jiaruiblog.entity;

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
}
