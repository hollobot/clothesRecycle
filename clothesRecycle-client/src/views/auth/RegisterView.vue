<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register, sendSms } from '@/api/auth'
import { getCampusList } from '@/api/campus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  phone: '',
  smsCode: '',
  password: '',
  studentId: '',
  name: '',
  campusId: 1,
})

const campuses = ref([])

const handleSendSms = async () => {
  if (!form.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }

  try {
    await sendSms(form.phone)
    ElMessage.success('验证码已发送（开发环境模拟）')
  } catch (error) {
    ElMessage.error(error.message || '发送验证码失败')
  }
}

const handleRegister = async () => {
  try {
    const data = await register(form)
    userStore.setLogin(data)
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  }
}

onMounted(async () => {
  try {
    campuses.value = await getCampusList()
    if (campuses.value.length > 0) {
      form.campusId = campuses.value[0].id
    }
  } catch {
    campuses.value = []
  }
})
</script>

<template>
  <div class="page auth-page">
    <div class="auth-card">
      <div class="login-logo">
        <div class="login-logo-badge"><div class="login-logo-inner"></div></div>
        <div>
          <div class="login-title">用户注册</div>
          <div class="login-sub">注册后可发布和认领衣物</div>
        </div>
      </div>

      <div class="field-row">
        <label>手机号</label>
        <el-input v-model="form.phone" class="input" placeholder="请输入手机号" />
      </div>

      <div class="field-row">
        <label>验证码</label>
        <div class="row-inline">
          <el-input v-model="form.smsCode" class="input" placeholder="请输入验证码" />
          <button class="btn btn-light" @click="handleSendSms">获取</button>
        </div>
      </div>

      <div class="field-row">
        <label>密码</label>
        <el-input v-model="form.password" type="password" show-password class="input" />
      </div>

      <div class="field-row">
        <label>学号</label>
        <el-input v-model="form.studentId" class="input" />
      </div>

      <div class="field-row">
        <label>姓名</label>
        <el-input v-model="form.name" class="input" />
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

      <button class="btn btn-primary btn-block" @click="handleRegister">注册并登录</button>
      <button class="btn btn-light btn-block" @click="$router.push('/login')">
        已有账号，去登录
      </button>
    </div>
  </div>
</template>
