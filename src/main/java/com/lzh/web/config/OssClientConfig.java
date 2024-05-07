package com.lzh.web.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class OssClientConfig implements InitializingBean {

    //读取配置文件的内容
    @Value("${oss.client.region}")
    private String endpoint;
    @Value("${oss.client.accessKey}")
    private String keyId;
    @Value("${oss.client.secretKey}")
    private String keySecret;
    @Value("${oss.client.bucket}")
    private String bucketName;

    //定义公共静态常量
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() {
        END_POINT = endpoint;
        ACCESS_KEY_ID = keyId;
        ACCESS_KEY_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}