package com.leadnews.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDTO  {

    /**
     * 设备id
     */
    private Integer equipmentId;


    private String username;

    /**
     * 手机号
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}