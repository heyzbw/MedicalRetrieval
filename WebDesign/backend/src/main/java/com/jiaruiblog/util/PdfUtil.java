package com.jiaruiblog.util;

import com.jiaruiblog.entity.ContentEachPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName PDFUtil
 * @Description pdf 准换工具
 * @Author luojiarui
 * @Date 2022/7/13 7:55 下午
 * @Version 1.0
 **/
@Slf4j
public class PdfUtil {

    private PdfUtil() {
        throw new IllegalStateException("PdfUtil class error");
    }

    public static void readPdfText(InputStream file, String textPath) throws IOException {
        if (file == null) {
            log.error("inputStream is null");
            return;
        }
        try (
                PDDocument document = PDDocument.load(file);
                FileWriter fileWriter = new FileWriter(textPath, true)
        ) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                ap.setCanExtractContent(true);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
                text = text.replace("\n", "");
                text = text.trim();
                fileWriter.write(text);
            }
        } catch (Exception e) {
            log.error("解析pdf文本文件出错", e);
            throw e;
        }
    }

    public static List<ContentEachPage> readPdfTextEachPage(InputStream file) throws IOException {
        List<ContentEachPage> contentEachPageList = new ArrayList<>();
        if (file == null) {
            log.error("inputStream is null");
            return null;
        }
        try (
                PDDocument document = PDDocument.load(file);
        ) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                ap.setCanExtractContent(true);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
//            stripper.setSortByPosition(true);
            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(document);
//                System.out.println("text:"+text);
                text = text.replace("\n", "");
                text = text.replace("\r", "");
//                System.out.println("text_new:"+text);
//                text = text.trim();
//                System.out.println("读取到的内容为："+text);

                ContentEachPage contentEachPage = new ContentEachPage();
                contentEachPage.setContent(text);
                contentEachPage.setPageNum(p);

                contentEachPageList.add(contentEachPage);
//                text = text.replace("\n", "");
//                text = text.trim();
            }
        } catch (Exception e) {
            log.error("解析pdf文本文件出错", e);
            throw e;
        }
        return contentEachPageList;
    }


    /**
     * @Author luojiarui
     * @Description // pdf 转为png
     * @Date 10:01 下午 2022/7/16
     * @Param [pdfPath]
     **/
    public static void transPdf2png(String pdfPath) {
        try {
            //根据pdf文件路径取得pdf文件
            File invoiceFile = new File(pdfPath);
            // 新建pdf文件的路径
            String path = "test";
            PDDocument doc = PDDocument.load(invoiceFile);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                // 第二个参数是设置缩放比(即像素)
                BufferedImage image = renderer.renderImage(i, 2.5f);
                ImageIO.write(image, "PNG", new File(path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @Author luojiarui
     * @Description //根据文件输入流和图片地址保存缩略图
     * @Date 7:22 下午 2022/7/24
     * @Param [inputStream, picPath]
     **/
    public static void pdfThumbnail(InputStream inputStream, String picPath) {
        try {
            PDDocument doc = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            // 第二个参数是设置缩放比(即像素)
            BufferedImage image = renderer.renderImage(0, 2.0f);
            ImageIO.write(image, "PNG", new File(picPath));
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
