package com.leadnews.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "spring.security")
@Validated
@Data
public class SecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     */
    private String tokenHeader = "Authorization";

    /**
     * mock 模式的开关
     */
    private Boolean mockEnable = false;
    /**
     * mock 模式的密钥
     * 一定要配置密钥，保证安全性
     */
    private String mockSecret = "test";

    /**
     * 免登录的 URL 列表
     */
    private List<String> permitAllUrls = Collections.emptyList();

    /**
     * PasswordEncoder 加密复杂度，越高开销越大
     */
    private Integer passwordEncoderLength = 4;

}
