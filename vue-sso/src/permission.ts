import router from '@/router/router'

import * as TokenConstants from '@/constants/tokenConstants.ts'

import {getUserInfo} from '@/api'

const whiteList = ['/login', '/social-login', '/auth-redirect', '/bind', '/register', '/oauthLogin/gitee']

router.beforeEach(async (to, from, next) => {
    var accessToken = localStorage.getItem(TokenConstants.ACCESSTOKEN_KEY);
    if (accessToken) {
        if (to.path === '/login') {
            next({path: '/'})
        } else {
            var user = localStorage.getItem("user");
            if (!user) {
                // 获取用户信息
                const res = await getUserInfo();
                if (res.code === 0) {
                    localStorage.setItem("user", JSON.stringify(res.data))
                    next({...to, replace: true}) // hack方法 确保addRoutes已完成
                }
            } else {
                next()
            }

        }
    } else {
        // 没有token
        if (whiteList.indexOf(to.path) !== -1) {
            // 在免登录白名单，直接进入
            next()
        } else {
            const redirect = encodeURIComponent(to.fullPath) // 编码 URI，保证参数跳转回去后，可以继续带上
            next(`/login?redirect=${redirect}`) // 否则全部重定向到登录页
        }
    }
})