package com.leadnews.user.service.oauth2;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.leadnews.common.execption.CustomException;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.user.common.enums.user.UserTypeEnum;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2CodeDO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.service.auth.AdminAuthService;
import com.leadnews.user.service.user.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2GrantServiceImpl implements OAuth2GrantService{

    private final OAuth2CodeService oAuth2CodeService;

    private final OAuth2TokenService oAuth2TokenService;

    private final AdminAuthService adminAuthService;

    @Override
    public String grantAuthorizationCodeForCode(Long userId, Integer userType, String clientId, List<String> scopes, String redirectUri, String state) {
        return oAuth2CodeService.createAuthorizationCode(userId, userType, clientId, scopes,
                redirectUri, state).getCode();
    }

    @Override
    public OAuth2AccessTokenDO grantAuthorizationCodeForAccessToken(String clientId, String code, String redirectUri, String state) {
        OAuth2CodeDO codeDO = oAuth2CodeService.consumeAuthorizationCode(code);
        Assert.notNull(codeDO, "授权码不能为空"); // 防御性编程

        // 校验 clientId 是否匹配
        if (!StrUtil.equals(clientId, codeDO.getClientId())) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "client_id 不匹配");
        }

        // TODO 校验 redirectUri 是否匹配
//        if (!StrUtil.equals(redirectUri, codeDO.getRedirectUri())) {
//            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "client_id 不匹配");
//        }

        // 校验 state 是否匹配
        state = StrUtil.nullToDefault(state, ""); // 数据库 state 为 null 时，会设置为 "" 空串
        if (!StrUtil.equals(state, codeDO.getState())) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "state 不匹配");
        }

        // 创建访问令牌
        return oAuth2TokenService.createAccessToken(codeDO.getUserId(), codeDO.getUserType(),
                codeDO.getClientId(), codeDO.getScopes());
    }

    @Override
    public OAuth2AccessTokenDO grantImplicit(Long userId, Integer userType, String clientId, List<String> scopes) {
        return oAuth2TokenService.createAccessToken(userId, userType, clientId, scopes);    }

    @Override
    public OAuth2AccessTokenDO grantPassword(String username, String password, String clientId, List<String> scopes) {
        // 使用账号 + 密码进行登录
        AdminUserDO user = adminAuthService.authenticate(username, password);
        Assert.notNull(user, "用户不能为空！"); // 防御性编程

        // 创建访问令牌
        return oAuth2TokenService.createAccessToken(user.getId(), UserTypeEnum.ADMIN.getValue(), clientId, scopes);
    }

    @Override
    public OAuth2AccessTokenDO grantRefreshToken(String refreshToken, String clientId) {
        return oAuth2TokenService.refreshAccessToken(refreshToken, clientId);
    }
}
