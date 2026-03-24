<script setup>
import { computed, onMounted, ref } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getGiftList } from '@/api/giftAdmin'
import { getAdminItems } from '@/api/itemAdmin'
import { getDropPoints } from '@/api/dropPoint'
import { useAdminStore } from '@/stores/admin'

use([CanvasRenderer, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const loading = ref(false)
/**
 * 当前登录管理员信息。
 */
const adminStore = useAdminStore()
/**
 * 是否超级管理员。
 */
const isSuperAdmin = computed(() => adminStore.isSuperAdmin)
const items = ref([])
const gifts = ref([])
const dropPoints = ref([])

const safeArray = (value) => (Array.isArray(value) ? value : [])

const loadData = async () => {
  loading.value = true
  try {
    const requestTasks = [getAdminItems(), getDropPoints()]
    // 校区管理员无礼品管理权限，避免调用礼品接口产生无效请求。
    if (isSuperAdmin.value) {
      requestTasks.push(getGiftList())
    }

    const [itemRes, pointRes, giftRes] = await Promise.allSettled(requestTasks)

    items.value = itemRes.status === 'fulfilled' ? safeArray(itemRes.value) : []
    dropPoints.value = pointRes.status === 'fulfilled' ? safeArray(pointRes.value) : []
    gifts.value =
      isSuperAdmin.value && giftRes && giftRes.status === 'fulfilled' ? safeArray(giftRes.value) : []
  } finally {
    loading.value = false
  }
}

const pendingItems = computed(
  () =>
    items.value.filter((item) =>
      // 兼容历史状态值，优先匹配后端当前状态：PENDING_AUDIT。
      ['PENDING_AUDIT', 'PENDING', 'AUDITING'].includes(String(item.status || '').toUpperCase()),
    ).length,
)

const approvedItems = computed(
  () =>
    items.value.filter((item) =>
      // 兼容历史字段 APPROVED，当前后端审核通过后状态为 ON_SHELF。
      ['APPROVED', 'ON_SHELF'].includes(String(item.status || '').toUpperCase()),
    ).length,
)

const monthlyGiftExchanged = computed(() =>
  gifts.value.reduce((sum, gift) => sum + Number(gift.exchangedCount || 0), 0),
)

/**
 * 校区管理员看板展示项：当前可管理回收点总数。
 */
const managedDropPointCount = computed(() => dropPoints.value.length)

const sevenDayStats = computed(() => {
  const now = new Date()
  const labels = []
  const values = []

  for (let i = 6; i >= 0; i -= 1) {
    const date = new Date(now)
    date.setHours(0, 0, 0, 0)
    date.setDate(now.getDate() - i)
    const key = `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`

    labels.push(i === 0 ? '今天' : `${date.getMonth() + 1}/${date.getDate()}`)

    const count = items.value.filter((item) => {
      if (!item.createTime) return false
      const createDate = new Date(item.createTime)
      if (Number.isNaN(createDate.getTime())) return false
      createDate.setHours(0, 0, 0, 0)
      return `${createDate.getFullYear()}-${createDate.getMonth()}-${createDate.getDate()}` === key
    }).length

    values.push(count)
  }

  return { labels, values }
})

const categoryStats = computed(() => {
  const map = new Map()
  items.value.forEach((item) => {
    const category = item.category || '未分类'
    map.set(category, (map.get(category) || 0) + 1)
  })

  return Array.from(map.entries()).map(([name, value]) => ({ name, value }))
})

const dropPointStats = computed(() => {
  const enabled = dropPoints.value.filter((it) => Number(it.status) === 1).length
  const disabled = dropPoints.value.length - enabled
  return [
    { name: '启用中', value: enabled },
    { name: '已禁用', value: disabled },
  ]
})

const itemTrendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 24, right: 14, top: 20, bottom: 24 },
  xAxis: {
    type: 'category',
    data: sevenDayStats.value.labels,
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#d0d0d0' } },
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#f0f0f0' } },
  },
  series: [
    {
      type: 'bar',
      data: sevenDayStats.value.values,
      barWidth: 18,
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: '#2e7d52',
      },
    },
  ],
}))

const categoryOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', right: 0, top: 'middle' },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['32%', '50%'],
      data: categoryStats.value,
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 2,
      },
      label: { show: false },
      emphasis: { label: { show: true, formatter: '{b}: {d}%' } },
    },
  ],
  color: ['#2e7d52', '#4caf7d', '#d4870a', '#1565c0', '#94a3b8'],
}))

const dropPointOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [
    {
      type: 'pie',
      radius: ['40%', '66%'],
      data: dropPointStats.value,
      label: { formatter: '{b}\n{c}' },
    },
  ],
  color: ['#2e7d52', '#c0392b'],
}))

onMounted(loadData)
</script>

<template>
  <section class="page-stack" v-loading="loading">
    <el-card shadow="never">
      <template #header>
        <div class="card-head">
          <span>数据看板</span>
          <el-button @click="loadData">刷新</el-button>
        </div>
      </template>

      <div class="metrics-row">
        <article class="metric-card">
          <div class="mc-label">物品总数</div>
          <div class="mc-value">{{ items.length }}</div>
          <div class="mc-diff diff-up">实时统计</div>
        </article>
        <article class="metric-card">
          <div class="mc-label">待审核物品</div>
          <div class="mc-value text-amber">{{ pendingItems }}</div>
          <div class="mc-diff diff-warn">需及时处理</div>
        </article>
        <article class="metric-card">
          <div class="mc-label">已通过物品</div>
          <div class="mc-value">{{ approvedItems }}</div>
          <div class="mc-diff diff-up">可公开展示</div>
        </article>
        <article class="metric-card">
          <template v-if="isSuperAdmin">
            <div class="mc-label">礼品兑换总数</div>
            <div class="mc-value">{{ monthlyGiftExchanged }}</div>
            <div class="mc-diff diff-up">累计核销</div>
          </template>
          <template v-else>
            <div class="mc-label">回收点总数</div>
            <div class="mc-value">{{ managedDropPointCount }}</div>
            <div class="mc-diff diff-up">当前校区</div>
          </template>
        </article>
      </div>
    </el-card>

    <div class="charts-row">
      <el-card shadow="never">
        <template #header><span>近 7 天新增物品</span></template>
        <VChart class="chart-box" :option="itemTrendOption" autoresize />
      </el-card>
      <el-card shadow="never">
        <template #header><span>分类占比</span></template>
        <VChart class="chart-box" :option="categoryOption" autoresize />
      </el-card>
    </div>

    <el-card shadow="never">
      <template #header><span>回收点运行状态</span></template>
      <VChart class="chart-box chart-small" :option="dropPointOption" autoresize />
    </el-card>
  </section>
</template>

<style scoped>
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.chart-small {
  height: 260px;
}
</style>
