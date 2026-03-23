<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '@/api/adminAuth'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const store = useAdminStore()

const form = reactive({
  phone: '',
  password: '',
})

const loading = ref(false)
const errorText = ref('')

const handleLogin = async () => {
  if (!form.phone || !form.password) {
    errorText.value = '请输入手机号和密码'
    return
  }

  loading.value = true
  errorText.value = ''

  try {
    const data = await adminLogin(form)
    store.setLogin(data)
    router.push('/dashboard')
  } catch (error) {
    errorText.value = error?.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-logo">
        <div class="login-logo-badge"><div class="login-logo-inner"></div></div>
        <div>
          <div class="login-title">管理后台</div>
          <div class="login-sub">校园二手衣物回收系统</div>
        </div>
      </div>

      <div class="login-field">
        <label class="login-label">手机号</label>
        <input v-model.trim="form.phone" class="login-input" placeholder="请输入手机号" />
      </div>

      <div class="login-field">
        <label class="login-label">密码</label>
        <input
          v-model.trim="form.password"
          type="password"
          class="login-input"
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        />
      </div>

      <p v-if="errorText" class="login-error">{{ errorText }}</p>
      <button class="login-btn" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <p class="login-tip">超级管理员 / 校区管理员 共用登录</p>
    </div>
  </div>
</template>
