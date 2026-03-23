<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserOverview } from '@/api/profile'
import { useNotifyStore } from '@/stores/notify'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const notifyStore = useNotifyStore()
const overview = ref({ donateCount: 0, claimCount: 0, pointBalance: 0 })

const menuList = [
  { title: '编辑资料', path: '/profile/edit', color: 'var(--green-pale)' },
  { title: '我的发布', path: '/my-items', color: 'var(--amber-pale)' },
  { title: '我的订单', path: '/orders', color: 'var(--green-pale)' },
  { title: '我的收藏', path: '/favorites', color: 'var(--blue-pale)' },
  { title: '积分中心', path: '/points', color: 'var(--green-pale)' },
  { title: '礼品商城', path: '/points/mall', color: 'var(--amber-pale)' },
  { title: '每日签到', path: '/sign', color: 'var(--green-pale)' },
  { title: '排行榜', path: '/rank', color: 'var(--blue-pale)' },
]

const avatarText = computed(() => String(userStore.profile?.name || '用').slice(0, 1))
const profileSubText = computed(() => {
  // 登录接口返回可能不含 phone 字段，避免误显示“请先登录”。
  if (!userStore.isLogin) return '请先登录'
  return userStore.profile?.phone || userStore.profile?.studentId || '已登录'
})

onMounted(() => {
  if (userStore.isLogin) {
    Promise.all([notifyStore.loadUnreadCount(), loadOverview()]).catch((error) => {
      ElMessage.error(error?.message || '个人中心数据加载失败')
    })
  }
})

/**
 * 拉取个人中心统计数据并同步积分显示。
 */
const loadOverview = async () => {
  const data = await getUserOverview()
  overview.value = {
    donateCount: Number(data?.donateCount || 0),
    claimCount: Number(data?.claimCount || 0),
    pointBalance: Number(data?.pointBalance || 0),
  }
}

const onClickLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="page">
    <div class="profile-hero">
      <el-avatar class="avatar-lg" :size="58" :src="userStore.profile?.avatarUrl">
        {{ avatarText }}
      </el-avatar>
      <div class="profile-info">
        <div class="pname">{{ userStore.profile?.name || '未登录用户' }}</div>
        <div class="psub">{{ profileSubText }}</div>
      </div>
      <button class="ghost-btn ghost-light" @click="$router.push('/messages')">
        消息 <span v-if="notifyStore.unreadCount > 0">({{ notifyStore.unreadCount }})</span>
      </button>
    </div>

    <div class="stat-strip">
      <div class="stat-cell">
        <div class="stat-num">{{ overview.donateCount }}</div>
        <div class="stat-lbl">捐赠件</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ overview.claimCount }}</div>
        <div class="stat-lbl">领取件</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ overview.pointBalance }}</div>
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
