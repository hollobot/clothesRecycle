<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodaySignStatus, getYearSignData, signToday } from '@/api/sign'

const status = ref({ signed: false, continuousDays: 0, todayPoints: 0 })
const selectedYear = ref(new Date().getFullYear())
const yearData = ref({ data: [], signedCount: 0, continuousDays: 0 })
const loading = ref(false)

const yearOptions = [selectedYear.value, selectedYear.value - 1, selectedYear.value - 2]

const loadToday = async () => {
  status.value = await getTodaySignStatus()
}

const loadYearData = async () => {
  yearData.value = await getYearSignData(selectedYear.value)
}

const weekdayList = ['日', '一', '二', '三', '四', '五', '六']

// 将年度签到数据映射到每个月，便于按月展示并形成明显分组间隔。
const monthCards = computed(() => {
  const signedMap = new Map(
    (Array.isArray(yearData.value.data) ? yearData.value.data : []).map((row) => [
      row.date,
      !!row.signed,
    ]),
  )

  const year = Number(selectedYear.value)
  const result = []

  for (let month = 1; month <= 12; month += 1) {
    const firstDayWeek = new Date(year, month - 1, 1).getDay()
    const monthDays = new Date(year, month, 0).getDate()
    const cells = []

    for (let i = 0; i < firstDayWeek; i += 1) {
      cells.push({ key: `empty-${month}-${i}`, empty: true })
    }

    let signedCount = 0
    for (let day = 1; day <= monthDays; day += 1) {
      const date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      const signed = signedMap.get(date) || false
      if (signed) signedCount += 1
      cells.push({ key: date, empty: false, day, signed, date })
    }

    result.push({
      month,
      title: `${month}月`,
      signedCount,
      cells,
    })
  }

  return result
})

const doSign = async () => {
  try {
    status.value = await signToday()
    await loadYearData()
    ElMessage.success('签到成功')
  } catch (error) {
    ElMessage.error(error.message || '签到失败')
  }
}

const onYearChange = async () => {
  try {
    await loadYearData()
  } catch (error) {
    ElMessage.error(error.message || '加载年度签到数据失败')
  }
}

const loadAll = async () => {
  loading.value = true
  try {
    await Promise.all([loadToday(), loadYearData()])
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    await loadAll()
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

    <div class="section" v-loading="loading">
      <div class="sign-hero-card">
        <div class="sh-title">{{ status.signed ? '今日已签到 ✓' : '今日尚未签到' }}</div>
        <div class="sh-sub">
          连续签到 {{ status.continuousDays || 0 }} 天 · 今日奖励 {{ status.todayPoints || 0 }} 积分
        </div>
        <button class="sh-btn" :disabled="status.signed" @click="doSign">
          {{ status.signed ? '已签到' : '立即签到' }}
        </button>
      </div>
    </div>

    <div class="section" v-loading="loading">
      <div class="section-title-row">
        <h3>{{ selectedYear }} 年签到日历</h3>
        <small>已签到 {{ yearData.signedCount || 0 }} 天</small>
      </div>

      <div class="month-grid-wrap">
        <article class="month-card" v-for="month in monthCards" :key="month.month">
          <div class="month-card-head">
            <strong>{{ month.title }}</strong>
            <small>签到 {{ month.signedCount }} 天</small>
          </div>

          <div class="weekday-row">
            <span class="weekday-item" v-for="weekday in weekdayList" :key="weekday">{{
              weekday
            }}</span>
          </div>

          <div class="month-calendar-grid">
            <div
              v-for="cell in month.cells"
              :key="cell.key"
              class="month-day-cell"
              :class="{ signed: cell.signed, empty: cell.empty }"
              :title="cell.empty ? '' : cell.date"
            >
              <span v-if="!cell.empty">{{ cell.day }}</span>
            </div>
          </div>
        </article>
      </div>

      <div class="hm-legend">
        <span>少</span>
        <div class="hm-l" style="background: var(--gray-200)"></div>
        <div class="hm-l" style="background: #52b788"></div>
        <span>多</span>
      </div>

      <p class="helper-text">连续签到：{{ yearData.continuousDays || 0 }} 天</p>
    </div>
  </div>
</template>
