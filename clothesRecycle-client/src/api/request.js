import axios from 'axios'

/**
 * 开发环境后端默认地址。
 */
const DEV_DEFAULT_API_BASE_URL = 'http://localhost:8080'

const envBaseUrl = import.meta.env.VITE_API_BASE_URL || DEV_DEFAULT_API_BASE_URL

/**
 * 企业级环境策略：
 * - 开发环境允许回退 localhost
 * - 生产环境必须显式配置 VITE_API_BASE_URL
 */
const resolveApiBaseUrl = () => {
  if (import.meta.env.PROD) {
    if (!envBaseUrl) {
      throw new Error(
        '生产环境缺少 VITE_API_BASE_URL，请在 clothesRecycle-client/.env.production 配置真实后端地址',
      )
    }
    return envBaseUrl
  }
  return envBaseUrl || DEV_DEFAULT_API_BASE_URL
}

const request = axios.create({
  baseURL: resolveApiBaseUrl(),
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('client_token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code && payload.code !== 200) {
      // 后端返回业务未登录时，统一清理本地登录态并回跳登录页。
      if (payload.code === 401) {
        localStorage.removeItem('client_token')
        localStorage.removeItem('client_profile')

        const currentPath = `${window.location.pathname}${window.location.search}`
        const loginUrl = `/login?redirect=${encodeURIComponent(currentPath)}`
        if (!window.location.pathname.startsWith('/login')) {
          window.location.assign(loginUrl)
        }
      }
      return Promise.reject(new Error(payload.msg || '请求失败'))
    }
    return payload?.data ?? payload
  },
  (error) => {
    // 网络层 401（例如 Sa-Token 鉴权失败）也走同一套跳转逻辑。
    if (error?.response?.status === 401) {
      localStorage.removeItem('client_token')
      localStorage.removeItem('client_profile')

      const currentPath = `${window.location.pathname}${window.location.search}`
      const loginUrl = `/login?redirect=${encodeURIComponent(currentPath)}`
      if (!window.location.pathname.startsWith('/login')) {
        window.location.assign(loginUrl)
      }
    }
    return Promise.reject(error)
  },
)

export default request
