package com.leadnews.user.service.oauth2;

import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;

import java.util.List;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
public interface OAuth2GrantService {

    /**
     * 授权码模式，第一阶段，获得 code 授权码
     *
     * 对应 Spring Security OAuth2 的 AuthorizationEndpoint 的 generateCode 方法
     *
     * @param userId 用户编号
     * @param userType 用户类型
     * @param clientId 客户端编号
     * @param scopes 授权范围
     * @param redirectUri 重定向 URI
     * @param state 状态
     * @return 授权码
     */
    String grantAuthorizationCodeForCode(Long userId, Integer userType,
                                         String clientId, List<String> scopes,
                                         String redirectUri, String state);

    /**
     * 授权码模式，第二阶段，获得 accessToken 访问令牌
     *
     * 对应 Spring Security OAuth2 的 AuthorizationCodeTokenGranter 功能
     *
     * @param clientId 客户端编号
     * @param code 授权码
     * @param redirectUri 重定向 URI
     * @param state 状态
     * @return 访问令牌
     */
    OAuth2AccessTokenDO grantAuthorizationCodeForAccessToken(String clientId, String code,
                                                             String redirectUri, String state);


    /**
     * 简化模式
     *
     * 对应 Spring Security OAuth2 的 ImplicitTokenGranter 功能
     *
     * @param userId 用户编号
     * @param userType 用户类型
     * @param clientId 客户端编号
     * @param scopes 授权范围
     * @return 访问令牌
     */
    OAuth2AccessTokenDO grantImplicit(Long userId, Integer userType,
                                      String clientId, List<String> scopes);

    /**
     * 密码模式
     *
     * 对应 Spring Security OAuth2 的 ResourceOwnerPasswordTokenGranter 功能
     *
     * @param username 账号
     * @param password 密码
     * @param clientId 客户端编号
     * @param scopes 授权范围
     * @return 访问令牌
     */
    OAuth2AccessTokenDO grantPassword(String username, String password,
                                      String clientId, List<String> scopes);

    /**
     * 刷新模式
     *
     * 对应 Spring Security OAuth2 的 ResourceOwnerPasswordTokenGranter 功能
     *
     * @param refreshToken 刷新令牌
     * @param clientId 客户端编号
     * @return 访问令牌
     */
    OAuth2AccessTokenDO grantRefreshToken(String refreshToken, String clientId);

    /**
     * 移除访问令牌
     *
     * 对应 Spring Security OAuth2 的 ConsumerTokenServices 的 revokeToken 方法
     *
     * @param accessToken 访问令牌
     * @param clientId 客户端编号
     * @return 是否移除到
     */
    boolean revokeToken(String clientId, String accessToken);
}
