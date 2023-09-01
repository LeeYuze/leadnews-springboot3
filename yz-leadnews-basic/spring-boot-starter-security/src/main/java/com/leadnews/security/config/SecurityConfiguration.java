package com.leadnews.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 开启跨域
                .cors().and()
                // CSRF 禁用，因为不使用 Session
                .csrf().disable()
                // 基于 token 机制，所以不需要 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable()
                .and() // 禁用默认登录页
                .formLogin().disable()
                // 禁用默认登出页
                .logout().disable();

        // 设置每个请求的权限
        httpSecurity
                .authorizeHttpRequests(chain -> chain
                        .requestMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
                        .requestMatchers(HttpMethod.POST,"/system/auth/login").permitAll()
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }
}
