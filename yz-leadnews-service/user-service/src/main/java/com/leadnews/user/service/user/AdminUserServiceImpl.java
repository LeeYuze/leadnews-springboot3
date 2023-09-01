package com.leadnews.user.service.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.mapper.user.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
* @author lihaohui
* @description 针对表【system_users(用户信息表)】的数据库操作Service实现
* @createDate 2023-08-31 20:00:25
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserDO>
    implements AdminUserService {

    private final AdminUserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public AdminUserDO getUser(Long id) {
        return userMapper.selectById(id);
    }
}




