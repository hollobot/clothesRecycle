import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// C 端采用统一壳布局：页面主体 + 底部 TabBar，保证移动端体验一致。
const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/ClientLayout.vue'),
    children: [
      { path: '', name: 'item-list', component: () => import('@/views/item/ItemListView.vue') },
      {
        path: 'item/:id',
        name: 'item-detail',
        component: () => import('@/views/item/ItemDetailView.vue'),
      },
      {
        path: 'publish',
        name: 'publish',
        component: () => import('@/views/item/PublishItemView.vue'),
        meta: { auth: true },
      },
      {
        path: 'messages',
        name: 'messages',
        component: () => import('@/views/message/MessagesView.vue'),
        meta: { auth: true },
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { auth: true },
      },
      {
        path: 'orders',
        name: 'orders',
        component: () => import('@/views/order/OrderView.vue'),
        meta: { auth: true },
      },
      {
        path: 'favorites',
        name: 'favorites',
        component: () => import('@/views/favorite/FavoritesView.vue'),
        meta: { auth: true },
      },
      {
        path: 'points',
        name: 'points',
        component: () => import('@/views/point/PointView.vue'),
        meta: { auth: true },
      },
      {
        path: 'points/mall',
        name: 'points-mall',
        component: () => import('@/views/point/PointsMallView.vue'),
        meta: { auth: true },
      },
      {
        path: 'sign',
        name: 'sign',
        component: () => import('@/views/sign/SignView.vue'),
        meta: { auth: true },
      },
      {
        path: 'rank',
        name: 'rank',
        component: () => import('@/views/rank/RankView.vue'),
      },
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { hideTabBar: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { hideTabBar: true },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

// 路由守卫：处理鉴权和登录回跳。
router.beforeEach((to) => {
  const store = useUserStore()

  if (to.meta.auth && !store.isLogin) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.name === 'login' && store.isLogin) {
    return { name: 'item-list' }
  }

  return true
})

export default router
