package com.leadnews.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.dtos.WmUserDTO;
import com.leadnews.model.wemedia.pojos.WmUser;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
public interface WmUserService extends IService<WmUser> {

    /**
     * 用户登录
     * @param dto 登录参数
     * @return
     */
    ResponseResult login(WmUserDTO dto);
}
