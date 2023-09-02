// createWebHistory:history路由
//  createWebHashHistory：hash路由
import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/home',
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/pages/Home.vue'),  // 配置路径别名后，可以使用@
    },
    {
        path: '/sso',
        name: 'Sso',
        component: () => import('@/pages/Sso.vue'),  // 配置路径别名后，可以使用@
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/pages/Login.vue'),  // 配置路径别名后，可以使用@
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router
