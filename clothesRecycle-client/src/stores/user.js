import { defineStore } from 'pinia'

/**
 * 读取本地用户信息并兜底 JSON 解析异常，避免脏数据导致应用白屏。
 */
const getProfileFromStorage = () => {
  try {
    const raw = localStorage.getItem('client_profile')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('client_token') || '',
    profile: getProfileFromStorage(),
  }),
  getters: {
    isLogin: (state) => !!state.token,
  },
  actions: {
    setLogin(data) {
      this.token = data.token
      this.profile = data
      localStorage.setItem('client_token', data.token)
      localStorage.setItem('client_profile', JSON.stringify(data))
    },
    /**
     * 合并更新本地用户资料，避免刷新后页面显示旧数据。
     */
    updateProfile(patch) {
      this.profile = {
        ...(this.profile || {}),
        ...(patch || {}),
      }
      localStorage.setItem('client_profile', JSON.stringify(this.profile))
    },
    logout() {
      this.token = ''
      this.profile = null
      localStorage.removeItem('client_token')
      localStorage.removeItem('client_profile')
    },
  },
})
