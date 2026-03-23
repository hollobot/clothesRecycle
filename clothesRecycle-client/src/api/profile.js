import request from './request'

/**
 * 获取个人中心统计概览。
 */
export const getUserOverview = () => request.get('/api/user/profile/overview')

/**
 * 获取当前登录用户资料。
 */
export const getUserProfile = () => request.get('/api/user/profile')

/**
 * 更新当前登录用户基础资料。
 */
export const updateUserProfile = (payload) => request.put('/api/user/profile', payload)

/**
 * 使用旧密码修改密码。
 */
export const changePassword = (payload) => request.post('/api/user/profile/password', payload)

/**
 * 发送短信验证码（模拟发送，验证码打印在后端控制台）。
 */
export const sendPasswordResetSms = () => request.post('/api/user/profile/password/sms/send')

/**
 * 使用短信验证码重置密码。
 */
export const resetPasswordBySms = (payload) =>
  request.post('/api/user/profile/password/sms/reset', payload)
