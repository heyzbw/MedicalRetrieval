package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MultiFilesUploadObj {

    private MultipartFile[] files;
    private String userId;
    private String username;
}
