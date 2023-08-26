package com.leadnews.minio.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Configuration
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioConfig {

    @Resource
    private MinioConfigProperties minioConfigProperties;

    @Bean
    public MinioClient buildMinioClient() {
        return MinioClient.builder()
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .endpoint(minioConfigProperties.getEndpoint())
                .build();
    }

}
