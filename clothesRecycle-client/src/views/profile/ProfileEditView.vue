<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCampusList } from '@/api/campus'
import { uploadItemImages } from '@/api/file'
import {
  changePassword,
  getUserProfile,
  resetPasswordBySms,
  sendPasswordResetSms,
  updateUserProfile,
} from '@/api/profile'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const saveProfileLoading = ref(false)
const changePwdLoading = ref(false)
const resetPwdLoading = ref(false)
const smsSending = ref(false)
const smsCountdown = ref(0)
const activeTab = ref('profile')
const useSmsReset = ref(false)
let countdownTimer = null

const campuses = ref([])

const profileForm = reactive({
  phone: '',
  name: '',
  studentId: '',
  campusId: undefined,
  avatarUrl: '',
})

const pwdForm = reactive({
  account: '',
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: '',
})

const smsForm = reactive({
  smsCode: '',
  newPassword: '',
  confirmNewPassword: '',
})

const canSubmitProfile = computed(
  () =>
    !!profileForm.name.trim() &&
    !!profileForm.studentId.trim() &&
    Number(profileForm.campusId) > 0 &&
    !saveProfileLoading.value,
)

const canChangePassword = computed(() => {
  if (changePwdLoading.value) return false
  if (useSmsReset.value) return false
  if (!pwdForm.oldPassword || !pwdForm.newPassword || !pwdForm.confirmNewPassword) return false
  if (pwdForm.newPassword.length < 6) return false
  if (pwdForm.oldPassword === pwdForm.newPassword) return false
  return pwdForm.newPassword === pwdForm.confirmNewPassword
})

const canResetBySms = computed(() => {
  if (resetPwdLoading.value) return false
  if (!useSmsReset.value) return false
  if (!smsForm.smsCode || !smsForm.newPassword || !smsForm.confirmNewPassword) return false
  if (smsForm.newPassword.length < 6) return false
  if (smsForm.newPassword === pwdForm.oldPassword) return false
  return smsForm.newPassword === smsForm.confirmNewPassword
})

const canSendSms = computed(() => !smsSending.value && smsCountdown.value <= 0)

const load = async () => {
  loading.value = true
  try {
    const [profile, campusList] = await Promise.all([getUserProfile(), getCampusList()])
    campuses.value = Array.isArray(campusList) ? campusList : []

    profileForm.phone = profile?.phone || ''
    profileForm.name = profile?.name || ''
    profileForm.studentId = profile?.studentId || ''
    profileForm.campusId = Number(profile?.campusId || campuses.value[0]?.id || 0) || undefined
    profileForm.avatarUrl = profile?.avatarUrl || ''

    pwdForm.account = profile?.phone || ''

    userStore.updateProfile({
      phone: profile?.phone,
      name: profile?.name,
      studentId: profile?.studentId,
      campusId: profile?.campusId,
      avatarUrl: profile?.avatarUrl,
    })
  } finally {
    loading.value = false
  }
}

/**
 * 上传头像并立即回填头像地址。
 */
const onAvatarChange = async (file) => {
  if (!file?.raw) return
  const raw = file.raw
  if (!String(raw.type || '').startsWith('image/')) {
    ElMessage.warning('仅支持图片格式头像')
    return
  }
  if (raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('头像大小不能超过5MB')
    return
  }

  try {
    const urls = await uploadItemImages([raw])
    if (Array.isArray(urls) && urls[0]) {
      profileForm.avatarUrl = urls[0]
      ElMessage.success('头像上传成功')
    }
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  }
}

const onSaveProfile = async () => {
  if (!canSubmitProfile.value) return

  saveProfileLoading.value = true
  try {
    const data = await updateUserProfile({
      name: profileForm.name.trim(),
      studentId: profileForm.studentId.trim(),
      campusId: Number(profileForm.campusId),
      avatarUrl: profileForm.avatarUrl || '',
    })
    userStore.updateProfile({
      name: data?.name,
      studentId: data?.studentId,
      campusId: data?.campusId,
      avatarUrl: data?.avatarUrl,
      phone: data?.phone,
    })
    ElMessage.success('基础信息已更新')
    router.replace('/profile')
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    saveProfileLoading.value = false
  }
}

const onChangePassword = async () => {
  if (!canChangePassword.value) return
  if (pwdForm.oldPassword === pwdForm.newPassword) {
    ElMessage.warning('新密码不能与旧密码相同')
    return
  }

  changePwdLoading.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.replace('/login')
  } catch (error) {
    ElMessage.error(error.message || '修改密码失败')
  } finally {
    changePwdLoading.value = false
  }
}

