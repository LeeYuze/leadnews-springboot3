package com.leadnews.knife4j.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihaohui
 * @date 2023/8/14
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("黑马头条 - 系统API")
                        .version("1.0")
                        .description("黑马头条 - 系统API")
                        .license(new License().name("Apache 2.0")
                                .url("http://doc.xiaominfo.com")));
    }

}
