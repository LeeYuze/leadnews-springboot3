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