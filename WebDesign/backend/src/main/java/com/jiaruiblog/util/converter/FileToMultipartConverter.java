package com.jiaruiblog.util.converter;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.apache.commons.fileupload.disk.DiskFileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import org.apache.commons.io.IOUtils;


public class FileToMultipartConverter {
    public static MultipartFile toMultipartFile(File file) throws IOException {
        DiskFileItem fileItem = new DiskFileItem(file.getName(), Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        try (FileInputStream inputStream = new FileInputStream(file)) {
            IOUtils.copy(inputStream, fileItem.getOutputStream());
        }
        return new CommonsMultipartFile(fileItem);
    }
}

