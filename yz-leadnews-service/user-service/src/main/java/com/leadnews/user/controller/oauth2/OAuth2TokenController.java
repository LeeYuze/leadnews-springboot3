package com.leadnews.user.controller.oauth2;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.user.controller.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.leadnews.user.convert.OAuth2TokenConvert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.service.oauth2.Oauth2TokenService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@RestController
@RequestMapping("/system/oauth2/token")
@Slf4j
@RequiredArgsConstructor
public class OAuth2TokenController {

    private final Oauth2TokenService oauth2TokenService;

    @PermitAll
    @GetMapping("/check")
    public ResponseResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO oAuth2AccessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        return ResponseResult.okResult(OAuth2TokenConvert.INSTANCE.convert(oAuth2AccessTokenDO));
    }
}
