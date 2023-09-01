package com.leadnews.user.controller.oauth2;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.user.common.enums.user.UserTypeEnum;
import com.leadnews.user.controller.oauth2.vo.open.OAuth2OpenAuthorizeInfoRespVO;
import com.leadnews.user.convert.OAuth2OpenConvert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.service.oauth2.OAuth2ApproveService;
import com.leadnews.user.service.oauth2.OAuth2ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.leadnews.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@RestController
@RequestMapping("/system/oauth2")
@Slf4j
@RequiredArgsConstructor
public class OAuth2OpenController {

    private final OAuth2ClientService oAuth2ClientService;

    private final OAuth2ApproveService oAuth2ApproveService;


    @GetMapping("/authorize")
    @Operation(summary = "获得授权信息", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【获取】调用")
    @Parameter(name = "clientId", required = true, description = "客户端编号", example = "tudou")
    public ResponseResult<OAuth2OpenAuthorizeInfoRespVO> authorize(@RequestParam("clientId") String clientId) {

        // 1. 获得 Client 客户端的信息
        OAuth2ClientDO client = oAuth2ClientService.validOAuthClientFromCache(clientId);
        // 2. 获得用户已经授权的信息
        List<OAuth2ApproveDO> approves = oAuth2ApproveService.getApproveList(getLoginUserId(), getUserType(), clientId);

        return ResponseResult.okResult(OAuth2OpenConvert.INSTANCE.convert(client, approves));
    }

    private Integer getUserType() {
        return UserTypeEnum.ADMIN.getValue();
    }
}
