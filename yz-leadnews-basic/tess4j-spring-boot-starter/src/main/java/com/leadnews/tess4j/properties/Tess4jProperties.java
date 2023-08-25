package com.leadnews.tess4j.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lihaohui
 * @date 2023/8/25
 */
@Data
@ConfigurationProperties("tess4j")
public class Tess4jProperties {
    /**
     * 字体库路径
     */
    private String dataPath;

    /**
     * 语言
     */
    private String language;
}
