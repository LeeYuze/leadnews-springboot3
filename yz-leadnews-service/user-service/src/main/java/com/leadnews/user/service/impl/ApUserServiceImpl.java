package com.leadnews.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.common.enums.AppHttpCodeEnum;
import com.leadnews.model.user.dtos.LoginDTO;
import com.leadnews.model.user.pojos.ApUser;
import com.leadnews.user.mapper.ApUserMapper;
import com.leadnews.user.service.ApUserService;
import com.leadnews.utils.common.AppJwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/8/14
 */
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Override
    public ResponseResult login(LoginDTO dto) {

        //  如果都不填写就是游客
        if (StrUtil.isBlank(dto.getPhone()) && StrUtil.isBlank(dto.getPassword())) {
            //游客  同样返回token  id = 0
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0l));
            return ResponseResult.okResult(map);
        }

        ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
        if (apUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
        }

        //1.2 比对密码
        String salt = apUser.getSalt();
        String pswd = dto.getPassword();

        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());

        if (!pswd.equals(apUser.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //1.3 返回数据  jwt
        apUser.setSalt("");
        apUser.setPassword("");

        Map<String, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(apUser.getId()));
        map.put("user", apUser);
        return ResponseResult.okResult(map);
    }
}
