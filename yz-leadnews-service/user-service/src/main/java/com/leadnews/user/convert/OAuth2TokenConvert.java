package com.leadnews.user.convert;

import com.leadnews.apis.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Mapper
public interface OAuth2TokenConvert {
    OAuth2TokenConvert INSTANCE = Mappers.getMapper(OAuth2TokenConvert.class);

    OAuth2AccessTokenCheckRespDTO convert(OAuth2AccessTokenDO bean);

}
