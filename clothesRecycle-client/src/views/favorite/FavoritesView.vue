<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavoriteList, removeFavorite } from '@/api/favorite'

const list = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    list.value = await getFavoriteList()
  } finally {
    loading.value = false
  }
}

// 收藏列表删除后立即刷新，保证状态与后端一致。
const onRemove = async (itemId) => {
  try {
    await removeFavorite(itemId)
    await load()
  } catch (error) {
    ElMessage.error(error.message || '取消收藏失败')
  }
}

onMounted(async () => {
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载收藏列表失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>我的收藏</h1>
      <button class="ghost-btn" @click="$router.push('/profile')">返回我的</button>
    </div>

    <div class="section" v-loading="loading">
      <el-empty v-if="list.length === 0" description="暂无收藏物品" />

      <article class="order-card" v-for="item in list" :key="item.id">
        <div class="order-title-row">
          <strong>{{ item.title }}</strong>
          <span class="chip">{{ item.category }}</span>
        </div>

        <p class="order-meta">{{ item.description || '暂无描述' }}</p>
        <p class="order-meta">
          {{ item.acquireType === 'POINT' ? `${item.pointPrice || 0} 积分` : '免费领取' }}
        </p>

        <div class="order-actions">
          <el-button @click="$router.push(`/item/${item.id}`)">查看详情</el-button>
          <el-button type="danger" plain @click="onRemove(item.id)">取消收藏</el-button>
        </div>
      </article>
    </div>
  </div>
</template>
