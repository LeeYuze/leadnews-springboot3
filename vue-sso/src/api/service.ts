// 这个时axios的配置
import axios from 'axios';
import * as TokenConstants from "@/constants/tokenConstants.ts"
import * as API from '@/api'
import {refreshToken} from "@/api";


const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: 'http://127.0.0.1:51601', // 此处的 /admin-api/ 地址，原因是后端的基础路径为 /admin-api/
    // 超时
    timeout: 30000,
    // 禁用 Cookie 等信息
    withCredentials: false,
})

service.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    // config.headers.Authorization
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});


// 添加响应拦截器
service.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    return response.status === 200 ? Promise.resolve(response.data) : Promise.reject(response);
}, function (error) {
    // 对响应错误做点什么
    const {response} = error;
});

export default service;