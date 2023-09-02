package com.leadnews.user.convert;

import com.leadnews.user.controller.oauth2.vo.user.UserProfileUpdateReqVO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    AdminUserDO convert(UserProfileUpdateReqVO bean);


}
