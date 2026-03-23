<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  changeCampusStatus,
  createCampus,
  deleteCampus,
  getCampusList,
  updateCampus,
} from '@/api/campus'
import { getDropPoints } from '@/api/dropPoint'

const loading = ref(false)
const campuses = ref([])
const dropPoints = ref([])

const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const formRef = ref()

const formData = reactive({
  name: '',
  address: '',
  remark: '',
})

const campusPointCount = computed(() => {
  const map = new Map()
  dropPoints.value.forEach((point) => {
    map.set(point.campusId, (map.get(point.campusId) || 0) + 1)
  })
  return map
})

const chartRows = computed(() => {
  const rows = campuses.value.map((campus) => {
    const value = campusPointCount.value.get(campus.id) || 0
    return { id: campus.id, name: campus.name, value }
  })

  const max = Math.max(...rows.map((row) => row.value), 1)
  return rows.map((row) => ({
    ...row,
    width: `${Math.max((row.value / max) * 100, 5)}%`,
  }))
})

const rules = {
  name: [{ required: true, message: '请输入校区名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入校区地址', trigger: 'blur' }],
}

const resetForm = () => {
  currentId.value = null
  formData.name = ''
  formData.address = ''
  formData.remark = ''
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (campus) => {
  isEdit.value = true
  currentId.value = campus.id
  formData.name = campus.name || ''
  formData.address = campus.address || ''
  formData.remark = campus.remark || ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  submitLoading.value = true
  try {
    const payload = {
      name: formData.name,
      address: formData.address,
      remark: formData.remark,
    }

    if (isEdit.value) {
      await updateCampus(currentId.value, payload)
      ElMessage.success('校区更新成功')
    } else {
      await createCampus(payload)
      ElMessage.success('校区创建成功')
    }

    dialogVisible.value = false
    await load()
  } finally {
    submitLoading.value = false
  }
}

const load = async () => {
  loading.value = true
  try {
    const [campusRes, pointsRes] = await Promise.all([
      getCampusList(),
      getDropPoints().catch(() => []),
    ])
    campuses.value = Array.isArray(campusRes) ? campusRes : []
    dropPoints.value = Array.isArray(pointsRes) ? pointsRes : []
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (campus) => {
  const enable = Number(campus.status) !== 1
  await changeCampusStatus(campus.id, enable)
  ElMessage.success(enable ? '校区已启用' : '校区已禁用')
  await load()
}

const removeCampus = async (campus) => {
  await ElMessageBox.confirm(`确认删除校区「${campus.name}」吗？`, '删除确认', {
    type: 'warning',
  })
  await deleteCampus(campus.id)
  ElMessage.success('校区已删除')
  await load()
}

onMounted(load)
</script>

<template>
  <section class="page-stack">
    <el-card shadow="never">
      <template #header>
        <div class="card-head">
          <span>校区管理</span>
          <div>
            <el-button type="primary" @click="openCreateDialog">新增校区</el-button>
            <el-button @click="load">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="campuses" v-loading="loading" border>
        <el-table-column prop="name" label="校区名称" min-width="130" />
        <el-table-column prop="address" label="校区地址" min-width="180" />
        <el-table-column label="回收点数量" width="120">
          <template #default="{ row }">{{ campusPointCount.get(row.id) || 0 }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'">
              {{ Number(row.status) === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{
            row.createTime ? row.createTime.replace('T', ' ') : '-'
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              link
              :type="Number(row.status) === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ Number(row.status) === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="removeCampus(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never">
      <template #header><span>各校区回收点数量</span></template>
      <div class="hbar-chart">
        <div v-for="row in chartRows" :key="row.id" class="hbar-row">
          <div class="hbar-lbl">{{ row.name }}</div>
          <div class="hbar-track"><div class="hbar-fill" :style="{ width: row.width }"></div></div>
          <div class="hbar-val">{{ row.value }} 个</div>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑校区' : '新增校区'"
      width="540px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="校区名称" prop="name">
          <el-input v-model="formData.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="校区地址" prop="address">
          <el-input v-model="formData.address" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
