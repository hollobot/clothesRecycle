<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auditItem, forceOffShelf, getAdminItemDetail, getAdminItems } from '@/api/itemAdmin'

const list = ref([])
const loading = ref(false)
const loadError = ref('')
const keyword = ref('')
const status = ref('all')
const activeId = ref(null)
const activeDetail = ref(null)

const statusOptions = [
  { value: 'all', label: '全部状态' },
  { value: 'PENDING_AUDIT', label: '待审核' },
  { value: 'ON_SHELF', label: '已上架' },
  { value: 'TRADING', label: '交易中' },
  { value: 'DONE', label: '已完成' },
  { value: 'REJECTED', label: '已拒绝' },
  { value: 'FORCE_OFF_SHELF', label: '强制下架' },
]

const statusClassMap = {
  PENDING_AUDIT: 'badge-pending',
  ON_SHELF: 'badge-ok',
  TRADING: 'badge-ok',
  DONE: 'badge-normal',
  REJECTED: 'badge-banned',
  FORCE_OFF_SHELF: 'badge-banned',
}

const statusLabelMap = {
  PENDING_AUDIT: '待审核',
  ON_SHELF: '已上架',
  TRADING: '交易中',
  DONE: '已完成',
  REJECTED: '已拒绝',
  FORCE_OFF_SHELF: '强制下架',
}

const getStatusClass = (value) => statusClassMap[value] || 'badge-normal'
const getStatusLabel = (value) => statusLabelMap[value] || '未知状态'

/**
 * 当前选中记录是否允许审核通过/拒绝。
 */
const canAuditCurrent = computed(() => activeDetail.value?.status === 'PENDING_AUDIT')

/**
 * 当前选中记录是否允许强制下架。
 */
const canForceOffShelfCurrent = computed(() =>
  ['ON_SHELF', 'TRADING'].includes(String(activeDetail.value?.status || '').toUpperCase()),
)

/**
 * 加载当前选中物品详情（含图片）。
 */
const loadActiveDetail = async (itemId) => {
  if (!itemId) {
    activeDetail.value = null
    return
  }
  activeDetail.value = await getAdminItemDetail(itemId)
}

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
    await loadActiveDetail(activeId.value)
  } catch (error) {
    loadError.value = error?.message || '物品列表加载失败'
    activeDetail.value = null
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

const activeItem = computed(() => activeDetail.value)

const selectItem = async (itemId) => {
  activeId.value = itemId
  try {
    await loadActiveDetail(itemId)
  } catch (error) {
    ElMessage.error(error?.message || '加载审核详情失败')
  }
}

const runAudit = async (approved) => {
  if (!activeItem.value || !canAuditCurrent.value) return

  let reason = ''
  if (!approved) {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：图片与描述不一致',
      inputValue: '图片与描述不一致',
      inputPattern: /^.{2,200}$/,
      inputErrorMessage: '拒绝原因长度需为 2-200 个字符',
    }).catch(() => ({ value: '' }))
    reason = value || ''
    if (!reason.trim()) return
  } else {
    reason = '审核通过'
  }

  try {
    await auditItem(activeItem.value.id, approved, reason)
    ElMessage.success(approved ? '审核已通过' : '已拒绝该物品')
    await load()
  } catch (error) {
    ElMessage.error(error?.message || '审核失败，请稍后重试')
  }
}

const runForceOffShelf = async () => {
  if (!activeItem.value || !canForceOffShelfCurrent.value) return

  const { value } = await ElMessageBox.prompt('请输入强制下架原因', '强制下架', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：违规内容',
    inputValue: '管理员巡检下架',
    inputPattern: /^.{2,200}$/,
    inputErrorMessage: '下架原因长度需为 2-200 个字符',
  }).catch(() => ({ value: '' }))
  const reason = value || ''
  if (!reason.trim()) return

  try {
    await forceOffShelf(activeItem.value.id, reason)
    ElMessage.success('已强制下架该物品')
    await load()
  } catch (error) {
    ElMessage.error(error?.message || '操作失败，请稍后重试')
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
          <div class="audit-imgs-grid" v-if="Array.isArray(activeItem.imageUrls) && activeItem.imageUrls.length">
            <el-image
              v-for="(url, index) in activeItem.imageUrls"
              :key="`${activeItem.id}-${index}`"
              :src="url"
              :preview-src-list="activeItem.imageUrls"
              fit="cover"
              class="audit-image"
              preview-teleported
            />
          </div>
          <div class="audit-imgs-grid" v-else>
            <div class="aimg t-green">暂无图片</div>
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
            <button class="abtn-pass" :disabled="!canAuditCurrent" @click="runAudit(true)">通过</button>
            <button class="abtn-fail" :disabled="!canAuditCurrent" @click="runAudit(false)">拒绝</button>
          </div>
          <button
            class="th-btn"
            style="margin-top: 8px"
            :disabled="!canForceOffShelfCurrent"
            @click="runForceOffShelf"
          >
            强制下架
          </button>
        </template>
        <div v-else class="table-empty">请先选择一条物品</div>
      </article>
    </div>
  </section>
</template>

<style scoped>
/* 审核详情图片采用响应式网格：大屏多列，小屏自动降列。 */
.audit-imgs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.audit-image {
  width: 100%;
  height: 96px;
  border-radius: 6px;
  border: 1px solid var(--gray-200);
}

@media (max-width: 768px) {
  .audit-imgs-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .audit-image {
    height: 88px;
  }
}

@media (max-width: 480px) {
  .audit-imgs-grid {
    grid-template-columns: 1fr;
  }

  .audit-image {
    height: 180px;
  }
}
</style>
