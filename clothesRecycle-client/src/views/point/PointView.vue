<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPointAccount, getPointRecords } from '@/api/point'

const account = ref({ balance: 0, frozen: 0 })
const pageData = ref({ records: [], total: 0, pageNum: 1, pageSize: 6 })

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

onMounted(async () => {
  try {
    await Promise.all([loadAccount(), loadRecords()])
  } catch (error) {
    ElMessage.error(error.message || '加载积分信息失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>积分中心</h1>
      <el-button @click="$router.push('/points/mall')">礼品商城</el-button>
    </div>

    <div class="section">
      <p>
        可用积分：<strong style="color: var(--green)">{{ account.balance }}</strong>
      </p>
      <p style="margin-top: 8px">
        冻结积分：<strong style="color: var(--amber)">{{ account.frozen }}</strong>
      </p>
    </div>

    <div class="section">
      <div class="section-title-row">
        <h3>积分明细</h3>
        <small>共 {{ pageData.total }} 条</small>
      </div>
      <div class="point-record" v-for="row in pageData.records" :key="row.id">
        <div>
          <p class="point-record-title">{{ row.remark || row.bizType || '积分变动' }}</p>
          <small class="point-record-time">{{
            row.createTime?.slice(0, 16)?.replace('T', ' ') || '-'
          }}</small>
        </div>
        <strong :class="amountClass(row.changeAmount)">{{ amountText(row.changeAmount) }}</strong>
      </div>
      <el-pagination
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
