<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCampusOptions } from '@/api/campus'
import {
  changeAdminUserStatus,
  createAdminUser,
  deleteAdminUser,
  getAdminUserDetail,
  getAdminUsers,
  updateAdminUser,
} from '@/api/adminUser'
import { useAdminStore } from '@/stores/admin'

const loading = ref(false)
const users = ref([])
/**
 * 校区数据源（用于名称展示与超级管理员选择）。
 */
const campuses = ref([])

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
  studentId: '',
  name: '',
  campusId: '',
  avatarUrl: '',
})

const campusMap = computed(() => {
  const map = new Map()
  campuses.value.forEach((campus) => map.set(Number(campus.id), campus.name))
  return map
})

const rules = computed(() => ({
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: isEdit.value ? [] : [{ required: true, message: '请输入密码', trigger: 'blur' }],
  studentId: isEdit.value ? [] : [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择所属校区', trigger: 'change' }],
}))

const statusText = (status) => (Number(status) === 1 ? '正常' : '禁用')

const loadCampuses = async () => {
  try {
    // 用户管理页需要兼容校区管理员，使用公开校区列表接口避免 403。
    const res = await getCampusOptions()
    campuses.value = Array.isArray(res) ? res : []
  } catch {
    campuses.value = []
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const scopedCampusId = resolveScopedCampusId(filters.campusId)
    const res = await getAdminUsers({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      campusId: scopedCampusId,
    })
    users.value = Array.isArray(res) ? res : []

    // 老登录态可能没有 campusId，这里按接口返回数据回填管理员校区默认值。
    if (!isSuperAdmin.value && !getOperatorCampusId() && users.value.length > 0) {
      const fallbackCampusId = Number(users.value[0].campusId) || null
      if (fallbackCampusId) {
        effectiveAdminCampusId.value = fallbackCampusId
        filters.campusId = fallbackCampusId
      }
    }
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.status = ''
  filters.campusId = isSuperAdmin.value ? '' : resolveScopedCampusId(null) || ''
  loadUsers()
}

const resetForm = () => {
  currentId.value = null
  formData.phone = ''
  formData.password = ''
  formData.studentId = ''
  formData.name = ''
  formData.campusId = isSuperAdmin.value ? '' : resolveScopedCampusId(filters.campusId) || ''
  formData.avatarUrl = ''
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

const openEditDialog = (row) => {
  // 兼容老登录态缺失 campusId：打开编辑时用当前行回填管理员校区默认值。
  if (!isSuperAdmin.value && !getOperatorCampusId() && row?.campusId) {
    effectiveAdminCampusId.value = Number(row.campusId) || null
  }
  isEdit.value = true
  currentId.value = row.id
  formData.phone = row.phone || ''
  formData.password = ''
  formData.studentId = row.studentId || ''
  formData.name = row.name || ''
  // 编辑场景仍显示数据原校区；校区管理员字段只读不可改。
  formData.campusId = Number(row.campusId) || ''
  formData.avatarUrl = row.avatarUrl || ''
  formVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateAdminUser(currentId.value, {
        name: formData.name,
        campusId: resolveScopedCampusId(formData.campusId),
        avatarUrl: formData.avatarUrl,
      })
      ElMessage.success('用户更新成功')
    } else {
      await createAdminUser({
        phone: formData.phone,
        password: formData.password,
        studentId: formData.studentId,
        name: formData.name,
        campusId: resolveScopedCampusId(formData.campusId),
        avatarUrl: formData.avatarUrl,
      })
      ElMessage.success('用户创建成功')
    }

    formVisible.value = false
    await loadUsers()
  } finally {
    submitLoading.value = false
  }
}

const openDetailDialog = async (row) => {
  const data = await getAdminUserDetail(row.id)
  detailData.value = data
  detailVisible.value = true
}

const toggleStatus = async (row) => {
  const enable = Number(row.status) !== 1
  await changeAdminUserStatus(row.id, enable)
  ElMessage.success(enable ? '用户已启用' : '用户已禁用')
  await loadUsers()
}

const removeUser = async (row) => {
  await ElMessageBox.confirm(`确认删除用户「${row.name}」吗？`, '删除确认', {
    type: 'warning',
  })
  await deleteAdminUser(row.id)
  ElMessage.success('用户已删除')
  await loadUsers()
}

onMounted(async () => {
  await loadCampuses()
  // 校区管理员默认锁定为本人校区，筛选框不可切换。
  if (!isSuperAdmin.value) {
    filters.campusId = resolveScopedCampusId(null) || ''
  }
  await loadUsers()
})
</script>

<template>
  <section class="page-stack">
    <el-card shadow="never">
      <el-form :inline="true" class="filter-form">
        <el-form-item>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索姓名/学号/手机号"
            clearable
            @keyup.enter="loadUsers"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="filters.campusId"
            placeholder="校区"
            :clearable="isSuperAdmin"
            :disabled="!isSuperAdmin"
            style="width: 140px"
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
          <el-button type="primary" @click="loadUsers">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="openCreateDialog">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="users" v-loading="loading" border>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="studentId" label="学号" min-width="130" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column label="校区" min-width="120">
          <template #default="{ row }">{{
            campusMap.get(Number(row.campusId)) || getCampusName(row.campusId)
          }}</template>
        </el-table-column>
        <el-table-column prop="pointBalance" label="积分" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'">{{
              statusText(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" min-width="170">
          <template #default="{ row }">{{
            row.createTime ? row.createTime.replace('T', ' ') : '-'
          }}</template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetailDialog(row)">详情</el-button>
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              link
              :type="Number(row.status) === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ Number(row.status) === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="removeUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="学号" prop="studentId" v-if="!isEdit">
          <el-input v-model="formData.studentId" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="校区" prop="campusId">
          <el-select
            v-model="formData.campusId"
            placeholder="请选择校区"
            :disabled="!isSuperAdmin"
            style="width: 100%"
          >
            <el-option
              v-for="campus in campusOptions"
              :key="campus.id"
              :label="campus.name"
              :value="Number(campus.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="头像地址" prop="avatarUrl">
          <el-input v-model="formData.avatarUrl" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="用户详情" width="520px" destroy-on-close>
      <el-descriptions :column="1" border v-if="detailData">
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ detailData.studentId }}</el-descriptions-item>
        <el-descriptions-item label="校区">
          {{ campusMap.get(Number(detailData.campusId)) || getCampusName(detailData.campusId) }}
        </el-descriptions-item>
        <el-descriptions-item label="积分">{{ detailData.pointBalance ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="冻结积分">{{
          detailData.frozenPoint ?? 0
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{
          statusText(detailData.status)
        }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">
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
