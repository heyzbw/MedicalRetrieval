package com.jiaruiblog.entity;

import lombok.Data;

@Data
public class ImageInfo {
    private String filename;
    private String fileType;
    private String UUID;

    public ImageInfo(String filename, String fileType, String UUID) {
        this.filename = filename;
        this.fileType = fileType;
        this.UUID = UUID;
    }
}
