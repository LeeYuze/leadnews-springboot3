package com.leadnews.user.convert;

import com.leadnews.user.controller.auth.vo.AuthLoginRespVO;
import com.leadnews.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
@Mapper
public interface AuthConvert {
    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AuthLoginRespVO convert(OAuth2AccessTokenDO bean);
}
