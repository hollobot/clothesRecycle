import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/auth/AdminLoginView.vue'),
    meta: { title: '管理员登录' },
  },
  {
    path: '/',
    component: () => import('@/views/AdminLayout.vue'),
    meta: { auth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '数据看板' },
      },
      {
        path: 'campus',
        component: () => import('@/views/campus/CampusView.vue'),
        meta: { title: '校区管理', superOnly: true },
      },
      {
        path: 'admins',
        component: () => import('@/views/admin/AdminManageView.vue'),
        meta: { title: '管理员管理', superOnly: true },
      },
      {
        path: 'drop-points',
        component: () => import('@/views/drop-point/DropPointView.vue'),
        meta: { title: '回收点管理' },
      },
      {
        path: 'items',
        component: () => import('@/views/item/ItemAuditView.vue'),
        meta: { title: '物品审核' },
      },
      {
        path: 'users',
        component: () => import('@/views/user/UserManageView.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'gifts',
        component: () => import('@/views/gift/GiftManageView.vue'),
        meta: { title: '礼品管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to) => {
  const store = useAdminStore()
  if (to.path !== '/login' && !store.isLogin) {
    return '/login'
  }

  if (to.path === '/login' && store.isLogin) {
    return '/dashboard'
  }

  if (to.meta?.superOnly && !store.isSuperAdmin) {
    return '/dashboard'
  }

  return true
})

export default router
