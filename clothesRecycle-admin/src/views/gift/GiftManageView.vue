<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  changeGiftStatus,
  createGift,
  getGiftExchanges,
  getGiftList,
  uploadGiftImages,
  updateGift,
  verifyGiftExchange,
} from '@/api/giftAdmin'

const tab = ref('gift')
const loading = ref(false)
const gifts = ref([])
const exchanges = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const saving = ref(false)
/**
 * 图片上传中状态。
 */
const uploadLoading = ref(false)
const formRef = ref()

const filters = reactive({
  exchangeCode: '',
})

const formData = reactive({
  name: '',
  description: '',
  imageUrl: '',
  pointCost: 10,
  stock: 10,
})

/**
 * 规范化礼品图片地址。
 *
 * @param {string} url 图片地址
 */
const normalizeImageUrl = (url) => {
  const value = String(url || '').trim()
  return value || ''
}

const rules = {
  name: [{ required: true, message: '请输入礼品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入礼品描述', trigger: 'blur' }],
  pointCost: [{ required: true, message: '请输入所需积分', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
}

const giftMap = computed(() => {
  const map = new Map()
  gifts.value.forEach((gift) => map.set(gift.id, gift.name))
  return map
})

const exchangeList = computed(() => {
  if (!filters.exchangeCode) return exchanges.value
  return exchanges.value.filter((it) =>
    String(it.exchangeCode || '').includes(filters.exchangeCode),
  )
})

const load = async () => {
  loading.value = true
  try {
    const [giftRes, exchangeRes] = await Promise.all([getGiftList(), getGiftExchanges()])
    gifts.value = Array.isArray(giftRes) ? giftRes : []
    exchanges.value = Array.isArray(exchangeRes) ? exchangeRes : []
  } finally {
    loading.value = false
  }
}

/**
 * 上传礼品图片（单图），成功后自动回填表单图片地址。
 *
 * @param {import('element-plus').UploadFile} uploadFile 上传文件对象
 */
const uploadGiftImage = async (uploadFile) => {
  const raw = uploadFile?.raw
  if (!raw) {
    return
  }
  if (!String(raw.type || '').startsWith('image/')) {
    ElMessage.warning('仅支持图片格式文件')
    return
  }
  if (raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('单张图片大小不能超过 5MB')
    return
  }

  uploadLoading.value = true
  try {
    const urls = await uploadGiftImages([raw])
    formData.imageUrl = normalizeImageUrl(Array.isArray(urls) ? urls[0] : '')
    ElMessage.success('礼品图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '礼品图片上传失败')
  } finally {
    uploadLoading.value = false
  }
}

const resetForm = () => {
  currentId.value = null
  formData.name = ''
  formData.description = ''
  formData.imageUrl = ''
  formData.pointCost = 10
  formData.stock = 10
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (gift) => {
  isEdit.value = true
  currentId.value = gift.id
  formData.name = gift.name || ''
  formData.description = gift.description || ''
  formData.imageUrl = normalizeImageUrl(gift.imageUrl)
  formData.pointCost = Number(gift.pointCost || 1)
  formData.stock = Number(gift.stock || 0)
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  try {
    const actionText = isEdit.value ? '更新礼品信息' : '创建礼品'
    await ElMessageBox.confirm(`确认${actionText}吗？`, '操作确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })

    saving.value = true
    const payload = {
      name: formData.name,
      description: formData.description,
      imageUrl: normalizeImageUrl(formData.imageUrl),
      pointCost: Number(formData.pointCost),
      stock: Number(formData.stock),
    }

    if (isEdit.value) {
      await updateGift(currentId.value, payload)
      ElMessage.success('礼品更新成功')
    } else {
      await createGift(payload)
      ElMessage.success('礼品创建成功')
    }

    dialogVisible.value = false
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '礼品保存失败')
    }
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (gift) => {
  const enable = Number(gift.status) !== 1
  try {
    await ElMessageBox.confirm(
      `确认将礼品「${gift.name || '#'}」${enable ? '上架' : '下架'}吗？`,
      '状态变更确认',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
    await changeGiftStatus(gift.id, enable)
    ElMessage.success(enable ? '礼品已上架' : '礼品已下架')
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '礼品状态更新失败')
    }
  }
}

const verifyExchange = async (exchangeId) => {
  try {
    await ElMessageBox.confirm('确认核销该兑换记录吗？', '核销确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await verifyGiftExchange(exchangeId)
    ElMessage.success('兑换记录已核销')
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '核销失败')
    }
  }
}

onMounted(load)
</script>

<template>
  <section class="page-stack">
    <el-card shadow="never">
      <el-segmented
        v-model="tab"
        :options="[
          { label: '礼品管理', value: 'gift' },
          { label: '兑换记录', value: 'exchange' },
        ]"
      />
    </el-card>

    <template v-if="tab === 'gift'">
      <el-card shadow="never">
        <div class="card-head">
          <span>礼品列表</span>
          <div>
            <el-button type="primary" @click="openCreateDialog">新增礼品</el-button>
            <el-button @click="load">刷新</el-button>
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <el-table :data="gifts" v-loading="loading" border>
          <el-table-column label="礼品图片" width="110">
            <template #default="{ row }">
              <el-image
                v-if="normalizeImageUrl(row.imageUrl)"
                :src="normalizeImageUrl(row.imageUrl)"
                fit="cover"
                class="gift-cover-table"
                :preview-src-list="[normalizeImageUrl(row.imageUrl)]"
                preview-teleported
              />
              <div v-else class="gift-cover-table gift-cover-empty">暂无图片</div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="礼品" min-width="160">
            <template #default="{ row }">
              <div>{{ row.name }}</div>
              <div class="table-secondary">{{ row.description }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="pointCost" label="积分" width="90" />
          <el-table-column prop="stock" label="库存" width="90" />
          <el-table-column prop="exchangedCount" label="已兑换" width="90" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'">
                {{ Number(row.status) === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button
                link
                :type="Number(row.status) === 1 ? 'warning' : 'success'"
                @click="toggleStatus(row)"
              >
                {{ Number(row.status) === 1 ? '下架' : '上架' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <template v-else>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item>
            <el-input
              v-model="filters.exchangeCode"
              clearable
              placeholder="按兑换码筛选"
              style="width: 220px"
            />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never">
        <el-table :data="exchangeList" v-loading="loading" border>
          <el-table-column prop="exchangeCode" label="兑换码" min-width="160" />
          <el-table-column label="礼品" min-width="150">
            <template #default="{ row }">{{
              giftMap.get(row.giftId) || `#${row.giftId}`
            }}</template>
          </el-table-column>
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="pointCost" label="积分" width="90" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'VERIFIED' ? 'success' : 'warning'">
                {{ row.status === 'VERIFIED' ? '已核销' : '待核销' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="核销时间" min-width="170">
            <template #default="{ row }">{{
              row.verifyTime ? row.verifyTime.replace('T', ' ') : '-'
            }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                :disabled="row.status === 'VERIFIED'"
                @click="verifyExchange(row.id)"
              >
                核销
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑礼品' : '新增礼品'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="礼品名称" prop="name">
          <el-input v-model="formData.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="礼品描述" prop="description">
          <el-input v-model="formData.description" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="图片地址">
          <el-input v-model="formData.imageUrl" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="uploadGiftImage"
          >
            <el-button :loading="uploadLoading">上传礼品图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="图片预览" v-if="normalizeImageUrl(formData.imageUrl)">
          <el-image
            :src="normalizeImageUrl(formData.imageUrl)"
            fit="cover"
            class="gift-cover-preview"
            :preview-src-list="[normalizeImageUrl(formData.imageUrl)]"
            preview-teleported
          />
        </el-form-item>
        <el-form-item label="所需积分" prop="pointCost">
          <el-input-number v-model="formData.pointCost" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" />
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
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.gift-cover-table {
  width: 64px;
  height: 64px;
  border-radius: 6px;
}

.gift-cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #8a8a8a;
  background: #f5f7fa;
  border: 1px dashed #d8dde6;
}

.gift-cover-preview {
  width: 120px;
  height: 120px;
  border-radius: 8px;
}
</style>
