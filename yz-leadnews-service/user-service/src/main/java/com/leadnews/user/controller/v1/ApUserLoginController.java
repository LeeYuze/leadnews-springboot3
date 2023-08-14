package com.leadnews.user.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.user.dtos.LoginDTO;
import com.leadnews.user.service.ApUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/14
 */
@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Tag(name = "用户模块")
public class ApUserLoginController {

    private final ApUserService apUserService;

    @Operation(description = "移动端头条-用户登录", summary = "用户登录")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDTO dto) {
        return apUserService.login(dto);
    }
}
