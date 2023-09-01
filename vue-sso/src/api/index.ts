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