package com.leadnews.user.service.auth;

import com.leadnews.user.controller.auth.vo.AuthLoginReqVO;
import com.leadnews.user.controller.auth.vo.AuthLoginRespVO;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import jakarta.validation.Valid;

/**
 * 管理后台认证 Service 接口
 * @author lihaohui
 * @date 2023/8/31
 */
public interface AdminAuthService {


    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO);

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    AdminUserDO authenticate(String username, String password);

}
