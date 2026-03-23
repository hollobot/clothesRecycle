<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { getItemList } from '@/api/item'

const list = ref([])
const campuses = ref([])
const activeCampusId = ref('')
const activeCategory = ref('全部')

const categoryList = ['全部', '上装', '下装', '外套', '鞋帽', '配饰']

const statusTextMap = {
  ON_SHELF: '可认领',
  TRADING: '交易中',
  DONE: '已完成',
  PENDING_AUDIT: '待审核',
  REJECTED: '已拒绝',
}

const statusClassMap = {
  ON_SHELF: 'status-on-shelf',
  TRADING: 'status-trading',
  DONE: 'status-done',
}

const getStatusText = (status) => statusTextMap[status] || status || '-'
const getStatusClass = (status) => statusClassMap[status] || 'status-default'

const filteredList = computed(() => {
  if (activeCategory.value === '全部') return list.value
  return list.value.filter((item) => item.category === activeCategory.value)
})

const load = async () => {
  const campusId = activeCampusId.value ? Number(activeCampusId.value) : undefined
  list.value = await getItemList(campusId)
}

const loadCampus = async () => {
  campuses.value = await getCampusList()
}

onMounted(async () => {
  try {
    await Promise.all([loadCampus(), load()])
  } catch (error) {
    ElMessage.error(error.message || '加载物品列表失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>衣物广场</h1>
      <el-select
        v-model="activeCampusId"
        placeholder="全部校区"
        class="compact-select"
        @change="load"
      >
        <el-option label="全部校区" value="" />
        <el-option
          v-for="campus in campuses"
          :key="campus.id"
          :label="campus.name"
          :value="String(campus.id)"
        />
      </el-select>
    </div>

    <div class="search-bar">
      <span class="search-circle"></span>
      <span class="search-text">搜索衣物品类、尺码、关键词（演示）</span>
    </div>

    <div class="chips-wrap">
      <button
        v-for="category in categoryList"
        :key="category"
        class="chip"
        :class="{ active: activeCategory === category }"
        @click="activeCategory = category"
      >
        {{ category }}
      </button>
    </div>

    <div class="grid-2" v-if="filteredList.length">
      <article
        class="item-card"
        v-for="item in filteredList"
        :key="item.id"
        @click="$router.push(`/item/${item.id}`)"
      >
        <div class="item-thumb">
          <img v-if="item.coverUrl" :src="item.coverUrl" alt="cover" class="item-thumb-img" />
          <span v-else>封面图</span>
        </div>
        <div class="item-info">
          <div class="item-name-row">
            <strong class="item-name">{{ item.title }}</strong>
            <span class="item-status" :class="getStatusClass(item.status)">{{
              getStatusText(item.status)
            }}</span>
          </div>
          <p class="item-meta">
            {{ item.category }} / {{ item.sizeType }} / {{ item.conditionLevel }}
          </p>
          <div class="item-tag" :class="item.acquireType === 'POINT' ? 'tag-pts' : 'tag-free'">
            {{ item.acquireType === 'POINT' ? `${item.pointPrice || 0} 积分` : '免费领取' }}
          </div>
        </div>
      </article>
    </div>

    <div class="section" v-else>
      <p class="helper-text">暂无符合条件的物品</p>
    </div>
  </div>
</template>
