<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { getDropPoints } from '@/api/dropPoint'
import { uploadItemImages } from '@/api/file'
import { publishItem } from '@/api/item'

const MAX_IMAGE_COUNT = 9
const MAX_IMAGE_SIZE = 5 * 1024 * 1024
const router = useRouter()

const form = reactive({
  title: '',
  category: '上装',
  genderType: '通用',
  sizeType: 'M',
  conditionLevel: '九成新',
  description: '',
  acquireType: 'FREE',
  campusId: 1,
  pointPrice: 0,
})

const campuses = ref([])
const dropPoints = ref([])
const uploadFileList = ref([])
const uploadedImageUrls = ref([])
const uploadLoading = ref(false)
const submitLoading = ref(false)

const beforeSelectUpload = (rawFile) => {
  // 使用 Element Plus 上传组件前置校验，避免无效文件进入列表。
  const isImage = String(rawFile.type || '').startsWith('image/')
  const isWithinSize = rawFile.size <= MAX_IMAGE_SIZE

  if (!isImage || !isWithinSize) {
    ElMessage.warning('仅支持图片格式，且单张不能超过 5MB')
    return false
  }
  return true
}

const handleUploadListChange = () => {
  // 文件列表变化后，强制重新上传以确保 URL 与当前选择一致。
  uploadedImageUrls.value = []
}

const handleUploadExceed = () => {
  ElMessage.warning(`最多上传 ${MAX_IMAGE_COUNT} 张图片`)
}

// 与后端 /api/user/files/images 对齐，上传成功后拿到 URL 列表。
const handleUploadImages = async () => {
  const files = uploadFileList.value.map((file) => file.raw).filter(Boolean)

  if (files.length === 0) {
    ElMessage.warning('请先选择图片')
    return
  }

  uploadLoading.value = true
  try {
    const urls = await uploadItemImages(files)
    uploadedImageUrls.value = Array.isArray(urls) ? urls : []
    ElMessage.success(`图片上传成功，共 ${uploadedImageUrls.value.length} 张`)
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    uploadLoading.value = false
  }
}

const handleSubmit = async () => {
  if (submitLoading.value) return

  if (!form.title.trim()) {
    ElMessage.warning('请填写物品名称')
    return
  }

  if (!form.description.trim()) {
    ElMessage.warning('请填写物品描述')
    return
  }

  // 发布接口要求 imageUrls 必填，这里强制校验。
  if (uploadedImageUrls.value.length === 0) {
    ElMessage.warning('请先上传至少 1 张图片')
    return
  }

  submitLoading.value = true
  try {
    await publishItem({
      ...form,
      imageUrls: uploadedImageUrls.value,
      pointPrice: form.acquireType === 'POINT' ? Number(form.pointPrice || 0) : 0,
    })

    ElMessage.success('发布成功，等待审核')

    // 发布成功后回到首页，便于用户继续浏览与分享。
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    submitLoading.value = false
  }
}

watch(
  () => form.acquireType,
  (value) => {
    if (value === 'FREE') form.pointPrice = 0
  },
)

watch(
  () => form.campusId,
  async (campusId) => {
    try {
      dropPoints.value = await getDropPoints(campusId)
    } catch {
      dropPoints.value = []
    }
  },
)

onMounted(async () => {
  try {
    campuses.value = await getCampusList()
    if (campuses.value.length > 0) {
      form.campusId = campuses.value[0].id
    }
    dropPoints.value = await getDropPoints(form.campusId)
  } catch (error) {
    ElMessage.error(error.message || '初始化发布页失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar"><h1>发布衣物</h1></div>

    <div class="section">
      <div class="upload-zone">
        <div class="upload-plus">+</div>
        <div class="upload-text">上传图片（最多 9 张，单张 5MB）</div>

        <el-upload
          v-model:file-list="uploadFileList"
          class="publish-upload"
          list-type="picture-card"
          multiple
          :limit="MAX_IMAGE_COUNT"
          :auto-upload="false"
          :before-upload="beforeSelectUpload"
          :on-change="handleUploadListChange"
          :on-remove="handleUploadListChange"
          :on-exceed="handleUploadExceed"
        >
          <span class="upload-picker-plus">+</span>
        </el-upload>

        <button class="btn btn-light" :disabled="uploadLoading" @click="handleUploadImages">
          {{ uploadLoading ? '上传中...' : '上传图片' }}
        </button>

        <p class="helper-text" v-if="uploadedImageUrls.length > 0">
          已上传 {{ uploadedImageUrls.length }} 张，可提交发布
        </p>
      </div>
    </div>

    <div class="section compact-section">
      <div class="field-row">
        <label>物品名称</label>
        <el-input v-model="form.title" class="input" placeholder="例如：白色T恤 M码" />
      </div>

      <div class="field-row">
        <label>所属校区</label>
        <el-select v-model="form.campusId" class="select" style="width: 100%">
          <el-option
            v-for="campus in campuses"
            :key="campus.id"
            :label="campus.name"
            :value="campus.id"
          />
        </el-select>
      </div>

      <div class="field-row">
        <label>品类</label>
        <el-segmented
          v-model="form.category"
          :options="['上装', '下装', '外套', '鞋帽', '配饰']"
          block
        />
      </div>

      <div class="field-row">
        <label>性别</label>
        <el-segmented v-model="form.genderType" :options="['男', '女', '通用']" block />
      </div>

      <div class="field-row">
        <label>尺码</label>
        <el-segmented
          v-model="form.sizeType"
          :options="['XS', 'S', 'M', 'L', 'XL', 'XXL', '均码']"
          block
        />
      </div>

      <div class="field-row">
        <label>新旧程度</label>
        <el-segmented
          v-model="form.conditionLevel"
          :options="['九成新', '八成新', '七成新及以下']"
          block
        />
      </div>

      <div class="field-row">
        <label>描述</label>
        <el-input v-model="form.description" class="textarea" type="textarea" :rows="4" />
      </div>

      <div class="field-row">
        <label>获取方式</label>
        <el-segmented
          v-model="form.acquireType"
          :options="[
            { label: '免费领取', value: 'FREE' },
            { label: '积分兑换', value: 'POINT' },
          ]"
          block
        />
      </div>

      <div class="field-row" v-if="form.acquireType === 'POINT'">
        <label>积分价格</label>
        <el-input-number v-model="form.pointPrice" :min="10" :max="500" />
      </div>

      <p class="helper-text" v-if="dropPoints.length > 0">
        可投递回收点：{{ dropPoints.map((point) => point.name).join('、') }}
      </p>
    </div>

    <div class="section no-bg">
      <button class="btn btn-primary btn-block" :disabled="submitLoading" @click="handleSubmit">
        {{ submitLoading ? '提交中...' : '提交审核' }}
      </button>
    </div>
  </div>
</template>
