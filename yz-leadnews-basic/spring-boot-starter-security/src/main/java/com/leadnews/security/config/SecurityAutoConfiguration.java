package com.leadnews.security.config;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lihaohui
 * @date 2023/8/31
 */
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    @Resource
    private SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPasswordEncoderLength());
    }
}
