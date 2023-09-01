package com.leadnews.user.service.auth;

import com.leadnews.common.execption.CustomException;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.user.common.enums.oauth2.OAuth2ClientConstants;
import com.leadnews.user.common.enums.user.UserTypeEnum;
import com.leadnews.user.controller.auth.vo.AuthLoginReqVO;
import com.leadnews.user.controller.auth.vo.AuthLoginRespVO;
import com.leadnews.user.convert.AuthConvert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.service.oauth2.Oauth2TokenService;
import com.leadnews.user.service.user.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author lihaohui
 * @date 2023/8/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserService userService;

    private final Oauth2TokenService oauth2TokenService;


    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {

        // TODO yz 校验验证码

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 创建 Token 令牌
        return createTokenAfterLoginSuccess(user.getId(), user.getUsername());
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username) {
        // TODO yz 插入登陆日志

        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);

        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }


    @Override
    public AdminUserDO authenticate(String username, String password) {
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);

        if (!userService.isPasswordMatch(password, user.getPassword())) {
            throw new CustomException(AppHttpCodeEnum.AUTH_LOGIN_BAD_CREDENTIALS);
        }

        // TODO yz 校验是否禁用
        return user;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }
}
