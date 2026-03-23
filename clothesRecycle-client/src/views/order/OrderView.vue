<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelOrder, completeOrder, confirmOrder, listMyOrders } from '@/api/order'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const orders = ref([])
const loading = ref(false)
const activeTab = ref('buyer')

const currentUserId = computed(() =>
  Number(userStore.profile?.userId || userStore.profile?.id || 0),
)

const statusConfig = {
  // 订单状态色统一跟随系统绿色主题。
  PENDING_CONFIRM: { text: '待确认', type: 'primary' },
  PENDING_DELIVERY: { text: '待交接', type: 'success' },
  DONE: { text: '已完成', type: 'info' },
  CANCELLED: { text: '已取消', type: 'danger' },
}

// 根据当前用户身份切换订单视图，保持与原型「我发起的/我收到的」一致。
const visibleOrders = computed(() => {
  if (activeTab.value === 'buyer') {
    return orders.value.filter((order) => Number(order.buyerId) === currentUserId.value)
  }
  return orders.value.filter((order) => Number(order.sellerId) === currentUserId.value)
})

const load = async () => {
  loading.value = true
  try {
    orders.value = await listMyOrders()
  } finally {
    loading.value = false
  }
}

const canConfirm = (order) => activeTab.value === 'seller' && order.status === 'PENDING_CONFIRM'
const canComplete = (order) => order.status === 'PENDING_DELIVERY'
const canCancel = (order) => ['PENDING_CONFIRM', 'PENDING_DELIVERY'].includes(order.status)

const getAcquireText = (order) => {
  if (order.acquireType === 'POINT') {
    return `积分兑换（${order.pointAmount || 0}）`
  }
  return '免费领取'
}

const operate = async (type, id) => {
  try {
    if (type === 'confirm') await confirmOrder(id)
    if (type === 'cancel') await cancelOrder(id)
    if (type === 'complete') await completeOrder(id)
    await load()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(async () => {
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载订单失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar"><h1>我的订单</h1></div>

    <div class="seg-ctrl">
      <el-segmented
        v-model="activeTab"
        :options="[
          { label: '我发起的', value: 'buyer' },
          { label: '我收到的', value: 'seller' },
        ]"
        block
      />
    </div>

    <div class="section" v-loading="loading">
      <el-empty v-if="visibleOrders.length === 0" description="暂无订单记录" />

      <article class="order-card" v-for="order in visibleOrders" :key="order.id">
        <div class="order-title-row">
          <strong>订单 #{{ order.id }}</strong>
          <el-tag :type="statusConfig[order.status]?.type || 'info'" effect="light">
            {{ statusConfig[order.status]?.text || order.status || '-' }}
          </el-tag>
        </div>

        <p class="order-meta">方式：{{ getAcquireText(order) }}</p>
        <p class="order-meta">备注：{{ order.remark || '无' }}</p>
        <p class="order-meta">
          创建时间：{{ order.createTime?.slice(0, 16)?.replace('T', ' ') || '-' }}
        </p>

        <div class="order-actions">
          <el-button v-if="canConfirm(order)" @click="operate('confirm', order.id)"
            >同意申请</el-button
          >
          <el-button v-if="canCancel(order)" @click="operate('cancel', order.id)"
            >取消订单</el-button
          >
          <el-button
            v-if="canComplete(order)"
            type="primary"
            @click="operate('complete', order.id)"
          >
            确认完成
          </el-button>
        </div>
      </article>
    </div>
  </div>
</template>
