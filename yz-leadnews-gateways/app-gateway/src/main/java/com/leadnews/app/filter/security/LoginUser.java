package com.leadnews.app.filter.security;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 登录用户信息
 *
 * copy from yudao-spring-boot-starter-security 的 LoginUser 类
 *
 * @author 芋道源码
 */
@Data
@Accessors(chain = true)
public class LoginUser {

    /**
     * 用户编号
     */
    private Long id;
    
    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 授权范围
     */
    private List<String> scopes;

}
