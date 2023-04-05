package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileObj {
    private MultipartFile file;
    private String fileChoice;
    private List<String> labels;
}
