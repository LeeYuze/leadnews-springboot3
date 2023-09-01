package com.leadnews.user.service.oauth2;

import cn.hutool.core.util.IdUtil;
import com.leadnews.common.execption.CustomException;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.leadnews.user.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.leadnews.user.mapper.oauth2.OAuth2AccessTokenMapper;
import com.leadnews.user.mapper.oauth2.OAuth2RefreshTokenMapper;
import com.leadnews.user.utils.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Oauth2TokenServiceImpl implements Oauth2TokenService {

    private final Oauth2ClientService oauth2ClientService;

    private final OAuth2RefreshTokenMapper oAuth2RefreshTokenMapper;

    private final OAuth2AccessTokenMapper oAuth2AccessTokenMapper;

    private final OAuth2AccessTokenRedisDAO oAuth2AccessTokenRedisDAO;

    @Override
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(Long userId, Integer userType, OAuth2ClientDO clientDO, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO()
                .setRefreshToken(generateRefreshToken())
                .setUserId(userId)
                .setUserType(userType)
                .setClientId(clientDO.getClientId())
                .setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));
        oAuth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }


    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(generateAccessToken())
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));

        oAuth2AccessTokenMapper.insert(accessTokenDO);
        // 记录到 Redis 中
        oAuth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }


    @Override
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        return null;
    }

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        OAuth2AccessTokenDO accessTokenDO = oAuth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }
        // 获取不到，从 MySQL 中获取
        accessTokenDO = oAuth2AccessTokenMapper.selectByAccessToken(accessToken);
        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oAuth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw new CustomException(AppHttpCodeEnum.TOKEN_INVALID);
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw new CustomException(AppHttpCodeEnum.TOKEN_EXPIRE);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        return null;
    }

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }
}
