package com.leadnews.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.common.execption.CustException;
import com.leadnews.model.wemedia.vos.WmUserVO;
import com.leadnews.utils.common.AppJwtUtil;
import com.leadnews.wemedia.mapper.WmUserMapper;
import com.leadnews.wemedia.service.WmUserService;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.wemedia.dtos.WmUserDTO;
import com.leadnews.model.wemedia.pojos.WmUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
    @Override
    public ResponseResult login(WmUserDTO dto) {
        String name = dto.getName();
        String password = dto.getPassword();

        if (StrUtil.isBlank(name) || StrUtil.isBlank(password)) {
            CustException.cust(AppHttpCodeEnum.PARAM_INVALID, "用户名密码不能为空");
        }


        LambdaQueryWrapper<WmUser> wrapper = Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, name);
        WmUser wmUser = getOne(wrapper);
        if (wmUser == null) {
            CustException.cust(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

        String inputPWD = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());
        if (!inputPWD.equals(wmUser.getPassword())) {
            CustException.cust(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户名密码错误");
        }

        Integer status = wmUser.getStatus();
        if (status.intValue() != 9) {
            CustException.cust(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }

        wmUser.setLoginTime(new Date());
        updateById(wmUser);

        String token = AppJwtUtil.getToken(wmUser.getId());

        WmUserVO wmUserVO = new WmUserVO();
        BeanUtils.copyProperties(wmUser, wmUserVO);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", wmUserVO);
        return ResponseResult.okResult(map);
    }
}
