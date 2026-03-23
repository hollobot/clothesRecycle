<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavoriteList, removeFavorite } from '@/api/favorite'

const list = ref([])

const load = async () => {
  list.value = await getFavoriteList()
}

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
      <el-button @click="$router.push('/profile')">返回我的</el-button>
    </div>

    <div class="section" v-if="list.length === 0">
      <p>暂无收藏物品</p>
    </div>

    <div class="order-card" v-for="item in list" :key="item.id">
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
        <el-button @click="onRemove(item.id)">取消收藏</el-button>
      </div>
    </div>
  </div>
</template>
