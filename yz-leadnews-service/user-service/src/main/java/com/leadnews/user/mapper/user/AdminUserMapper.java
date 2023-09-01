package com.leadnews.user.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.utils.mapper.BaseMapperX;

/**
* @author lihaohui
* @description 针对表【system_users(用户信息表)】的数据库操作Mapper
* @createDate 2023-08-31 20:00:25
* @Entity generator.domain.SystemUsers
*/
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }
}




