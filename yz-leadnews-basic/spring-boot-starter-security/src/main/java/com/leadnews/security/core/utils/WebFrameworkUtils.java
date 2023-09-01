package com.leadnews.security.core.utils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lihaohui
 * @date 2023/9/1
 */
public class WebFrameworkUtils {
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";
    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type";

    public static void setLoginUserId(ServletRequest request, Long userId) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID, userId);
    }

    /**
     * 设置用户类型
     *
     * @param request 请求
     * @param userType 用户类型
     */
    public static void setLoginUserType(ServletRequest request, Integer userType) {
        request.setAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE, userType);
    }

    /**
     * 获得当前用户的类型
     * 注意：该方法仅限于 web 相关的 framework 组件使用！！！
     *
     * @param request 请求
     * @return 用户编号
     */
    public static Integer getLoginUserType(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        // 1. 优先，从 Attribute 中获取
        Integer userType = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_TYPE);
        if (userType != null) {
            return userType;
        }
        // 2. 其次，基于 URL 前缀的约定
//        if (request.getRequestURI().startsWith(properties.getAdminApi().getPrefix())) {
//            return UserTypeEnum.ADMIN.getValue();
//        }
//        if (request.getRequestURI().startsWith(properties.getAppApi().getPrefix())) {
//            return UserTypeEnum.MEMBER.getValue();
//        }
        return null;
    }
}
