<script setup>
import { computed, onMounted, ref } from 'vue'
import { auditItem, forceOffShelf, getAdminItems } from '@/api/itemAdmin'

const list = ref([])
const loading = ref(false)
const loadError = ref('')
const keyword = ref('')
const status = ref('all')
const activeId = ref(null)

const statusOptions = [
  { value: 'all', label: '全部状态' },
  { value: 'PENDING', label: '待审核' },
  { value: 'APPROVED', label: '已通过' },
  { value: 'REJECTED', label: '已拒绝' },
  { value: 'OFF_SHELF', label: '已下架' },
]

const statusClassMap = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-ok',
  REJECTED: 'badge-banned',
  OFF_SHELF: 'badge-banned',
}

const statusLabelMap = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  OFF_SHELF: '已下架',
}

const getStatusClass = (value) => statusClassMap[value] || 'badge-normal'
const getStatusLabel = (value) => statusLabelMap[value] || value || '未知'

const load = async () => {
  loading.value = true
  loadError.value = ''

  try {
    const queryStatus = status.value === 'all' ? undefined : status.value
    const data = await getAdminItems(queryStatus)
    list.value = Array.isArray(data) ? data : []

    if (!activeId.value || !list.value.some((item) => item.id === activeId.value)) {
      activeId.value = list.value[0]?.id || null
    }
  } catch (error) {
    loadError.value = error?.message || '物品列表加载失败'
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  if (!key) return list.value

  return list.value.filter((item) =>
    [item.title, item.category, item.description].some((it) =>
      String(it || '')
        .toLowerCase()
        .includes(key),
    ),
  )
})

const activeItem = computed(
  () => filteredList.value.find((item) => item.id === activeId.value) || null,
)

const selectItem = (itemId) => {
  activeId.value = itemId
}

const runAudit = async (approved) => {
  if (!activeItem.value) return

  let reason = ''
  if (!approved) {
    reason = window.prompt('请输入拒绝原因', '图片与描述不一致') || ''
    if (!reason.trim()) return
  } else {
    reason = '审核通过'
  }

  try {
    await auditItem(activeItem.value.id, approved, reason)
    await load()
  } catch (error) {
    window.alert(error?.message || '审核失败，请稍后重试')
  }
}

const runForceOffShelf = async () => {
  if (!activeItem.value) return

  const reason = window.prompt('请输入强制下架原因', '管理员巡检下架') || ''
  if (!reason.trim()) return

  try {
    await forceOffShelf(activeItem.value.id, reason)
    await load()
  } catch (error) {
    window.alert(error?.message || '操作失败，请稍后重试')
  }
}

onMounted(load)
</script>

<template>
  <section class="page-stack">
    <div class="search-row">
      <input v-model.trim="keyword" class="s-input" placeholder="搜索物品名称、分类、描述..." />
      <select v-model="status" class="s-select" @change="load">
        <option v-for="option in statusOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>
      <button class="th-btn primary" :disabled="loading" @click="load">查询</button>
    </div>

    <div v-if="loadError" class="state-banner error">{{ loadError }}</div>

    <div class="audit-grid">
      <article class="table-card">
        <table class="data-table" style="width: 100%">
          <thead>
            <tr>
              <th>物品</th>
              <th>分类</th>
              <th>状态</th>
              <th>发布时间</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in filteredList"
              :key="item.id"
              class="row-hover"
              :class="{ selected: item.id === activeId }"
              @click="selectItem(item.id)"
            >
              <td>
                <div class="table-primary">{{ item.title || '-' }}</div>
                <div class="table-secondary">{{ item.acquireType || '未设置获取方式' }}</div>
              </td>
              <td>{{ item.category || '-' }}</td>
              <td>
                <span class="badge" :class="getStatusClass(item.status)">{{
                  getStatusLabel(item.status)
                }}</span>
              </td>
              <td>{{ item.createTime ? item.createTime.replace('T', ' ') : '-' }}</td>
            </tr>
            <tr v-if="!filteredList.length">
              <td colspan="4" class="table-empty">暂无物品</td>
            </tr>
          </tbody>
        </table>
      </article>

      <article class="chart-card">
        <template v-if="activeItem">
          <div class="chart-title">审核详情</div>
          <div class="audit-imgs-grid">
            <div class="aimg t-green">封面预览</div>
            <div class="aimg t-amber">附图预留</div>
          </div>
          <div class="audit-info">
            <div class="ai-row">
              <div class="ai-key">名称</div>
              <div class="ai-val">{{ activeItem.title || '-' }}</div>
            </div>
            <div class="ai-row">
              <div class="ai-key">分类</div>
              <div class="ai-val">{{ activeItem.category || '-' }}</div>
            </div>
            <div class="ai-row">
              <div class="ai-key">尺码</div>
              <div class="ai-val">{{ activeItem.sizeType || '-' }}</div>
            </div>
            <div class="ai-row">
              <div class="ai-key">成色</div>
              <div class="ai-val">{{ activeItem.conditionLevel || '-' }}</div>
            </div>
            <div class="ai-row">
              <div class="ai-key">获取方式</div>
              <div class="ai-val">{{ activeItem.acquireType || '-' }}</div>
            </div>
            <div class="ai-row">
              <div class="ai-key">描述</div>
              <div class="ai-val">{{ activeItem.description || '-' }}</div>
            </div>
            <div class="ai-row" v-if="activeItem.auditReason">
              <div class="ai-key">审核备注</div>
              <div class="ai-val">{{ activeItem.auditReason }}</div>
            </div>
          </div>
          <div class="audit-btns">
            <button class="abtn-pass" @click="runAudit(true)">通过</button>
            <button class="abtn-fail" @click="runAudit(false)">拒绝</button>
          </div>
          <button class="th-btn" style="margin-top: 8px" @click="runForceOffShelf">强制下架</button>
        </template>
        <div v-else class="table-empty">请先选择一条物品</div>
      </article>
    </div>
  </section>
</template>
