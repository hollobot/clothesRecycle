<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { getDropPoints } from '@/api/dropPoint'
import { publishItem } from '@/api/item'

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

const handleSubmit = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('请填写物品名称')
    return
  }

  try {
    await publishItem({
      ...form,
      pointPrice: form.acquireType === 'POINT' ? Number(form.pointPrice || 0) : 0,
    })

    ElMessage.success('发布成功，等待审核')

    Object.assign(form, {
      title: '',
      category: '上装',
      genderType: '通用',
      sizeType: 'M',
      conditionLevel: '九成新',
      description: '',
      acquireType: 'FREE',
      campusId: campuses.value[0]?.id || 1,
      pointPrice: 0,
    })
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
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
        <div class="upload-text">上传图片（后端未接入文件接口，当前为占位）</div>
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
      <button class="btn btn-primary btn-block" @click="handleSubmit">提交审核</button>
    </div>
  </div>
</template>
