<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import {
  changeDropPointStatus,
  createDropPoint,
  getDropPoints,
  updateDropPoint,
} from '@/api/dropPoint'

const loading = ref(false)
const saving = ref(false)
const campuses = ref([])
const points = ref([])

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
  const res = await getCampusList().catch(() => [])
  campuses.value = Array.isArray(res) ? res : []
}

const loadPoints = async () => {
  loading.value = true
  try {
    const res = await getDropPoints(filters.campusId || undefined)
    points.value = Array.isArray(res) ? res : []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  currentId.value = null
  formData.campusId = ''
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
  isEdit.value = true
  currentId.value = row.id
  formData.campusId = row.campusId
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
      campusId: Number(formData.campusId),
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
            placeholder="全部校区"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="campus in campuses"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
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
            {{ campuses.find((it) => it.id === row.campusId)?.name || `#${row.campusId}` }}
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
          <el-select v-model="formData.campusId" style="width: 100%">
            <el-option
              v-for="campus in campuses"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
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
