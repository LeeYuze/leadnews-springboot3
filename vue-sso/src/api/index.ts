import http from './httpInstance.js'
import service from './service.ts'
import * as TokenConstants from '@/constants/tokenConstants.ts'
import axios from "axios";

export function login(data) {
    return http({
        method: 'post',
        url: '/user/system/auth/login',
        data,
    })
}

export function getUserInfo() {
    return http({
        method: 'get',
        url: '/user/system/auth/get-permission-info'
    })
}

// ========== OAUTH 2.0 相关 ==========

export function getAuthorize(clientId) {
    return http({
        url: '/user/system/oauth2/authorize?clientId=' + clientId,
        method: 'get'
    })
}


// 刷新访问令牌
export function refreshToken() {
    var refreshToken = localStorage.getItem(TokenConstants.REFRESHTOKEN_KEY);

    return service({
        url: '/user/system/auth/refresh-token?refreshToken=' + refreshToken,
        method: 'post'
    })
}

export function authorize(responseType, clientId, redirectUri, state,
                          autoApprove, checkedScopes, uncheckedScopes) {
    // 构建 scopes
    const scopes = {}
    for (const scope of checkedScopes) {
        scopes[scope] = true
    }
    for (const scope of uncheckedScopes) {
        scopes[scope] = false
    }
    console.log(scopes)
    // 发起请求
    return http({
        url: '/user/system/oauth2/authorize',
        headers: {
            'Content-type': 'application/x-www-form-urlencoded'
        },
        params: {
            response_type: responseType,
            client_id: clientId,
            redirect_uri: redirectUri,
            state: state,
            auto_approve: autoApprove,
            scope: JSON.stringify(scopes)
        },
        method: 'post'
    })
}