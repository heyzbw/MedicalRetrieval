package com.jiaruiblog.entity.vo;

import com.jiaruiblog.entity.ocrResult.OcrPosition;
import lombok.Data;

@Data
public class PositionVO {
    private int charNum;
    private boolean isHandwritten;
    private String leftBottom;
    private String leftTop;
    private String rightBottom;
    private String rightTop;
    private String text;
    public PositionVO(OcrPosition ocrPosition){
        charNum =     ocrPosition.getCharNum();
//        isHandwritten  = ocrPosition.g();
        leftBottom   = ocrPosition.getLeftBottom();
        leftTop      = ocrPosition.getLeftTop();
        rightBottom  = ocrPosition.getRightBottom();
        rightTop     = ocrPosition.getRightTop();
        text         = ocrPosition.getText();
    }
}
