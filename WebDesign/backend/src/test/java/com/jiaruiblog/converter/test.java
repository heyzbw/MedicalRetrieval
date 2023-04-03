package com.jiaruiblog.converter;

import com.itextpdf.text.DocumentException;
import com.jiaruiblog.util.converter.ImageToPdfConverter;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {

    @Test
    public void testForImage2Pdf() throws DocumentException, IOException {
        ImageToPdfConverter imageToPdfConverter = new ImageToPdfConverter();
        List<String> files = new ArrayList<>();
        files.add("C:\\Users\\22533\\Desktop\\image\\test1.jpg");
        files.add("C:\\Users\\22533\\Desktop\\image\\test2.jpg");
//        imageToPdfConverter.convertImagesToPdf(files,"output3.pdf");
    }
}
