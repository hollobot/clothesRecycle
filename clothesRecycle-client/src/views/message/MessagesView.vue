<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMessagePage, markAllMessagesRead, markMessageRead } from '@/api/message'
import { useNotifyStore } from '@/stores/notify'

const notifyStore = useNotifyStore()
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
      <article class="message-row" v-for="message in pageData.records" :key="message.id">
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
        <button
          v-if="message.readStatus !== 1"
          class="btn btn-light"
          @click="onMarkRead(message.id)"
        >
          标记已读
        </button>
      </article>
    </div>
  </div>
</template>
