package com.leadnews.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.user.dtos.LoginDTO;
import com.leadnews.model.user.pojos.ApUser;

/**
 * @author lihaohui
 * @date 2023/8/14
 */
public interface ApUserService extends IService<ApUser> {
    /**
     * app端登录
     * @param dto
     * @return
     */
     ResponseResult login(LoginDTO dto);
}
