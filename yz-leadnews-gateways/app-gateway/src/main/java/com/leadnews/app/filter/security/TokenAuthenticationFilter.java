package com.leadnews.app.filter.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leadnews.apis.oauth2.OAuth2TokenApi;
import com.leadnews.app.dto.OAuth2AccessTokenCheckRespDTO;
import com.leadnews.app.utils.JsonUtils;
import com.leadnews.app.utils.SecurityFrameworkUtils;
import com.leadnews.app.utils.WebFrameworkUtils;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Slf4j
@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    private static final TypeReference<ResponseResult<OAuth2AccessTokenCheckRespDTO>> CHECK_RESULT_TYPE_REFERENCE
            = new TypeReference<ResponseResult<OAuth2AccessTokenCheckRespDTO>>() {
    };

    private static final LoginUser LOGIN_USER_EMPTY = new LoginUser();


    private final WebClient webClient;

    public TokenAuthenticationFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        // Q：为什么不使用 OAuth2TokenApi 进行调用？
        // A1：Spring Cloud OpenFeign 官方未内置 Reactive 的支持 https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#reactive-support
        // A2：校验 Token 的 API 需要使用到 header[tenant-id] 传递租户编号，暂时不想编写 RequestInterceptor 实现
        // 因此，这里采用 WebClient，通过 lbFunction 实现负载均衡
        this.webClient = WebClient.builder().filter(lbFunction).build();
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 移除 login-user 的请求头，避免伪造模拟
        SecurityFrameworkUtils.removeLoginUser(exchange);

        // 情况一，如果没有 Token 令牌，则直接继续 filter
        String token = SecurityFrameworkUtils.obtainAuthorization(exchange);
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        // 情况二，如果有 Token 令牌，则解析对应 userId、userType 等字段，并通过 通过 Header 转发给服务
        return getLoginUser(exchange, token).defaultIfEmpty(LOGIN_USER_EMPTY).flatMap(user -> {
            // 1. 无用户，直接 filter 继续请求
            if (user == LOGIN_USER_EMPTY) {
                return chain.filter(exchange);
            }

            // 2.1 有用户，则设置登录用户
            SecurityFrameworkUtils.setLoginUser(exchange, user);

            log.info("Token令牌 {} - 用户信息 - {}", token, user);

            // 2.2 将 user 并设置到 login-user 的请求头，使用 json 存储值
            ServerWebExchange newExchange = exchange.mutate()
                    .request(builder -> SecurityFrameworkUtils.setLoginUserHeader(builder, user))
                    .build();
            return chain.filter(newExchange);
        });
    }

    private Mono<LoginUser> getLoginUser(ServerWebExchange exchange, String token) {
        // 从缓存中，获取 LoginUser
//        Long tenantId = WebFrameworkUtils.getTenantId(exchange);
//        KeyValue<Long, String> cacheKey = new KeyValue<Long, String>().setKey(tenantId).setValue(token);
//        LoginUser localUser = loginUserCache.getIfPresent(cacheKey);
//        if (localUser != null) {
//            return Mono.just(localUser);
//        }
        // TODO yz 先查用户缓存，如果命中就返回

        // 缓存不存在，则请求远程服务
        return checkAccessToken(token).flatMap((Function<String, Mono<LoginUser>>) body -> {
            LoginUser remoteUser = buildUser(body);
            if (remoteUser != null) {
                // 非空，则进行缓存
                // TODO yz 保存用户缓存
                // loginUserCache.put(cacheKey, remoteUser);
                return Mono.just(remoteUser);
            }
            return Mono.empty();
        });
    }

    private Mono<String> checkAccessToken(String token) {
        return webClient.get()
                .uri(OAuth2TokenApi.URL_CHECK, uriBuilder -> uriBuilder.queryParam("accessToken", token).build())
                .retrieve()
                .bodyToMono(String.class);
    }

    private LoginUser buildUser(String body) {
        // 处理结果，结果不正确
        ResponseResult<OAuth2AccessTokenCheckRespDTO> result = JsonUtils.parseObject(body, CHECK_RESULT_TYPE_REFERENCE);
        if (result == null) {
            return null;
        }
        if (!result.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            // 特殊情况：令牌已经过期（code = 401），需要返回 LOGIN_USER_EMPTY，避免 Token 一直因为缓存，被误判为有效
            if (Objects.equals(result.getCode(), HttpStatus.UNAUTHORIZED.value())) {
                return LOGIN_USER_EMPTY;
            }
            return null;
        }

        // 创建登录用户
        OAuth2AccessTokenCheckRespDTO tokenInfo = result.getData();
        return new LoginUser().setId(tokenInfo.getUserId()).setUserType(tokenInfo.getUserType())
                .setScopes(tokenInfo.getScopes());
    }


    @Override
    public int getOrder() {
        return -1900;
    }
}
