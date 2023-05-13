package com.jiaruiblog.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName ElasticSearchConfig
 * @Description ES的配置信息
 * @Author luojiarui
 * @Date 2022/7/12 10:50 下午
 * @Version 1.0
 **/
@Component
public class ElasticSearchConfig {

    @Value("${cloud.elasticsearch.host}")
    private String esHost;

    @Value("${cloud.elasticsearch.port}")
    private int esPort;

    @Value("${cloud.elasticsearch.username}")
    private String username;

    @Value("${cloud.elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient restClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(esHost, esPort), new UsernamePasswordCredentials(username, password));

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(esHost, esPort, "http"))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
    }
    // 在业务启动的时候进行初始化
    // https://blog.csdn.net/wdz985721191/article/details/122866091
}
