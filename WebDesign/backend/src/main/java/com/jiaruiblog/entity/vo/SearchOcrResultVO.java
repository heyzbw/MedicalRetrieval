package com.jiaruiblog.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchOcrResultVO {

    private String ocrText;
    private String pdfURL;
    private int pdfPage ;

    private List<PositionVO> positionVOS;
    private byte[] image;

}
