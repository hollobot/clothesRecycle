<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { getItemList } from '@/api/item'

const list = ref([])
const campuses = ref([])
const activeCampusId = ref('')
const activeCategory = ref('全部')
const searchKeyword = ref('')
const appliedKeyword = ref('')

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

const thumbClassPool = ['t-green', 't-amber', 't-blue', 't-gray']

const getStatusText = (status) => statusTextMap[status] || '未知状态'
const getStatusClass = (status) => statusClassMap[status] || 'status-default'
const getThumbClass = (item, index) => {
  // 无封面图时按分类/序号给占位块上色，保持与原型图一致的视觉层次。
  if (item.category === '上装') return 't-green'
  if (item.category === '外套') return 't-amber'
  if (item.category === '下装') return 't-blue'
  return thumbClassPool[index % thumbClassPool.length]
}

const load = async () => {
  const campusId = activeCampusId.value ? Number(activeCampusId.value) : undefined
  const category = activeCategory.value === '全部' ? undefined : activeCategory.value
  const keyword = appliedKeyword.value.trim() || undefined

  // 广场搜索走后端查询，保证关键词与分类筛选结果准确。
  list.value = await getItemList({ campusId, category, keyword })
}

const loadCampus = async () => {
  campuses.value = await getCampusList()
}

const onSearch = () => {
  // 点击搜索按钮后应用关键词，并重新请求后端。
  appliedKeyword.value = searchKeyword.value
  load()
}

const onClearSearch = () => {
  searchKeyword.value = ''
  appliedKeyword.value = ''
  load()
}

/**
 * 切换分类后立即刷新广场数据。
 */
const onSelectCategory = (category) => {
  activeCategory.value = category
  load()
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
    <div class="topbar topbar-stack">
      <div class="topbar-main">
        <h1>衣物广场</h1>
        <div class="topbar-tools">
          <el-select
            v-model="activeCampusId"
            placeholder="全部校区"
            class="compact-select plaza-campus-select"
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
      </div>

      <div class="search-bar plaza-search-bar">
        <span class="search-circle"></span>
        <input
          v-model="searchKeyword"
          class="plaza-search-input"
          placeholder="搜索衣物品类、尺码、关键词"
          @keyup.enter="onSearch"
        />
        <button class="plaza-search-btn" @click="onSearch">搜索</button>
        <button class="plaza-search-btn ghost" v-if="appliedKeyword" @click="onClearSearch">
          清空
        </button>
      </div>
    </div>

    <div class="chips-wrap">
      <button
        v-for="category in categoryList"
        :key="category"
        class="chip"
        :class="{ active: activeCategory === category }"
        @click="onSelectCategory(category)"
      >
        {{ category }}
      </button>
    </div>

    <div class="grid-2" v-if="list.length">
      <article
        class="item-card"
        v-for="(item, index) in list"
        :key="item.id"
        @click="$router.push(`/item/${item.id}`)"
      >
        <div class="item-thumb" :class="getThumbClass(item, index)">
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
      <p class="helper-text">暂无符合条件的物品，请调整校区、分类或关键词</p>
    </div>
  </div>
</template>
