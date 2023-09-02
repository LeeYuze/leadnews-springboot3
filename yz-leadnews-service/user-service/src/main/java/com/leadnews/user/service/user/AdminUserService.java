package com.leadnews.user.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.user.controller.oauth2.vo.user.UserProfileUpdateReqVO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import jakarta.validation.Valid;

/**
* @author lihaohui
* @description 针对表【system_users(用户信息表)】的数据库操作Service
* @createDate 2023-08-31 20:00:25
*/
public interface AdminUserService extends IService<AdminUserDO> {

    /**
     * 修改用户个人信息
     *
     * @param id 用户编号
     * @param reqVO 用户个人信息
     */
    void updateUserProfile(Long id, @Valid UserProfileUpdateReqVO reqVO);

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    AdminUserDO getUserByUsername(String username);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);


    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    AdminUserDO getUser(Long id);
}
