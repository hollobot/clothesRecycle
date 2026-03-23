<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelOrder, completeOrder, confirmOrder, listMyOrders } from '@/api/order'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const orders = ref([])
const loading = ref(false)
const activeTab = ref('buyer')
const detailVisible = ref(false)
const activeOrder = ref(null)
const isMobile = ref(false)

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

const itemStatusTextMap = {
  ON_SHELF: '上架中',
  TRADING: '交易中',
  DONE: '已完成',
  REJECTED: '已拒绝',
  FORCE_OFF_SHELF: '强制下架',
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

/**
 * 后端已返回当前登录用户的可操作状态，前端仅做兜底布尔转换。
 */
const canConfirm = (order) => !!order.canConfirm
const canComplete = (order) => !!order.canComplete
const canCancel = (order) => !!order.canCancel

const getAcquireText = (order) => {
  if (order.acquireType === 'POINT') {
    return `积分兑换（${order.pointAmount || 0}）`
  }
  return '免费领取'
}

const getItemStatusText = (status) => itemStatusTextMap[status] || status || '-'

const getCancelBtnText = (order) => {
  const isSellerOrder = Number(order.sellerId) === currentUserId.value
  if (order.status === 'PENDING_CONFIRM' && isSellerOrder) {
    return '拒绝申请'
  }
  return '取消订单'
}

/**
 * 订单状态时间线配置。
 */
const timelineLabelMap = {
  PENDING_CONFIRM: '待发布方确认',
  PENDING_DELIVERY: '待双方交接',
  DONE: '订单已完成',
  CANCELLED: '订单已取消',
}

const buildTimeline = (order) => {
  if (!order) return []

  const timeline = [
    {
      type: 'primary',
      title: '订单已创建',
      time: order.createTime,
      description: '系统已生成订单，等待下一步处理',
    },
  ]

  if (order.status === 'PENDING_CONFIRM') {
    timeline.push({
      type: 'warning',
      title: timelineLabelMap.PENDING_CONFIRM,
      time: order.updateTime,
      description: '请发布方尽快确认申请',
    })
  }

  if (order.status === 'PENDING_DELIVERY') {
    timeline.push({
      type: 'success',
      title: timelineLabelMap.PENDING_DELIVERY,
      time: order.updateTime,
      description: '双方线下交接后任一方可确认完成',
    })
  }

  if (order.status === 'DONE') {
    timeline.push({
      type: 'success',
      title: timelineLabelMap.DONE,
      time: order.updateTime,
      description: '积分结算已完成',
    })
  }

  if (order.status === 'CANCELLED') {
    timeline.push({
      type: 'danger',
      title: timelineLabelMap.CANCELLED,
      time: order.updateTime,
      description: '订单已取消，物品会自动恢复可认领状态',
    })
  }

  return timeline
}

const openDetail = (order) => {
  activeOrder.value = order
  detailVisible.value = true
}

/**
 * 同步移动端状态，用于调整订单详情抽屉布局。
 */
const syncMobileState = () => {
  isMobile.value = window.innerWidth < 768
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
  syncMobileState()
  window.addEventListener('resize', syncMobileState)
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载订单失败')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', syncMobileState)
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
        <el-image
          v-if="order.itemCoverUrl"
          :src="order.itemCoverUrl"
          fit="cover"
          style="width: 72px; height: 72px; border-radius: 10px; margin-bottom: 10px"
          preview-teleported
        />
        <div class="order-title-row">
          <strong>{{ order.itemTitle || `订单 #${order.id}` }}</strong>
          <el-tag :type="statusConfig[order.status]?.type || 'info'" effect="light">
            {{ statusConfig[order.status]?.text || '未知状态' }}
          </el-tag>
        </div>

        <p class="order-meta">物品状态：{{ getItemStatusText(order.itemStatus) }}</p>

        <p class="order-meta">方式：{{ getAcquireText(order) }}</p>
        <p class="order-meta">备注：{{ order.remark || '无' }}</p>
        <p class="order-meta">
          创建时间：{{ order.createTime?.slice(0, 16)?.replace('T', ' ') || '-' }}
        </p>

        <div class="order-actions">
          <el-button
            v-if="activeTab === 'seller'"
            type="success"
            :disabled="!canConfirm(order)"
            @click="operate('confirm', order.id)"
          >
            同意申请
          </el-button>
          <el-button type="warning" :disabled="!canCancel(order)" @click="operate('cancel', order.id)">
            {{ getCancelBtnText(order) }}
          </el-button>
          <el-button type="primary" :disabled="!canComplete(order)" @click="operate('complete', order.id)">
            确认完成
          </el-button>
          <el-button @click="openDetail(order)">查看详情</el-button>
        </div>
      </article>
    </div>

    <el-drawer
      v-model="detailVisible"
      title="订单详情"
      :direction="isMobile ? 'btt' : 'rtl'"
      :size="isMobile ? '92%' : '420px'"
    >
      <template v-if="activeOrder">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ activeOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="物品">{{ activeOrder.itemTitle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            {{ statusConfig[activeOrder.status]?.text || '未知状态' }}
          </el-descriptions-item>
          <el-descriptions-item label="物品状态">
            {{ getItemStatusText(activeOrder.itemStatus) }}
          </el-descriptions-item>
          <el-descriptions-item label="获取方式">{{ getAcquireText(activeOrder) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ activeOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 16px">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px">订单进度</div>
          <el-timeline>
            <el-timeline-item
              v-for="(node, index) in buildTimeline(activeOrder)"
              :key="`${activeOrder.id}-${index}`"
              :type="node.type"
              :timestamp="node.time?.slice?.(0, 16)?.replace?.('T', ' ') || '-'"
            >
              <div style="font-weight: 600">{{ node.title }}</div>
              <div style="color: #6b7280; font-size: 12px">{{ node.description }}</div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-drawer>
  </div>
</template>
