<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodaySignStatus, getYearSignData, signToday } from '@/api/sign'

const status = ref({ signed: false, continuousDays: 0, todayPoints: 0 })
const selectedYear = ref(new Date().getFullYear())
const yearData = ref({ data: [], signedCount: 0, continuousDays: 0 })
const yearOptions = [selectedYear.value, selectedYear.value - 1, selectedYear.value - 2]

const load = async () => {
  status.value = await getTodaySignStatus()
}

const doSign = async () => {
  try {
    status.value = await signToday()
    await loadYearData()
    ElMessage.success('签到成功')
  } catch (error) {
    ElMessage.error(error.message || '签到失败')
  }
}

const loadYearData = async () => {
  yearData.value = await getYearSignData(selectedYear.value)
}

const cellClass = (cell) => {
  if (!cell.signed) return 'hm-cell'
  return 'hm-cell s2'
}

const weeklyGrid = () => {
  const source = yearData.value.data || []
  const cells = [...source]
  const extra = (7 - (cells.length % 7)) % 7
  for (let i = 0; i < extra; i += 1) {
    cells.push({ date: `extra-${i}`, signed: false, extra: true })
  }
  return cells
}

const onYearChange = async () => {
  try {
    await loadYearData()
  } catch (error) {
    ElMessage.error(error.message || '加载年度签到数据失败')
  }
}

onMounted(async () => {
  try {
    await Promise.all([load(), loadYearData()])
  } catch (error) {
    ElMessage.error(error.message || '加载签到信息失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>每日签到</h1>
      <el-select v-model="selectedYear" style="width: 110px" @change="onYearChange">
        <el-option v-for="year in yearOptions" :key="year" :label="`${year}年`" :value="year" />
      </el-select>
    </div>

    <div class="section">
      <p>今日状态：{{ status.signed ? '已签到' : '未签到' }}</p>
      <p style="margin-top: 6px">连续签到：{{ status.continuousDays }} 天</p>
      <p style="margin-top: 6px">今日奖励：{{ status.todayPoints }} 积分</p>
      <el-button
        type="primary"
        style="margin-top: 10px"
        :disabled="status.signed"
        @click="doSign"
        >{{ status.signed ? '今日已签到' : '立即签到' }}</el-button
      >
    </div>

    <div class="section">
      <div class="section-title-row">
        <h3>{{ selectedYear }} 年签到热力图</h3>
        <small>已签到 {{ yearData.signedCount || 0 }} 天</small>
      </div>
      <div class="heatmap-grid">
        <div
          v-for="cell in weeklyGrid()"
          :key="cell.date"
          :class="cellClass(cell)"
          :title="cell.extra ? '' : cell.date"
        ></div>
      </div>
      <p style="margin-top: 10px; color: var(--gray-600)">
        连续签到：{{ yearData.continuousDays || 0 }} 天
      </p>
    </div>
  </div>
</template>
