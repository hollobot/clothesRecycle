<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCampusList } from '@/api/campus'
import {
  changeAdminAccountStatus,
  createAdminAccount,
  deleteAdminAccount,
  getAdminAccountDetail,
  getAdminAccounts,
  resetAdminPassword,
  updateAdminAccount,
} from '@/api/adminAccount'

const loading = ref(false)
const list = ref([])
const campuses = ref([])

const filters = reactive({
  keyword: '',
  status: '',
  campusId: '',
})

const formVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const detailData = ref(null)

const formRef = ref()
const formData = reactive({
  phone: '',
  password: '',
  name: '',
  role: 'CAMPUS_ADMIN',
  campusId: '',
})

const roleOptions = [
  { label: '校区管理员', value: 'CAMPUS_ADMIN' },
  { label: '超级管理员', value: 'SUPER_ADMIN' },
]

const campusMap = computed(() => {
  const map = new Map()
  campuses.value.forEach((campus) => map.set(campus.id, campus.name))
  return map
})

const rules = computed(() => ({
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: isEdit.value ? [] : [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  campusId:
    formData.role === 'CAMPUS_ADMIN'
      ? [{ required: true, message: '请选择所属校区', trigger: 'change' }]
      : [],
}))

const statusText = (status) => (Number(status) === 1 ? '启用' : '禁用')

const loadCampuses = async () => {
  try {
    const res = await getCampusList()
    campuses.value = Array.isArray(res) ? res : []
  } catch {
    campuses.value = []
  }
}

const loadAdmins = async () => {
  loading.value = true
  try {
    const res = await getAdminAccounts({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      campusId: filters.campusId || undefined,
    })
    list.value = Array.isArray(res) ? res : []
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.status = ''
  filters.campusId = ''
  loadAdmins()
}

const resetForm = () => {
  currentId.value = null
  formData.phone = ''
  formData.password = ''
  formData.name = ''
  formData.role = 'CAMPUS_ADMIN'
  formData.campusId = ''
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  currentId.value = row.id
  formData.phone = row.phone || ''
  formData.password = ''
  formData.name = row.name || ''
  formData.role = row.role || 'CAMPUS_ADMIN'
  formData.campusId = row.campusId || ''
  formVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  submitLoading.value = true
  try {
    const payload = {
      name: formData.name,
      role: formData.role,
      campusId: formData.role === 'SUPER_ADMIN' ? null : Number(formData.campusId),
    }

    if (isEdit.value) {
      await updateAdminAccount(currentId.value, payload)
      ElMessage.success('管理员更新成功')
    } else {
      await createAdminAccount({
        phone: formData.phone,
        password: formData.password,
        ...payload,
      })
      ElMessage.success('管理员创建成功')
    }

    formVisible.value = false
    await loadAdmins()
  } catch (error) {
    // 新建/编辑管理员失败时给出明确提示，避免白屏或无反馈。
    ElMessage.error(error?.message || '保存管理员失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

const openDetailDialog = async (row) => {
  const data = await getAdminAccountDetail(row.id)
  detailData.value = data
  detailVisible.value = true
}

const toggleStatus = async (row) => {
  const enable = Number(row.status) !== 1
  await changeAdminAccountStatus(row.id, enable)
  ElMessage.success(enable ? '管理员已启用' : '管理员已禁用')
  await loadAdmins()
}

const resetPassword = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入新密码', `重置 ${row.name} 的密码`, {
    inputPattern: /^.{6,}$/,
    inputErrorMessage: '密码长度至少 6 位',
  })
  await resetAdminPassword(row.id, value)
  ElMessage.success('密码已重置')
}

const removeAdmin = async (row) => {
  await ElMessageBox.confirm(`确认删除管理员「${row.name}」吗？`, '删除确认', {
    type: 'warning',
  })
  await deleteAdminAccount(row.id)
  ElMessage.success('管理员已删除')
  await loadAdmins()
}

onMounted(async () => {
  await loadCampuses()
  await loadAdmins()
})
</script>

<template>
  <section class="page-stack">
    <el-card shadow="never">
      <el-form :inline="true" class="filter-form">
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索姓名/手机号"
            clearable
            @keyup.enter="loadAdmins"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.campusId" placeholder="校区" clearable style="width: 140px">
            <el-option
              v-for="campus in campuses"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAdmins">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="openCreateDialog">新增管理员</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column label="角色" min-width="120">
          <template #default="{ row }">
            {{ row.role === 'SUPER_ADMIN' ? '超级管理员' : '校区管理员' }}
          </template>
        </el-table-column>
        <el-table-column label="所属校区" min-width="120">
          <template #default="{ row }">
            {{
              row.role === 'SUPER_ADMIN' ? '-' : campusMap.get(row.campusId) || `#${row.campusId}`
            }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'">{{
              statusText(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{
            row.createTime ? row.createTime.replace('T', ' ') : '-'
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetailDialog(row)">详情</el-button>
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="resetPassword(row)">重置密码</el-button>
            <el-button
              link
              :type="Number(row.status) === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ Number(row.status) === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="removeAdmin(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '编辑管理员' : '新增管理员'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" style="width: 100%">
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属校区" prop="campusId" v-if="formData.role === 'CAMPUS_ADMIN'">
          <el-select v-model="formData.campusId" placeholder="请选择校区" style="width: 100%">
            <el-option
              v-for="campus in campuses"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="管理员详情" width="500px" destroy-on-close>
      <el-descriptions :column="1" border v-if="detailData">
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          {{ detailData.role === 'SUPER_ADMIN' ? '超级管理员' : '校区管理员' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属校区">
          {{
            detailData.role === 'SUPER_ADMIN'
              ? '-'
              : campusMap.get(detailData.campusId) || `#${detailData.campusId}`
          }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">{{
          statusText(detailData.status)
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ detailData.createTime ? detailData.createTime.replace('T', ' ') : '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<style scoped>
.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}
</style>
