package com.leadnews.user.service.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import com.leadnews.common.execption.CustomException;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.user.common.constants.oauth2.RedisKeyConstants;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.mapper.oauth2.OAuth2ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2ClientServiceImpl implements Oauth2ClientService{

    private final OAuth2ClientMapper oAuth2ClientMapper;

    @Override
    public OAuth2ClientDO getOAuth2Client(Long id) {
        return oAuth2ClientMapper.selectById(id);
    }

    @Cacheable(cacheNames = RedisKeyConstants.OAUTH_CLIENT, key = "#clientId", unless = "#result == null")
    @Override
    public OAuth2ClientDO getOAuth2ClientFromCache(String clientId) {
        return oAuth2ClientMapper.selectByClientId(clientId);
    }

    @Override
    public OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType, Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        OAuth2ClientDO client = getSelf().getOAuth2ClientFromCache(clientId);

        if (client == null) {
            throw new CustomException(AppHttpCodeEnum.OAUTH2_CLIENT_NOT_EXISTS);
        }

        // TODO yz 状态校验
        // TODO yz 密钥校验
        // TODO yz 授权方式校验
        // TODO yz 授权范围校验
        // TODO yz 回调地址校验

        return client;
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private Oauth2ClientServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
