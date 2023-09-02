package com.leadnews.security.core.service;

import cn.hutool.core.collection.CollUtil;
import com.leadnews.security.core.LoginUser;
import com.leadnews.security.core.utils.SecurityFrameworkUtils;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * @author lihaohui
 * @date 2023/9/2
 */
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {
    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        return false;
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return false;
    }

    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return false;
        }
        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }
}
