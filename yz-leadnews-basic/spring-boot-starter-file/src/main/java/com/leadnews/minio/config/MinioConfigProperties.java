package com.leadnews.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioConfigProperties implements Serializable {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}
