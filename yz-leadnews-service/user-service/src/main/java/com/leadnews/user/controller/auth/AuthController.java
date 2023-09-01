package com.leadnews.user.controller.auth;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.security.core.utils.SecurityFrameworkUtils;
import com.leadnews.user.controller.auth.vo.AuthLoginReqVO;
import com.leadnews.user.controller.auth.vo.AuthLoginRespVO;
import com.leadnews.user.controller.auth.vo.AuthPermissionInfoRespVO;
import com.leadnews.user.convert.AuthConvert;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.service.auth.AdminAuthService;
import com.leadnews.user.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author lihaohui
 * @date 2023/8/31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/auth")
public class AuthController {

    private final AdminAuthService authService;

    private final AdminUserService userService;

    @PermitAll
    @PostMapping("/login")
    public ResponseResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return ResponseResult.okResult(authService.login(reqVO));
    }

    @GetMapping("/get-permission-info")
    public ResponseResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(SecurityFrameworkUtils.getLoginUserId());
        if (user == null) {
            return null;
        }

        return ResponseResult.okResult(AuthConvert.INSTANCE.convert(user));
    }
}
