package com.leadnews.security.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.leadnews.security.core.filter.TokenAuthenticationFilter;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    @Resource
    private ApplicationContext applicationContext;


    /**
     * 认证失败处理类 Bean
     */
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    /**
     * 权限不够处理器 Bean
     */
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Resource
    private TokenAuthenticationFilter authenticationTokenFilter;

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

        httpSecurity.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        Multimap<HttpMethod, String> permitAllUrls = getPermitAllUrlsFromAnnotations();


        // 设置每个请求的权限
        httpSecurity
                .authorizeHttpRequests(chain -> chain
                        .requestMatchers(HttpMethod.GET, "/*.html", "/*/*.html", "/*/*.css", "/*/*.js").permitAll()
                        .requestMatchers(HttpMethod.GET, "/system/oauth2/token/check").permitAll()
                        .requestMatchers(HttpMethod.POST, "/system/auth/login", "/system/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.GET, permitAllUrls.get(HttpMethod.GET).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.POST, permitAllUrls.get(HttpMethod.POST).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PUT, permitAllUrls.get(HttpMethod.PUT).toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.DELETE, permitAllUrls.get(HttpMethod.DELETE).toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                );

        // 添加 Token Filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    private Multimap<HttpMethod, String> getPermitAllUrlsFromAnnotations() {
        Multimap<HttpMethod, String> result = HashMultimap.create();
        // 获得接口对应的 HandlerMethod 集合
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // 获得有 @PermitAll 注解的接口
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(PermitAll.class)) {
                continue;
            }
            if (entry.getKey().getPatternsCondition() == null) {
                continue;
            }
            Set<String> urls = entry.getKey().getPatternsCondition().getPatterns();
            // 根据请求方法，添加到 result 结果
            entry.getKey().getMethodsCondition().getMethods().forEach(requestMethod -> {
                switch (requestMethod) {
                    case GET:
                        result.putAll(HttpMethod.GET, urls);
                        break;
                    case POST:
                        result.putAll(HttpMethod.POST, urls);
                        break;
                    case PUT:
                        result.putAll(HttpMethod.PUT, urls);
                        break;
                    case DELETE:
                        result.putAll(HttpMethod.DELETE, urls);
                        break;
                }
            });
        }
        return result;
    }

}
