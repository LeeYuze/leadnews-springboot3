package com.leadnews.user.mapper.oauth2;

import com.leadnews.user.dal.dataobject.oauth2.OAuth2ClientDO;
import com.leadnews.user.utils.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;


/**
 * OAuth2 客户端 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMapperX<OAuth2ClientDO> {


    default OAuth2ClientDO selectByClientId(String clientId) {
        return selectOne(OAuth2ClientDO::getClientId, clientId);
    }

}
