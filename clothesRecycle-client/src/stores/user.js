import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('client_token') || '',
    profile: JSON.parse(localStorage.getItem('client_profile') || 'null'),
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
    logout() {
      this.token = ''
      this.profile = null
      localStorage.removeItem('client_token')
      localStorage.removeItem('client_profile')
    },
  },
})
