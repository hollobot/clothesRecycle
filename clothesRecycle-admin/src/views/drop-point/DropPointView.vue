<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusOptions } from '@/api/campus'
import {
  changeDropPointStatus,
  createDropPoint,
  getDropPoints,
  updateDropPoint,
} from '@/api/dropPoint'
import { useAdminStore } from '@/stores/admin'

const loading = ref(false)
const saving = ref(false)
/**
 * 校区数据源（用于名称展示与超级管理员选择）。
 */
const campuses = ref([])
const points = ref([])

/**
 * 当前登录管理员信息。
 */
const adminStore = useAdminStore()
/**
 * 是否超级管理员。
 */
const isSuperAdmin = computed(() => adminStore.isSuperAdmin)
/**
 * 当前管理员所属校区 ID（校区管理员必填，超级管理员可能为空）。
 */
const adminCampusId = computed(() => Number(adminStore.profile?.campusId) || null)
/**
 * 校区管理员有效校区 ID（优先登录态，缺失时根据已加载数据回填）。
 */
const effectiveAdminCampusId = ref(adminCampusId.value)

/**
 * 获取当前操作人有效校区 ID。
 */
const getOperatorCampusId = () => adminCampusId.value || effectiveAdminCampusId.value || null
/**
 * 查询与表单可用校区选项：校区管理员仅保留自己校区。
 */
const campusOptions = computed(() => {
  if (isSuperAdmin.value) {
    return campuses.value
  }
  const operatorCampusId = getOperatorCampusId()
  if (!operatorCampusId) {
    return []
  }
  return campuses.value.filter((campus) => Number(campus.id) === operatorCampusId)
})

const resolveScopedCampusId = (campusId) => {
  if (isSuperAdmin.value) {
    return campusId ? Number(campusId) : undefined
  }
  return getOperatorCampusId() || undefined
}

/**
 * 根据校区 ID 显示校区名称，避免类型不一致时回退成数字。
 */
const getCampusName = (campusId) => {
  const matched = campuses.value.find((campus) => Number(campus.id) === Number(campusId))
  return matched ? matched.name : `#${campusId}`
}

const filters = reactive({
  campusId: '',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const formRef = ref()

const formData = reactive({
  campusId: '',
  name: '',
  locationDesc: '',
  openTime: '',
  managerNote: '',
})

const rules = {
  campusId: [{ required: true, message: '请选择所属校区', trigger: 'change' }],
  name: [{ required: true, message: '请输入回收点名称', trigger: 'blur' }],
  locationDesc: [{ required: true, message: '请输入位置描述', trigger: 'blur' }],
  openTime: [{ required: true, message: '请输入开放时间', trigger: 'blur' }],
}

const loadCampuses = async () => {
  // 回收点页对校区管理员开放，需走公开校区接口，避免 /api/super 权限限制。
  const res = await getCampusOptions().catch(() => [])
  campuses.value = Array.isArray(res) ? res : []
}

const loadPoints = async () => {
  loading.value = true
  try {
    const scopedCampusId = resolveScopedCampusId(filters.campusId)
    const res = await getDropPoints(scopedCampusId)
    points.value = Array.isArray(res) ? res : []

    // 老登录态可能没有 campusId，这里按接口返回数据回填管理员校区默认值。
    if (!isSuperAdmin.value && !getOperatorCampusId() && points.value.length > 0) {
      const fallbackCampusId = Number(points.value[0].campusId) || null
      if (fallbackCampusId) {
        effectiveAdminCampusId.value = fallbackCampusId
        filters.campusId = fallbackCampusId
      }
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  currentId.value = null
  formData.campusId = isSuperAdmin.value ? '' : resolveScopedCampusId(filters.campusId) || ''
  formData.name = ''
  formData.locationDesc = ''
  formData.openTime = ''
  formData.managerNote = ''
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  // 兼容老登录态缺失 campusId：打开编辑时用当前行回填管理员校区默认值。
  if (!isSuperAdmin.value && !getOperatorCampusId() && row?.campusId) {
    effectiveAdminCampusId.value = Number(row.campusId) || null
  }
  isEdit.value = true
  currentId.value = row.id
  // 编辑场景仍显示数据原校区；校区管理员字段只读不可改。
  formData.campusId = Number(row.campusId) || ''
  formData.name = row.name || ''
  formData.locationDesc = row.locationDesc || ''
  formData.openTime = row.openTime || ''
  formData.managerNote = row.managerNote || ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  saving.value = true
  try {
    const payload = {
      campusId: resolveScopedCampusId(formData.campusId),
      name: formData.name,
      locationDesc: formData.locationDesc,
      openTime: formData.openTime,
      managerNote: formData.managerNote,
    }

    if (isEdit.value) {
      await updateDropPoint(currentId.value, payload)
      ElMessage.success('回收点更新成功')
    } else {
      await createDropPoint(payload)
      ElMessage.success('回收点创建成功')
    }

    dialogVisible.value = false
    await loadPoints()
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  const enable = Number(row.status) !== 1
  await changeDropPointStatus(row.id, enable)
  ElMessage.success(enable ? '回收点已启用' : '回收点已禁用')
  await loadPoints()
}

onMounted(async () => {
  await loadCampuses()
  // 校区管理员默认锁定为本人校区，筛选框不可切换。
  if (!isSuperAdmin.value) {
    filters.campusId = resolveScopedCampusId(null) || ''
  }
  await loadPoints()
})
</script>

<template>
  <section class="page-stack">
    <el-card shadow="never">
      <el-form :inline="true" class="filter-form">
        <el-form-item>
          <el-select
            v-model="filters.campusId"
            placeholder="校区"
            :clearable="isSuperAdmin"
            :disabled="!isSuperAdmin"
            style="width: 160px"
          >
            <el-option
              v-for="campus in campusOptions"
              :key="campus.id"
              :label="campus.name"
              :value="Number(campus.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPoints">查询</el-button>
          <el-button @click="openCreateDialog">新增回收点</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="points" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column label="所属校区" min-width="130">
          <template #default="{ row }">
            {{ getCampusName(row.campusId) }}
          </template>
        </el-table-column>
        <el-table-column prop="locationDesc" label="位置描述" min-width="180" />
        <el-table-column prop="openTime" label="开放时间" min-width="130" />
        <el-table-column prop="stockCount" label="存量" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'">
              {{ Number(row.status) === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              link
              :type="Number(row.status) === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ Number(row.status) === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑回收点' : '新增回收点'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="96px">
        <el-form-item label="所属校区" prop="campusId">
          <el-select v-model="formData.campusId" :disabled="!isSuperAdmin" style="width: 100%">
            <el-option
              v-for="campus in campusOptions"
              :key="campus.id"
              :label="campus.name"
              :value="Number(campus.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="回收点名称" prop="name">
          <el-input v-model="formData.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="位置描述" prop="locationDesc">
          <el-input v-model="formData.locationDesc" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="formData.openTime" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="负责人备注">
          <el-input v-model="formData.managerNote" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}
</style>
