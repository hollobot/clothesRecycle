import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  server: {
    // 关键：监听本机所有网卡的IP（包括192.168.81.134），替代默认的127.0.0.1
    host: '0.0.0.0',
    // 端口
    port: 5174,
    // 可选：启动时自动打开浏览器（建议开启，方便测试）
    open: false,
    // 可选：端口被占用时自动切换可用端口（避免报错）
    strictPort: false,
    // 可选：允许跨域（开发环境常用，非必须）
    cors: true,
  },
})
