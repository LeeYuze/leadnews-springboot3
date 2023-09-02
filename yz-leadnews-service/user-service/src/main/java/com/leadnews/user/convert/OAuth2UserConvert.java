package com.leadnews.user.convert;


import com.leadnews.user.controller.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.leadnews.user.controller.oauth2.vo.user.OAuth2UserUpdateReqVO;
import com.leadnews.user.controller.oauth2.vo.user.UserProfileUpdateReqVO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OAuth2UserConvert {

    OAuth2UserConvert INSTANCE = Mappers.getMapper(OAuth2UserConvert.class);

    OAuth2UserInfoRespVO convert(AdminUserDO bean);

    UserProfileUpdateReqVO convert(OAuth2UserUpdateReqVO bean);

}
