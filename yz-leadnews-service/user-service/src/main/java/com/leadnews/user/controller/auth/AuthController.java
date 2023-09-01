package com.leadnews.user.controller.auth;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.user.controller.auth.vo.AuthLoginReqVO;
import com.leadnews.user.controller.auth.vo.AuthLoginRespVO;
import com.leadnews.user.service.auth.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return ResponseResult.okResult(authService.login(reqVO));
    }
}
