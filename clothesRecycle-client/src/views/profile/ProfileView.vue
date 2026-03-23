<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotifyStore } from '@/stores/notify'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const notifyStore = useNotifyStore()

const menuList = [
  { title: '我的订单', path: '/orders', color: 'var(--amber-pale)' },
  { title: '我的收藏', path: '/favorites', color: 'var(--blue-pale)' },
  { title: '积分中心', path: '/points', color: 'var(--green-pale)' },
  { title: '礼品商城', path: '/points/mall', color: 'var(--amber-pale)' },
  { title: '每日签到', path: '/sign', color: 'var(--green-pale)' },
  { title: '排行榜', path: '/rank', color: 'var(--blue-pale)' },
]

const avatarText = computed(() => String(userStore.profile?.name || '用').slice(0, 1))

onMounted(() => {
  if (userStore.isLogin) {
    notifyStore.loadUnreadCount()
  }
})

const onClickLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="page">
    <div class="profile-hero">
      <div class="avatar-lg">{{ avatarText }}</div>
      <div class="profile-info">
        <div class="pname">{{ userStore.profile?.name || '未登录用户' }}</div>
        <div class="psub">{{ userStore.profile?.phone || '请先登录' }}</div>
      </div>
      <button class="ghost-btn ghost-light" @click="$router.push('/messages')">
        消息 <span v-if="notifyStore.unreadCount > 0">({{ notifyStore.unreadCount }})</span>
      </button>
    </div>

    <div class="stat-strip">
      <div class="stat-cell">
        <div class="stat-num">-</div>
        <div class="stat-lbl">捐赠件</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">-</div>
        <div class="stat-lbl">领取件</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ userStore.profile?.pointBalance || 0 }}</div>
        <div class="stat-lbl">积分</div>
      </div>
    </div>

    <div class="menu-list">
      <button
        v-for="menu in menuList"
        :key="menu.path"
        class="menu-row"
        @click="$router.push(menu.path)"
      >
        <span class="menu-left">
          <span class="menu-dot" :style="{ background: menu.color }"></span>
          <span class="menu-text">{{ menu.title }}</span>
        </span>
        <span class="menu-arrow">›</span>
      </button>
    </div>

    <div class="section no-bg">
      <button class="btn btn-light btn-block" @click="onClickLogout">退出登录</button>
    </div>
  </div>
</template>
