package com.leadnews.user.controller.oauth2.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OAuth2AccessTokenCreateReqDTO implements Serializable {

    @Schema(description = "用户编号", required = true, example = "10")
    private Long userId;

    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", required = true, example = "1")
    private Integer userType;

    @Schema(description = "客户端编号", required = true, example = "yudaoyuanma")
    private String clientId;

    @Schema(description = "授权范围的数组", example = "user_info")
    private List<String> scopes;

}