/**
 * 发送短信验证码并开启 60 秒倒计时。
 */
const onSendSmsCode = async () => {
  if (!canSendSms.value) return

  smsSending.value = true
  try {
    await sendPasswordResetSms()
    ElMessage.success('验证码已发送（请查看后端控制台日志）')
    smsCountdown.value = 60
    if (countdownTimer) clearInterval(countdownTimer)
    countdownTimer = setInterval(() => {
      smsCountdown.value -= 1
      if (smsCountdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (error) {
    ElMessage.error(error.message || '发送验证码失败')
  } finally {
    smsSending.value = false
  }
}

const onResetPasswordBySms = async () => {
  if (!canResetBySms.value) return

  resetPwdLoading.value = true
  try {
    await resetPasswordBySms({
      smsCode: smsForm.smsCode,
      newPassword: smsForm.newPassword,
    })
    ElMessage.success('密码重置成功，请重新登录')
    userStore.logout()
    router.replace('/login')
  } catch (error) {
    ElMessage.error(error.message || '短信重置失败')
  } finally {
    resetPwdLoading.value = false
  }
}

/**
 * 切换密码修改模式：普通改密 / 短信验证码改密。
 */
const onToggleResetMode = () => {
  useSmsReset.value = !useSmsReset.value
}

onMounted(async () => {
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载账户资料失败')
  }
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})
</script>

<template>
  <div class="page" v-loading="loading">
    <div class="topbar topbar-back">
      <button class="ghost-btn" @click="$router.back()">返回</button>
      <h1>编辑资料</h1>
      <span style="width: 54px"></span>
    </div>

    <el-card shadow="never" class="section">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础信息" name="profile">
          <el-form label-width="88px">
            <el-form-item label="头像">
              <div class="avatar-row">
                <el-avatar :size="60" :src="profileForm.avatarUrl">
                  {{ (profileForm.name || '用').slice(0, 1) }}
                </el-avatar>
                <el-upload :show-file-list="false" :auto-upload="false" :on-change="onAvatarChange">
                  <el-button>上传头像</el-button>
                </el-upload>
              </div>
            </el-form-item>

            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="profileForm.name" maxlength="64" show-word-limit />
            </el-form-item>
            <el-form-item label="学号">
              <el-input v-model="profileForm.studentId" maxlength="32" show-word-limit />
            </el-form-item>
            <el-form-item label="所属校区">
              <el-select v-model="profileForm.campusId" style="width: 100%">
                <el-option
                  v-for="campus in campuses"
                  :key="campus.id"
                  :label="campus.name"
                  :value="campus.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :disabled="!canSubmitProfile" @click="onSaveProfile">
                {{ saveProfileLoading ? '保存中...' : '保存基础信息' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="密码修改" name="password">
          <el-form label-width="88px" v-if="!useSmsReset">
            <el-form-item label="账户">
              <el-input v-model="pwdForm.account" disabled />
            </el-form-item>
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="pwdForm.confirmNewPassword" type="password" show-password />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :disabled="!canChangePassword" @click="onChangePassword">
                {{ changePwdLoading ? '提交中...' : '使用旧密码修改' }}
              </el-button>
            </el-form-item>
            <el-form-item>
              <el-link type="warning" plain @click="onToggleResetMode">忘记密码？用验证码修改</el-link>
            </el-form-item>
          </el-form>

          <el-form label-width="88px" v-else>
            <el-form-item label="账户">
              <el-input v-model="profileForm.phone" disabled />
            </el-form-item>
            <el-form-item label="验证码">
              <div class="sms-row">
                <el-input v-model="smsForm.smsCode" maxlength="6" placeholder="请输入6位验证码" />
                <el-button :disabled="!canSendSms" @click="onSendSmsCode">
                  {{ smsCountdown > 0 ? `${smsCountdown}s后重试` : smsSending ? '发送中...' : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="smsForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="smsForm.confirmNewPassword" type="password" show-password />
            </el-form-item>

            <el-form-item>
              <el-button type="warning" :disabled="!canResetBySms" @click="onResetPasswordBySms">
                {{ resetPwdLoading ? '提交中...' : '短信验证重置密码' }}
              </el-button>
            </el-form-item>
            <el-form-item>
              <el-link type="warning" plain @click="onToggleResetMode">返回旧密码修改</el-link>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.card-title {
  font-size: 14px;
  font-weight: 600;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sms-row {
  width: 100%;
  display: flex;
  gap: 8px;
}
</style>
