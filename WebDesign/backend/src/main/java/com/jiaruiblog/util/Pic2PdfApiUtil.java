package com.jiaruiblog.util;
import com.aliyun.docmind_api20220711.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.docmind_api20220711.Client;

import java.util.Arrays;

public class Pic2PdfApiUtil {

    static String accessKeyId = "LTAI5tEHgbuGyYxtAa56qGBd";
    static String accessKeySecret = "M04UWqLdevse1Dn8QwMd3ILSbFKLHQ";
    public static void submit() throws Exception {
        Config config = new Config()
                // 前面准备好的您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 前面准备好的您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名，支持ipv4和ipv6两种方式，ipv6请使用docmind-api-dualstack.cn-hangzhou.aliyuncs.com
        config.endpoint = "docmind-api.cn-hangzhou.aliyuncs.com";
        Client client = new Client(config);
        SubmitConvertImageToPdfJobRequest imageToPdfJobRequest = new SubmitConvertImageToPdfJobRequest();
        String fileUrl1 = "https://example.com/example1.jpg";
        String fileUrl2 = "https://example.com/example2.jpg";
        imageToPdfJobRequest.imageUrls = Arrays.asList(fileUrl1, fileUrl2);
        // 填写第一张图片的文件扩展名
        imageToPdfJobRequest.imageNameExtension = "jpg";
        SubmitConvertImageToPdfJobResponse response = client.submitConvertImageToPdfJob(imageToPdfJobRequest);
    }

    public static void main(String args[]) throws Exception {
        submit();
    }
}
