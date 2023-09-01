import {createApp} from 'vue'
import App from './App.vue'
// 引入路由
import router from '@/router/router'
// 引入element-plus
import ElementPlus from 'element-plus'

import { createPinia } from 'pinia'


import "./scss/index.scss"
import "./permission.ts"
import 'element-plus/dist/index.css'

const pinia = createPinia()

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.use(pinia)
app.mount('#app')
