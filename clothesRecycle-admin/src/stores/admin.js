import { defineStore } from 'pinia'

const getProfileFromStorage = () => {
  try {
    const raw = localStorage.getItem('admin_profile')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export const useAdminStore = defineStore('admin', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    profile: getProfileFromStorage(),
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isSuperAdmin: (state) => state.profile?.role === 'SUPER_ADMIN',
    displayName: (state) => state.profile?.name || '管理员',
  },
  actions: {
    setLogin(data) {
      this.token = data.token
      this.profile = data
      localStorage.setItem('admin_token', data.token)
      localStorage.setItem('admin_profile', JSON.stringify(data))
    },
    logout() {
      this.token = ''
      this.profile = null
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_profile')
    },
  },
})
