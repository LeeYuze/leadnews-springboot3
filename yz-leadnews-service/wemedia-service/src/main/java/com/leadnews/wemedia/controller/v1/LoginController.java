package com.leadnews.wemedia.controller.v1;

import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.model.wemedia.dtos.WmUserDTO;
import com.leadnews.wemedia.service.WmUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihaohui
 * @date 2023/8/15
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final WmUserService wmUserService;

    @Operation(summary = "用户登录", description = "自媒体端 - 用户登录")
    @PostMapping("/in")
    public ResponseResult login(@RequestBody WmUserDTO dto){
        return wmUserService.login(dto);
    }
}
