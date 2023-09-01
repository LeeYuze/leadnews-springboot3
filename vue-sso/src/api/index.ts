import http from './httpInstance.js'

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