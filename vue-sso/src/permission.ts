import router from '@/router/router'

const whiteList = ['/login', '/social-login',  '/auth-redirect', '/bind', '/register', '/oauthLogin/gitee']

router.beforeEach((to, from, next) => {
    if (to.path === '/login') {
        next()
    } else {
        // 判断当前用户是否已拉取完 user_info 信息
        next({ path: '/login' })
    }
})