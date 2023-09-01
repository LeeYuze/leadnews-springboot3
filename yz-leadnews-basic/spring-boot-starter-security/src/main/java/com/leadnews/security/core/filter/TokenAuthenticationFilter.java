package com.leadnews.security.core.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.protobuf.ServiceException;
import com.leadnews.apis.oauth2.OAuth2TokenApi;
import com.leadnews.apis.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.leadnews.security.core.LoginUser;
import com.leadnews.security.core.utils.SecurityFrameworkUtils;
import com.leadnews.security.core.utils.WebFrameworkUtils;
import com.leadnews.security.core.utils.json.JsonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";

    private final OAuth2TokenApi oauth2TokenApi;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
        LoginUser loginUser = buildLoginUserByHeader(request);

        // 情况二，基于 Token 获得用户
        // 注意，这里主要满足直接使用 Nginx 直接转发到 Spring Cloud 服务的场景。
        if (loginUser == null) {
            String token = SecurityFrameworkUtils.obtainAuthorization(request, AUTHORIZATION);
            if (StrUtil.isNotEmpty(token)) {
                Integer userType = WebFrameworkUtils.getLoginUserType(request);

                loginUser = buildLoginUserByToken(token, userType);
            }
        }

        // 设置当前用户
        if (loginUser != null) {
            SecurityFrameworkUtils.setLoginUser(loginUser, request);
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }


    private LoginUser buildLoginUserByToken(String token, Integer userType) {
        // 校验访问令牌
        OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getData();
        if (accessToken == null) {
            return null;
        }
        // 用户类型不匹配，无权限
        if (ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
            throw new AccessDeniedException("错误的用户类型");
        }
        return new LoginUser().setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
                .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes());
    }


    private LoginUser buildLoginUserByHeader(HttpServletRequest request) {
        String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
        return StrUtil.isNotEmpty(loginUserStr) ? JsonUtils.parseObject(loginUserStr, LoginUser.class) : null;
    }

}
