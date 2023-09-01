package com.leadnews.user.controller.oauth2;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.leadnews.common.execption.CustomException;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.user.common.enums.oauth2.OAuth2GrantTypeEnum;
import com.leadnews.user.common.enums.user.UserTypeEnum;
import com.leadnews.user.controller.oauth2.vo.open.OAuth2OpenAuthorizeInfoRespVO;
import com.leadnews.user.convert.OAuth2OpenConvert;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.service.oauth2.OAuth2ApproveService;
import com.leadnews.user.service.oauth2.OAuth2ClientService;
import com.leadnews.user.service.oauth2.OAuth2GrantService;
import com.leadnews.user.utils.json.JsonUtils;
import com.leadnews.user.utils.oauth2.OAuth2Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.leadnews.security.core.utils.SecurityFrameworkUtils.getLoginUserId;
import static com.leadnews.user.utils.collection.CollectionUtils.convertList;

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

    private final OAuth2GrantService oAuth2GrantService;


    @PostMapping("/authorize")
    @Operation(summary = "申请授权", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【提交】调用")
    @Parameters({
            @Parameter(name = "response_type", required = true, description = "响应类型", example = "code"),
            @Parameter(name = "client_id", required = true, description = "客户端编号", example = "tudou"),
            @Parameter(name = "scope", description = "授权范围", example = "userinfo.read"), // 使用 Map<String, Boolean> 格式，Spring MVC 暂时不支持这么接收参数
            @Parameter(name = "redirect_uri", required = true, description = "重定向 URI", example = "https://www.iocoder.cn"),
            @Parameter(name = "auto_approve", required = true, description = "用户是否接受", example = "true"),
            @Parameter(name = "state", example = "1")
    })
    public ResponseResult<String> approveOrDeny(@RequestParam("response_type") String responseType,
                                                @RequestParam("client_id") String clientId,
                                                @RequestParam(value = "scope", required = false) String scope,
                                                @RequestParam("redirect_uri") String redirectUri,
                                                @RequestParam(value = "auto_approve") Boolean autoApprove,
                                                @RequestParam(value = "state", required = false) String state) {

        Map<String, Boolean> scopes = JsonUtils.parseObject(scope, Map.class);
        scopes = ObjectUtil.defaultIfNull(scopes, Collections.emptyMap());
        // 0. 校验用户已经登录。通过 Spring Security 实现

        // 1.1 校验 responseType 是否满足 code 或者 token 值
        OAuth2GrantTypeEnum grantTypeEnum = getGrantTypeEnum(responseType);

        // 1.2 校验 redirectUri 重定向域名是否合法 + 校验 scope 是否在 Client 授权范围内
        OAuth2ClientDO client = oAuth2ClientService.validOAuthClientFromCache(clientId, null,
                grantTypeEnum.getGrantType(), scopes.keySet(), redirectUri);

        // 2.1 假设 approved 为 null，说明是场景一
        if (Boolean.TRUE.equals(autoApprove)) {
            // 如果无法自动授权通过，则返回空 url，前端不进行跳转
            if (!oAuth2ApproveService.checkForPreApproval(getLoginUserId(), getUserType(), clientId, scopes.keySet())) {
                return ResponseResult.okResult(null);
            }
        } else { // 2.2 假设 approved 非 null，说明是场景二
            // 如果计算后不通过，则跳转一个错误链接
            if (!oAuth2ApproveService.updateAfterApproval(getLoginUserId(), getUserType(), clientId, scopes)) {
                return ResponseResult.okResult(OAuth2Utils.buildUnsuccessfulRedirect(redirectUri, responseType, state,
                        "access_denied", "User denied access"));
            }
        }

        // 3.1 如果是 code 授权码模式，则发放 code 授权码，并重定向
        List<String> approveScopes = convertList(scopes.entrySet(), Map.Entry::getKey, Map.Entry::getValue);
        if (grantTypeEnum == OAuth2GrantTypeEnum.AUTHORIZATION_CODE) {
            return ResponseResult.okResult(getAuthorizationCodeRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
        }

        return ResponseResult.okResult(getImplicitGrantRedirect(getLoginUserId(), client, approveScopes, redirectUri, state));
    }


    private String getImplicitGrantRedirect(Long userId, OAuth2ClientDO client,
                                            List<String> scopes, String redirectUri, String state) {
        // 1. 创建 access token 访问令牌
        OAuth2AccessTokenDO accessTokenDO = oAuth2GrantService.grantImplicit(userId, getUserType(), client.getClientId(), scopes);
        Assert.notNull(accessTokenDO, "访问令牌不能为空"); // 防御性检查
        // 2. 拼接重定向的 URL
        // noinspection unchecked
        return OAuth2Utils.buildImplicitRedirectUri(redirectUri, accessTokenDO.getAccessToken(), state, accessTokenDO.getExpiresTime(),
                scopes, JsonUtils.parseObject(client.getAdditionalInformation(), Map.class));
    }


    private String getAuthorizationCodeRedirect(Long userId, OAuth2ClientDO client,
                                                List<String> scopes, String redirectUri, String state) {
        // 1. 创建 code 授权码
        String authorizationCode = oAuth2GrantService.grantAuthorizationCodeForCode(userId, getUserType(), client.getClientId(), scopes,
                redirectUri, state);
        // 2. 拼接重定向的 URL
        return OAuth2Utils.buildAuthorizationCodeRedirectUri(redirectUri, authorizationCode, state);
    }

    private static OAuth2GrantTypeEnum getGrantTypeEnum(String responseType) {
        if (StrUtil.equals(responseType, "code")) {
            return OAuth2GrantTypeEnum.AUTHORIZATION_CODE;
        }
        if (StrUtil.equalsAny(responseType, "token")) {
            return OAuth2GrantTypeEnum.IMPLICIT;
        }
        throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "response_type 参数值只允许 code 和 token");
    }


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
