import axios from 'axios'

/**
 * 管理端登录态失效后统一清理并跳转登录页。
 */
const redirectToLogin = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_profile')

  const currentPath = `${window.location.pathname}${window.location.search}`
  const loginUrl = `/login?redirect=${encodeURIComponent(currentPath)}`
  if (!window.location.pathname.startsWith('/login')) {
    window.location.assign(loginUrl)
  }
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code && payload.code !== 200) {
      // 业务态 401：Token 失效或未登录，直接回跳登录页。
      if (payload.code === 401) {
        redirectToLogin()
      }
      return Promise.reject(new Error(payload.msg || '请求失败'))
    }
    return payload?.data ?? payload
  },
  (error) => {
    // 网络层 401：与业务态保持一致处理。
    if (error?.response?.status === 401) {
      redirectToLogin()
    }
    return Promise.reject(error)
  },
)

export default request
