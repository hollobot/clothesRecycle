<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRankList } from '@/api/rank'

const activeTab = ref('points')
const rank = ref({ list: [], myRank: null })
const campusId = ref(1)

const load = async () => {
  rank.value = await getRankList(activeTab.value, campusId.value, 50)
}

const onTabChange = async () => {
  try {
    await load()
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
    return row.campusName
  }
  return row.nickname
}

const myRankText = () => {
  if (activeTab.value === 'campus') return '校区榜不显示个人排名'
  if (!rank.value.myRank || !rank.value.myRank.rank) return '暂未上榜'
  return `第 ${rank.value.myRank.rank} 名 · ${rank.value.myRank.score} 分`
}

onMounted(async () => {
  try {
    await load()
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
