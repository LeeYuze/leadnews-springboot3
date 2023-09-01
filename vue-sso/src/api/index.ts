import http from './httpInstance.js'

export function login(data) {
    return http({
        method: 'post',
        url: '/user/system/auth/login',
        data,
    })
}