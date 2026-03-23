<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  phone: '',
  password: '',
})

const handleLogin = async () => {
  if (!form.phone || !form.password) {
    ElMessage.warning('请输入手机号和密码')
    return
  }

  try {
    const data = await login(form)
    userStore.setLogin(data)
    router.push(String(route.query.redirect || '/'))
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  }
}
</script>

<template>
  <div class="page auth-page">
    <div class="auth-card">
      <div class="login-logo">
        <div class="login-logo-badge"><div class="login-logo-inner"></div></div>
        <div>
          <div class="login-title">用户登录</div>
          <div class="login-sub">校园二手衣物回收系统</div>
        </div>
      </div>

      <div class="field-row">
        <label>手机号</label>
        <el-input v-model="form.phone" class="input" placeholder="请输入手机号" />
      </div>

      <div class="field-row">
        <label>密码</label>
        <el-input
          v-model="form.password"
          type="password"
          show-password
          class="input"
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        />
      </div>

      <button class="btn btn-primary btn-block" @click="handleLogin">登录</button>
      <button class="btn btn-light btn-block" @click="$router.push('/register')">
        没有账号，去注册
      </button>
    </div>
  </div>
</template>
