<script setup>
import { computed, onMounted } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { useNotifyStore } from '@/stores/notify'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const notifyStore = useNotifyStore()
const userStore = useUserStore()

const tabs = [
  { label: '广场', routeName: 'item-list', icon: '▦' },
  { label: '发布', routeName: 'publish', icon: '＋' },
  { label: '消息', routeName: 'messages', icon: '●' },
  { label: '我的', routeName: 'profile', icon: '◉' },
]

const showTabBar = computed(() => !route.meta?.hideTabBar)

const isActive = (tab) => {
  const current = route.name
  if (tab.routeName === 'item-list') {
    return ['item-list', 'item-detail'].includes(String(current))
  }
  return current === tab.routeName
}

const goTab = (tab) => {
  if (String(route.name) === tab.routeName) return
  router.push({ name: tab.routeName })
}

onMounted(() => {
  if (userStore.isLogin) {
    notifyStore.loadUnreadCount()
  }
})
</script>

<template>
  <div class="client-shell">
    <main class="client-shell-content">
      <RouterView />
    </main>

    <nav v-if="showTabBar" class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.routeName"
        class="tab-item"
        :class="{ active: isActive(tab) }"
        @click="goTab(tab)"
      >
        <span class="tab-icon">{{ tab.icon }}</span>
        <span class="tab-text">{{ tab.label }}</span>
        <span
          v-if="tab.routeName === 'messages' && notifyStore.unreadCount > 0"
          class="tab-dot"
        ></span>
      </button>
    </nav>
  </div>
</template>
