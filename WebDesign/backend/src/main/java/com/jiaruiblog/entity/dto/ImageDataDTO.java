package com.jiaruiblog.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageDataDTO {
    private String filename;
    private MultipartFile[] imageList;

}

