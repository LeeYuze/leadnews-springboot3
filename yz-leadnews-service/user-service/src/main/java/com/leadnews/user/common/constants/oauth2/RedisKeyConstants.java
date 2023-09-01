package com.leadnews.user.common.constants.oauth2;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
public interface RedisKeyConstants {
    /**
     * OAuth2 客户端的缓存
     * <p>
     * KEY 格式：user:{id}
     * VALUE 数据类型：String 客户端信息
     */
    String OAUTH_CLIENT = "oauth_client";

    /**
     * 访问令牌的缓存
     * <p>
     * KEY 格式：oauth2_access_token:{token}
     * VALUE 数据类型：String 访问令牌信息 {@link com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO}
     * <p>
     * 由于动态过期时间，使用 RedisTemplate 操作
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";

}
