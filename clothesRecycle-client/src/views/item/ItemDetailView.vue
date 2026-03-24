<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addFavorite, getFavoriteList, removeFavorite } from '@/api/favorite'
import { createOrder } from '@/api/order'
import { getItemDetail } from '@/api/item'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const detail = ref(null)
const isFavorite = ref(false)
const favoriteLoading = ref(false)
const claimLoading = ref(false)
const currentUserId = computed(() =>
  Number(userStore.profile?.userId || userStore.profile?.id || 0),
)

const statusTextMap = {
  ON_SHELF: '可认领',
  TRADING: '交易中',
  DONE: '已完成',
  PENDING_AUDIT: '待审核',
  REJECTED: '已拒绝',
  OFF_SHELF: '已下架',
  FORCE_OFF_SHELF: '强制下架',
}

const gallery = computed(() => {
  if (!detail.value) return []
  if (Array.isArray(detail.value.imageUrls) && detail.value.imageUrls.length > 0) {
    return detail.value.imageUrls
  }
  return detail.value.coverUrl ? [detail.value.coverUrl] : []
})

const isClaimable = () => detail.value?.status === 'ON_SHELF'
const isOwnItem = computed(() => Number(detail.value?.userId || 0) === currentUserId.value)
const favoriteDisabled = computed(() => favoriteLoading.value || isOwnItem.value)
const claimDisabled = computed(() => !isClaimable() || isOwnItem.value || claimLoading.value)

const loadFavorite = async () => {
  if (!userStore.isLogin) {
    isFavorite.value = false
    return
  }
  const favorites = await getFavoriteList()
  isFavorite.value = favorites.some((item) => Number(item.id) === Number(route.params.id))
}

const load = async () => {
  detail.value = await getItemDetail(route.params.id)
}

const handleClaim = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录再认领')
    return
  }
  if (isOwnItem.value) {
    ElMessage.warning('自己发布的物品不能认领')
    return
  }
  if (!isClaimable()) {
    ElMessage.warning('当前物品不可认领')
    return
  }
  if (claimLoading.value) return

  try {
    claimLoading.value = true
    await createOrder({ itemId: Number(route.params.id), remark: '我想认领这件衣物' })
    ElMessage.success('申请认领成功')
    await load()
  } catch (error) {
    ElMessage.error(error.message || '认领失败')
  } finally {
    claimLoading.value = false
  }
}

const handleToggleFavorite = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录再收藏')
    return
  }
  if (isOwnItem.value) {
    ElMessage.warning('自己发布的物品无需收藏')
    return
  }
  if (favoriteLoading.value) return

  try {
    favoriteLoading.value = true
    if (isFavorite.value) {
      await removeFavorite(Number(route.params.id))
      isFavorite.value = false
      ElMessage.success('已取消收藏')
      return
    }
    await addFavorite(Number(route.params.id))
    isFavorite.value = true
    ElMessage.success('收藏成功')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const formatAcquire = () => {
  if (!detail.value) return '-'
  return detail.value.acquireType === 'POINT'
    ? `积分兑换（${detail.value.pointPrice || 0} 积分）`
    : '免费领取'
}

onMounted(async () => {
  try {
    await Promise.all([load(), loadFavorite()])
  } catch (error) {
    ElMessage.error(error.message || '加载物品详情失败')
  }
})
</script>

<template>
  <div class="page" v-if="detail">
    <div class="topbar topbar-back">
      <button class="ghost-btn" @click="$router.back()">返回</button>
      <h1>物品详情</h1>
      <span style="width: 54px"></span>
    </div>

    <div class="detail-hero" v-if="gallery.length > 0">
      <el-carousel height="150px" indicator-position="outside">
        <el-carousel-item v-for="url in gallery" :key="url">
          <img :src="url" alt="物品图片" class="item-thumb-img" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="section">
      <h2 class="detail-title">{{ detail.title }}</h2>
      <div class="detail-tags">
        <span class="chip">{{ detail.category }}</span>
        <span class="chip">{{ detail.genderType }}</span>
        <span class="chip">{{ detail.sizeType }}</span>
        <span class="chip">{{ detail.conditionLevel }}</span>
      </div>
      <p class="detail-desc">{{ detail.description || '暂无描述' }}</p>
      <p class="helper-text">状态：{{ statusTextMap[detail.status] || '未知状态' }}</p>
      <p class="helper-text">获取方式：{{ formatAcquire() }}</p>
      <p class="helper-text">发布时间：{{ detail.createTime?.slice(0, 10) || '-' }}</p>

      <div class="action-row">
        <el-button :type="isFavorite ? 'primary' : 'default'" :disabled="favoriteDisabled" @click="handleToggleFavorite">
          {{ isOwnItem ? '我的发布（不可收藏）' : favoriteLoading ? '处理中...' : isFavorite ? '已收藏，点我取消' : '收藏' }}
        </el-button>
        <el-button type="success" :disabled="claimDisabled" @click="handleClaim">
          {{ isOwnItem ? '本人发布不可认领' : claimLoading ? '申请中...' : isClaimable() ? '申请认领' : '暂不可认领' }}
        </el-button>
      </div>
    </div>
  </div>
</template>
