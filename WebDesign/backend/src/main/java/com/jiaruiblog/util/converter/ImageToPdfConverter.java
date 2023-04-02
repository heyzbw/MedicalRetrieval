package com.jiaruiblog.util.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageToPdfConverter {

    public MultipartFile convertImagesToPdf(List<String> filePaths, String filename) throws IOException, DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        for (String filePath : filePaths) {
            Image image = Image.getInstance(filePath);
            image.setBorderWidth(0);
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

            // Set the image position to the top of the page
            float yOffset = PageSize.A4.getHeight() - image.getScaledHeight();
            image.setAbsolutePosition(0, yOffset);

            document.setPageSize(PageSize.A4);
            document.newPage();
            document.add(image);
        }


        document.close();

        // Create a temporary file to store the PDF
        String tempPdfFilePath = FilenameUtils.concat(FileUtils.getTempDirectoryPath(), filename);
        System.out.println("tempPdfFilePath:"+tempPdfFilePath);
        File tempPdfFile = new File(tempPdfFilePath);
        FileUtils.writeByteArrayToFile(tempPdfFile, outputStream.toByteArray());

        // Convert the temporary file to a MultipartFile
        MultipartFile multipartFile = FileToMultipartConverter.toMultipartFile(tempPdfFile);
//        tempPdfFile.delete(); // Delete the temporary file

        return multipartFile;
    }
}


