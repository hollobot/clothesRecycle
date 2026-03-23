<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelMyPublishedItem, getItemDetail, getMyPublishedItems } from '@/api/item'

const loading = ref(false)
const items = ref([])
const detailVisible = ref(false)
const activeDetail = ref(null)
const cancellingItemId = ref(null)

const statusTypeMap = {
  PENDING_AUDIT: 'warning',
  ON_SHELF: 'success',
  TRADING: 'primary',
  DONE: 'info',
  REJECTED: 'danger',
  OFF_SHELF: 'info',
  FORCE_OFF_SHELF: 'danger',
}

const statusTextMap = {
  PENDING_AUDIT: '待审核',
  ON_SHELF: '上架中',
  TRADING: '交易中',
  DONE: '已完成',
  REJECTED: '已拒绝',
  OFF_SHELF: '已下架',
  FORCE_OFF_SHELF: '强制下架',
}

const load = async () => {
  loading.value = true
  try {
    items.value = await getMyPublishedItems()
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => statusTextMap[status] || '未知状态'
const getStatusType = (status) => statusTypeMap[status] || 'info'

/**
 * 仅允许对未交易完成的本人发布物品执行取消发布。
 */
const canCancelItem = (item) =>
  ['PENDING_AUDIT', 'ON_SHELF', 'REJECTED'].includes(String(item.status || ''))

const openDetail = async (itemId) => {
  try {
    activeDetail.value = await getItemDetail(itemId)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '加载详情失败')
  }
}

const onCancelPublish = async (item) => {
  if (!canCancelItem(item)) return

  const { value } = await ElMessageBox.prompt('请输入取消发布原因', '取消发布', {
    confirmButtonText: '确认取消',
    cancelButtonText: '暂不取消',
    inputValue: '用户主动取消发布',
    inputPattern: /^.{2,100}$/,
    inputErrorMessage: '原因需 2-100 个字符',
  }).catch(() => ({ value: '' }))
  if (!value) return

  try {
    cancellingItemId.value = item.id
    await cancelMyPublishedItem(item.id, value)
    ElMessage.success('取消发布成功')
    await load()
  } catch (error) {
    ElMessage.error(error.message || '取消发布失败')
  } finally {
    cancellingItemId.value = null
  }
}

const timelineNodes = computed(() => {
  if (!activeDetail.value) return []

  const detail = activeDetail.value
  const nodes = [
    {
      type: 'primary',
      title: '物品已提交',
      time: detail.createTime,
      desc: '等待管理员审核',
    },
  ]

  if (detail.status === 'PENDING_AUDIT') {
    nodes.push({
      type: 'warning',
      title: '审核中',
      time: detail.updateTime,
      desc: '请耐心等待审核结果',
    })
  }

  if (detail.status === 'ON_SHELF') {
    nodes.push({
      type: 'success',
      title: '审核通过并上架',
      time: detail.updateTime,
      desc: '其他用户可申请认领',
    })
  }

  if (detail.status === 'TRADING') {
    nodes.push({
      type: 'primary',
      title: '已进入交易中',
      time: detail.updateTime,
      desc: '已有用户申请认领，请到订单中处理',
    })
  }

  if (detail.status === 'DONE') {
    nodes.push({
      type: 'success',
      title: '交易完成',
      time: detail.updateTime,
      desc: '订单已完成，物品流程结束',
    })
  }

  if (detail.status === 'REJECTED') {
    nodes.push({
      type: 'danger',
      title: '审核被拒绝',
      time: detail.updateTime,
      desc: detail.auditReason || '请修改后重新发布',
    })
  }

  if (detail.status === 'OFF_SHELF' || detail.status === 'FORCE_OFF_SHELF') {
    nodes.push({
      type: 'info',
      title: detail.status === 'OFF_SHELF' ? '已取消发布' : '管理员下架',
      time: detail.updateTime,
      desc: detail.auditReason || '该物品已下架',
    })
  }

  return nodes
})

onMounted(async () => {
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载我的发布失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar"><h1>我的发布</h1></div>

    <div class="section" v-loading="loading">
      <el-empty v-if="items.length === 0" description="暂无发布记录" />

      <el-card v-for="item in items" :key="item.id" class="my-item-card" shadow="never">
        <div class="my-item-head">
          <el-image
            v-if="item.coverUrl"
            :src="item.coverUrl"
            fit="cover"
            class="my-item-cover"
            preview-teleported
          />
          <div v-else class="my-item-cover placeholder">暂无封面</div>
          <div>
            <div class="my-item-title">{{ item.title || '-' }}</div>
            <div class="helper-text">{{ item.category || '-' }} / {{ item.sizeType || '-' }}</div>
            <div class="helper-text">发布时间：{{ item.createTime?.slice?.(0, 16)?.replace?.('T', ' ') || '-' }}</div>
          </div>
          <el-tag :type="getStatusType(item.status)" effect="light">
            {{ getStatusText(item.status) }}
          </el-tag>
        </div>

        <div class="my-item-actions">
          <el-button @click="openDetail(item.id)">查看详情</el-button>
          <el-button
            type="warning"
            :disabled="!canCancelItem(item) || cancellingItemId === item.id"
            @click="onCancelPublish(item)"
          >
            {{ cancellingItemId === item.id ? '取消中...' : '取消发布' }}
          </el-button>
        </div>
      </el-card>
    </div>

    <el-drawer v-model="detailVisible" title="发布详情" :size="'92%'" direction="btt">
      <template v-if="activeDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="物品名称">{{ activeDetail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            {{ getStatusText(activeDetail.status) }}
          </el-descriptions-item>
          <el-descriptions-item label="获取方式">
            {{ activeDetail.acquireType === 'POINT' ? `积分兑换(${activeDetail.pointPrice || 0})` : '免费领取' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述">{{ activeDetail.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 14px">
          <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px">状态时间线</div>
          <el-timeline>
            <el-timeline-item
              v-for="(node, index) in timelineNodes"
              :key="`${activeDetail.id}-${index}`"
              :type="node.type"
              :timestamp="node.time?.slice?.(0, 16)?.replace?.('T', ' ') || '-'"
            >
              <div style="font-weight: 600">{{ node.title }}</div>
              <div style="font-size: 12px; color: #6b7280">{{ node.desc }}</div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped>
.my-item-card {
  margin-bottom: 10px;
}

.my-item-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.my-item-cover {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  flex-shrink: 0;
  border: 1px solid var(--gray-200);
}

.my-item-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gray-50);
  color: var(--gray-400);
  font-size: 11px;
}

.my-item-title {
  font-size: 14px;
  font-weight: 600;
}

.my-item-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
