<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const route = useRoute()
const router = useRouter()
const store = useAdminStore()

const topNavItems = computed(() => {
  // 超级管理员可访问礼品管理；校区管理员不展示该入口。
  const base = [
    { to: '/dashboard', label: '数据看板' },
    { to: '/items', label: '物品管理' },
    { to: '/drop-points', label: '回收点管理' },
  ]

  if (store.isSuperAdmin) {
    return [
      { to: '/campus', label: '校区管理' },
      ...base,
      { to: '/gifts', label: '积分礼品' },
      { to: '/admins', label: '管理员账号' },
    ]
  }

  return base
})

const sideNavGroups = computed(() => {
  const groups = [
    {
      label: '概览',
      items: [{ to: '/dashboard', label: '数据看板', color: 'var(--green)' }],
    },
    {
      label: '内容管理',
      items: [
        { to: '/items', label: '物品审核', color: 'var(--amber)' },
        { to: '/drop-points', label: '回收点管理', color: '#52b788' },
      ],
    },
  ]

  if (store.isSuperAdmin) {
    groups[1].items.push({ to: '/gifts', label: '礼品管理', color: '#e07b54' })
    groups.unshift({
      label: '系统管理',
      items: [
        { to: '/campus', label: '校区管理', color: 'var(--amber)' },
        { to: '/admins', label: '管理员账号', color: '#e07b54' },
      ],
    })
  }

  groups.push({
    label: '用户',
    items: [{ to: '/users', label: '用户管理', color: 'var(--blue)' }],
  })

  return groups
})

const currentTitle = computed(() => route.meta?.title || '管理后台')

const logout = () => {
  store.logout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-app-shell">
    <header class="admin-topbar">
      <div class="admin-logo">
        <div class="logo-badge"><div class="logo-badge-inner"></div></div>
        校园二手衣物回收
      </div>
      <nav class="admin-nav-links">
        <RouterLink
          v-for="item in topNavItems"
          :key="item.to"
          :to="item.to"
          class="admin-nav-link"
          :class="{ active: route.path.startsWith(item.to) }"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
      <div class="admin-spacer"></div>
      <div class="admin-user-chip">
        <div class="auser-av" :class="{ super: store.isSuperAdmin }">
          {{ store.isSuperAdmin ? '超' : '管' }}
        </div>
        <div class="auser-name">{{ store.displayName }}</div>
      </div>
      <button class="th-btn" @click="logout">退出登录</button>
    </header>

    <div class="admin-body">
      <aside class="admin-sidebar">
        <template v-for="group in sideNavGroups" :key="group.label">
          <div class="sidebar-group-lbl">{{ group.label }}</div>
          <RouterLink
            v-for="item in group.items"
            :key="item.to"
            :to="item.to"
            class="sidebar-item"
            :class="{ active: route.path.startsWith(item.to) }"
          >
            <span class="si-dot" :style="{ backgroundColor: item.color }"></span>
            <span class="si-text">{{ item.label }}</span>
          </RouterLink>
        </template>
      </aside>

      <main class="admin-main">
        <section class="admin-content">
          <h1 class="admin-page-h">{{ currentTitle }}</h1>
          <RouterView />
        </section>
      </main>
    </div>
  </div>
</template>
