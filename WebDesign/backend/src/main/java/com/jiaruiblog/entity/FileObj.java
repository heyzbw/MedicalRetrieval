package com.jiaruiblog.entity;

import lombok.Data;

import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileObj
 * @Description FileObj
 * @Author luojiarui
 * @Date 2022/7/3 10:47 下午
 * @Version 1.0
 **/
@Data
@Document(indexName = "docwrite")
public class FileObj {

    /**
     * 用于存储文件id
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String fileId;

    /**
     * 文件名
     */
    @Field(type = FieldType.Text, analyzer="ik_max_word")
    private String name;

    /**
     * 文件的type，pdf，word，or txt
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 文件转化成base64编码后所有的内容。
     */


    @Field(type = FieldType.Text, analyzer="ik_smart")
    private String content;

    @Field(type = FieldType.Text, analyzer="synonyms")
    private String syn;

    @Field(type = FieldType.Text, analyzer="no_synonyms")
    private String no_syn;


    @Field(type = FieldType.Integer)
    private int click_rate = 0;

    @Field(type = FieldType.Integer)
    private int like_num = 0;

    @Field(type = FieldType.Integer)
    private int collect_num = 0;

//    OCR结果的字段
    @Field(type = FieldType.Nested, analyzer="ik_max_word")
    private List<OcrResult> ocrResultList;

    private Map<String, Object> jsonMap = new HashMap<>();


    public void readFile(String path) throws TikaException, IOException, SAXException {
        //读文件
        File file = new File(path);
        byte[] bytes = getContent(file);
        //将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);

        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler(-1); // 设置字符数限制为-1，以获取所有内容
        parser.parse(new ByteArrayInputStream(bytes), handler, metadata,new ParseContext());
        jsonMap.put("content", handler.toString());
        jsonMap.put("attachment", bytes);
        jsonMap.put("metadata", metadata);
    }

    public void readFile(InputStream inputStream) {
        byte[] bytes = getContent(inputStream);
        //将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] getContent(File file) {
        byte[] bytesArray = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            if (fileInputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    private byte[] getContent(InputStream inputStream) {
        byte[] bytesArray = new byte[]{};
        try {
            if (inputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

}
