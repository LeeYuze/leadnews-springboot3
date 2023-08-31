package com.leadnews.user.mapper.oauth2;


import com.leadnews.user.dal.dataobject.oauth2.OAuth2CodeDO;
import com.leadnews.user.utils.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2CodeMapper extends BaseMapperX<OAuth2CodeDO> {

    default OAuth2CodeDO selectByCode(String code) {
        return selectOne(OAuth2CodeDO::getCode, code);
    }

}
