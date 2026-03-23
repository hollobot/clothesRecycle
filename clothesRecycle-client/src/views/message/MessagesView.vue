<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMessagePage, markAllMessagesRead, markMessageRead } from '@/api/message'
import { useNotifyStore } from '@/stores/notify'

const notifyStore = useNotifyStore()
const router = useRouter()
const pageData = ref({ records: [], total: 0, pageNum: 1, pageSize: 10 })

const load = async () => {
  pageData.value = await getMessagePage(pageData.value.pageNum, pageData.value.pageSize)
}

const onMarkRead = async (messageId) => {
  try {
    await markMessageRead(messageId)
    await Promise.all([load(), notifyStore.loadUnreadCount()])
  } catch (error) {
    ElMessage.error(error.message || '标记已读失败')
  }
}

const onReadAll = async () => {
  try {
    await markAllMessagesRead()
    await Promise.all([load(), notifyStore.loadUnreadCount()])
    ElMessage.success('已全部标记已读')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

/**
 * 消息跳转规则：审核消息跳转我的发布，订单消息跳转我的订单。
 */
const resolveMessageRoute = (message) => {
  const type = String(message?.msgType || '').toUpperCase()
  if (type === 'AUDIT') return '/my-items'
  if (type === 'ORDER') return '/orders'
  return '/messages'
}

const onOpenMessage = async (message) => {
  try {
    if (message.readStatus !== 1) {
      await markMessageRead(message.id)
      await notifyStore.loadUnreadCount()
    }
    router.push(resolveMessageRoute(message))
  } catch (error) {
    ElMessage.error(error.message || '打开消息失败')
  }
}

onMounted(async () => {
  try {
    await Promise.all([load(), notifyStore.loadUnreadCount()])
  } catch (error) {
    ElMessage.error(error.message || '加载消息失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>消息通知</h1>
      <button class="ghost-btn" @click="onReadAll">全部已读</button>
    </div>

    <div class="section" v-if="pageData.records.length === 0">
      <p class="helper-text">暂无消息</p>
    </div>

    <div class="section" v-else>
      <article
        class="message-row"
        :class="{ unread: message.readStatus !== 1 }"
        v-for="message in pageData.records"
        :key="message.id"
      >
        <div class="notif-icon" :class="message.readStatus === 1 ? 'ni-blue' : 'ni-green'"></div>
        <div class="message-main">
          <div class="message-title-row">
            <strong>{{ message.title }}</strong>
            <span class="chip" :class="message.readStatus === 1 ? '' : 'active'">
              {{ message.readStatus === 1 ? '已读' : '未读' }}
            </span>
          </div>
          <p class="message-content">{{ message.content }}</p>
          <small class="message-time">{{
            message.createTime?.slice(0, 16)?.replace('T', ' ') || '-'
          }}</small>
        </div>
        <div class="message-right">
          <button
            v-if="message.readStatus !== 1"
            class="btn btn-light message-action-btn"
            @click="onMarkRead(message.id)"
          >
            标记已读
          </button>
          <span class="message-arrow">›</span>
        </div>
        <button class="message-mask-btn" @click="onOpenMessage(message)">查看详情</button>
      </article>
    </div>
  </div>
</template>
