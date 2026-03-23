<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { getMyRank, getRankList } from '@/api/rank'

const activeTab = ref('points')
const rank = ref({ list: [], myRank: null })
const myRankData = ref(null)
const campusId = ref(null)

// 排行榜按全局展示，不再限制校区条件。
const safeCampusId = computed(() => (campusId.value ? Number(campusId.value) : undefined))

const load = async () => {
  rank.value = await getRankList(activeTab.value, safeCampusId.value, 10)
}

// /api/user/rank/mine 已接入；若后端未返回对应字段，前端按占位文案降级展示。
const loadMyRank = async () => {
  try {
    myRankData.value = await getMyRank(safeCampusId.value)
  } catch {
    myRankData.value = null
  }
}

const onTabChange = async () => {
  try {
    await Promise.all([load(), loadMyRank()])
  } catch (error) {
    ElMessage.error(error.message || '加载排行榜失败')
  }
}

const barWidth = (score) => {
  const top = rank.value.list?.[0]?.score || 1
  return `${Math.max(10, Math.round((Number(score || 0) / top) * 100))}%`
}

const rankLabel = (row) => {
  if (activeTab.value === 'campus') {
    return row.campusName || `校区#${row.campusId || '-'}`
  }
  return row.nickname || `用户#${row.userId || '-'}`
}

const myRankText = () => {
  if (activeTab.value === 'campus') return '校区榜不显示个人名次'

  const source = myRankData.value?.[activeTab.value] || rank.value.myRank
  if (!source || !source.rank) return '第 1 名 · 0 分'

  return `第 ${source.rank} 名 · ${source.score} 分`
}

onMounted(async () => {
  try {
    // 保留校区接口调用兼容旧逻辑，但默认不附带校区筛选。
    await getCampusList().catch(() => [])

    await Promise.all([load(), loadMyRank()])
  } catch (error) {
    ElMessage.error(error.message || '加载排行榜失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar"><h1>排行榜</h1></div>

    <div class="section">
      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane label="积分榜" name="points" />
        <el-tab-pane label="捐赠榜" name="donate" />
        <el-tab-pane label="校区榜" name="campus" />
      </el-tabs>

      <div class="my-rank-banner">我的排名：{{ myRankText() }}</div>

      <div
        class="rank-row"
        v-for="row in rank.list"
        :key="`${activeTab}-${row.userId || row.campusId}`"
      >
        <div class="rank-name">{{ row.rank }}. {{ rankLabel(row) }}</div>
        <div class="rank-bar-track">
          <div class="rank-bar-fill" :style="{ width: barWidth(row.score) }"></div>
        </div>
        <strong class="rank-score">{{ row.score }}</strong>
      </div>

      <el-empty v-if="rank.list?.length === 0" description="暂无排行榜数据" />
    </div>
  </div>
</template>
