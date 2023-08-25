package com.leadnews.wemedia.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihaohui
 * @date 2023/8/25
 */
@Configuration
@ComponentScan("com.leadnews.apis.article.fallback")
public class FeignConfig {
}
