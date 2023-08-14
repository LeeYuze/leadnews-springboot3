package com.leadnews.app.filter;

import com.github.xiaoymin.knife4j.spring.gateway.utils.StrUtil;
import com.leadnews.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/8/14
 */
@Component
public class AuthorizeFilter implements Ordered, GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizeFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.debug("============【AppGateway】开始登录校验============");

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();

        List<String> unAuthorizePaths = List.of("/login", "/v3/api-docs");
        logger.debug("============不需要校验 --- {}============", unAuthorizePaths);

        logger.debug("当前校验URI地址：{}", path);

        // 判断是否不需要校验
        for (String unAuthPath : unAuthorizePaths) {
            if (path.contains(unAuthPath)) {
                logger.debug("{} 不需要进行校验 --- 网关放行", path);
                return chain.filter(exchange);
            }
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if(StrUtil.isBlank(token)){
            logger.debug("请求地址：{} --- 登录校验失败 token为空", path);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result == 1 || result  == 2){
                logger.debug("请求地址：{} --- 登录校验失败 token过期", path);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        logger.debug("============【AppGateway】结束登录校验============");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
