import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

import { resolve } from "path"

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 配置别名
    alias: {
      "@": resolve(__dirname, "src")
    },
    extensions: [".js", ".json", ".ts", ".vue"] // 使用路径别名时想要省略的后缀名
  },
  server: {
    proxy: {
      '/devApi': {
        target: 'https://10.194.98.123', // 所要代理的目标地址
        secure: false,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/devApi/, '') // 重写传过来的path路径
      }
    }
  }
})
