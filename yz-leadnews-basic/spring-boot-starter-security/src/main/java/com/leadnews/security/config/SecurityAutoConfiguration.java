package com.leadnews.security.config;

import com.leadnews.apis.oauth2.OAuth2TokenApi;
import com.leadnews.security.core.filter.TokenAuthenticationFilter;
import com.leadnews.security.core.handler.AccessDeniedHandlerImpl;
import com.leadnews.security.core.handler.AuthenticationEntryPointImpl;
import com.leadnews.security.core.service.SecurityFrameworkService;
import com.leadnews.security.core.service.SecurityFrameworkServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

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

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public TokenAuthenticationFilter authenticationTokenFilter(OAuth2TokenApi oauth2TokenApi) {
        return new TokenAuthenticationFilter(oauth2TokenApi);
    }

    @Bean("ss") // 使用 Spring Security 的缩写，方便使用
    public SecurityFrameworkService securityFrameworkService() {
        return new SecurityFrameworkServiceImpl();
    }

}
