package com.leadnews.user.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.user.dal.dataobject.user.SystemUsers;
import com.leadnews.user.mapper.user.SystemUsersMapper;
import com.leadnews.user.service.user.SystemUsersService;
import org.springframework.stereotype.Service;

/**
* @author lihaohui
* @description 针对表【system_users(用户信息表)】的数据库操作Service实现
* @createDate 2023-08-31 20:00:25
*/
@Service
public class SystemUsersServiceImpl extends ServiceImpl<SystemUsersMapper, SystemUsers>
    implements SystemUsersService {

}




