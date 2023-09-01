package com.leadnews.user.controller.oauth2;

import cn.hutool.core.collection.CollUtil;
import com.leadnews.model.common.dtos.ResponseResult;
import com.leadnews.user.controller.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.leadnews.user.convert.OAuth2UserConvert;
import com.leadnews.user.dal.dataobject.user.AdminUserDO;
import com.leadnews.user.service.auth.AdminAuthService;
import com.leadnews.user.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.leadnews.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

/**
 * 提供给外部应用调用为主
 *
 * 1. 在 getUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.read')") 注解，声明需要满足 scope = user.read
 * 2. 在 updateUserInfo 方法上，添加 @PreAuthorize("@ss.hasScope('user.write')") 注解，声明需要满足 scope = user.write
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - OAuth2.0 用户")
@RestController
@RequestMapping("/system/oauth2/user")
@Validated
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserController {


    private final AdminUserService userService;


    @GetMapping("/get")
    @Operation(summary = "获得用户基本信息")
    public ResponseResult<OAuth2UserInfoRespVO> getUserInfo() {
        // 获得用户基本信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        OAuth2UserInfoRespVO resp = OAuth2UserConvert.INSTANCE.convert(user);

        return ResponseResult.okResult(resp);
    }

//    @PutMapping("/update")
//    @Operation(summary = "更新用户基本信息")
//    @PreAuthorize("@ss.hasScope('user.write')")
//    public CommonResult<Boolean> updateUserInfo(@Valid @RequestBody OAuth2UserUpdateReqVO reqVO) {
//        // 这里将 UserProfileUpdateReqVO =》UserProfileUpdateReqVO 对象，实现接口的复用。
//        // 主要是，AdminUserService 没有自己的 BO 对象，所以复用只能这么做
//        userService.updateUserProfile(getLoginUserId(), OAuth2UserConvert.INSTANCE.convert(reqVO));
//        return success(true);
//    }

}
