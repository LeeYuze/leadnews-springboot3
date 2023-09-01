package com.leadnews.user.api;

import com.leadnews.apis.oauth2.OAuth2TokenApi;
import com.leadnews.apis.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.leadnews.apis.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.leadnews.apis.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.user.convert.OAuth2TokenConvert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.service.oauth2.Oauth2TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@RequiredArgsConstructor
public class OAuth2TokenApiImpl implements OAuth2TokenApi {

    private final Oauth2TokenService oauth2TokenService;


    @Override
    public ResponseResult<OAuth2AccessTokenRespDTO> createAccessToken(OAuth2AccessTokenCreateReqDTO reqDTO) {
        return null;
    }

    @Override
    public ResponseResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO oAuth2AccessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        return ResponseResult.okResult(OAuth2TokenConvert.INSTANCE.convert(oAuth2AccessTokenDO));
    }

    @Override
    public ResponseResult<OAuth2AccessTokenRespDTO> removeAccessToken(String accessToken) {
        return null;
    }

    @Override
    public ResponseResult<OAuth2AccessTokenRespDTO> refreshAccessToken(String refreshToken, String clientId) {
        return null;
    }
}
