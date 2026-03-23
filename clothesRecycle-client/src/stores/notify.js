import { defineStore } from 'pinia'
import { getUnreadCount } from '@/api/message'

export const useNotifyStore = defineStore('notify', {
  state: () => ({
    unreadCount: 0,
  }),
  actions: {
    setUnreadCount(count) {
      this.unreadCount = count
    },
    async loadUnreadCount() {
      try {
        const data = await getUnreadCount()
        this.unreadCount = Number(data.unread || 0)
      } catch {
        this.unreadCount = 0
      }
    },
  },
})
