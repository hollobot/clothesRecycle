<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPointAccount, getPointRecords } from '@/api/point'

const account = ref({ balance: 0, frozen: 0 })
const pageData = ref({ records: [], total: 0, pageNum: 1, pageSize: 8 })
const loading = ref(false)

const loadAccount = async () => {
  account.value = await getPointAccount()
}

const loadRecords = async () => {
  pageData.value = await getPointRecords(pageData.value.pageNum, pageData.value.pageSize)
}

const onPageChange = async (currentPage) => {
  pageData.value.pageNum = currentPage
  await loadRecords()
}

const amountClass = (amount) => (Number(amount) >= 0 ? 'point-up' : 'point-down')
const amountText = (amount) => (Number(amount) > 0 ? `+${amount}` : String(amount || 0))

// 账户与流水并行加载，减少页面等待时间。
const loadAll = async () => {
  loading.value = true
  try {
    await Promise.all([loadAccount(), loadRecords()])
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    await loadAll()
  } catch (error) {
    ElMessage.error(error.message || '加载积分信息失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>积分中心</h1>
      <el-button type="primary" plain @click="$router.push('/points/mall')">礼品商城</el-button>
    </div>

    <div class="section" v-loading="loading">
      <div class="point-hero">
        <div class="point-hero-title">我的积分</div>
        <div class="point-hero-value">{{ account.balance || 0 }}</div>
        <div class="point-hero-sub">冻结积分：{{ account.frozen || 0 }}</div>
      </div>
    </div>

    <div class="section" v-loading="loading">
      <div class="section-title-row">
        <h3>积分明细</h3>
        <small>共 {{ pageData.total }} 条</small>
      </div>

      <el-empty v-if="pageData.records.length === 0" description="暂无积分记录" />

      <article class="point-record" v-for="row in pageData.records" :key="row.id">
        <div>
          <p class="point-record-title">{{ row.remark || row.bizType || '积分变动' }}</p>
          <small class="point-record-time">{{
            row.createTime?.slice(0, 16)?.replace('T', ' ') || '-'
          }}</small>
        </div>
        <strong :class="amountClass(row.changeAmount)">{{ amountText(row.changeAmount) }}</strong>
      </article>

      <el-pagination
        v-if="pageData.total > pageData.pageSize"
        background
        layout="prev, pager, next"
        :total="pageData.total"
        :page-size="pageData.pageSize"
        :current-page="pageData.pageNum"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>
