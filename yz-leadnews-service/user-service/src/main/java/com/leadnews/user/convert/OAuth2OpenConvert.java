package com.leadnews.user.convert;

import com.leadnews.security.core.utils.SecurityFrameworkUtils;
import com.leadnews.user.controller.oauth2.vo.KeyValue;
import com.leadnews.user.controller.oauth2.vo.open.OAuth2OpenAccessTokenRespVO;
import com.leadnews.user.controller.oauth2.vo.open.OAuth2OpenAuthorizeInfoRespVO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.utils.collection.CollectionUtils;
import com.leadnews.user.utils.oauth2.OAuth2Utils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Mapper
public interface OAuth2OpenConvert {
    OAuth2OpenConvert INSTANCE = Mappers.getMapper(OAuth2OpenConvert.class);

    default OAuth2OpenAccessTokenRespVO convert(OAuth2AccessTokenDO bean) {
        OAuth2OpenAccessTokenRespVO respVO = convert0(bean);
        respVO.setTokenType(SecurityFrameworkUtils.AUTHORIZATION_BEARER.toLowerCase());
        respVO.setExpiresIn(OAuth2Utils.getExpiresIn(bean.getExpiresTime()));
        respVO.setScope(OAuth2Utils.buildScopeStr(bean.getScopes()));
        return respVO;
    }

    OAuth2OpenAccessTokenRespVO convert0(OAuth2AccessTokenDO bean);


    default OAuth2OpenAuthorizeInfoRespVO convert(OAuth2ClientDO client, List<OAuth2ApproveDO> approves) {
        // 构建 scopes
        List<KeyValue<String, Boolean>> scopes = new ArrayList<>(client.getScopes().size());
        Map<String, OAuth2ApproveDO> approveMap = CollectionUtils.convertMap(approves, OAuth2ApproveDO::getScope);
        client.getScopes().forEach(scope -> {
            OAuth2ApproveDO approve = approveMap.get(scope);
            scopes.add(new KeyValue<>(scope, approve != null ? approve.getApproved() : false));
        });
        // 拼接返回
        return new OAuth2OpenAuthorizeInfoRespVO(
                new OAuth2OpenAuthorizeInfoRespVO.Client(client.getName(), client.getLogo()), scopes);
    }

}
