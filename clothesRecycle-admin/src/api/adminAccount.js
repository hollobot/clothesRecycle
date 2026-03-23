import request from './request'

export const getAdminAccounts = (params) => request.get('/api/super/admin-accounts', { params })

export const getAdminAccountDetail = (adminId) =>
  request.get(`/api/super/admin-accounts/${adminId}`)

export const createAdminAccount = (payload) => request.post('/api/super/admin-accounts', payload)

export const updateAdminAccount = (adminId, payload) =>
  request.put(`/api/super/admin-accounts/${adminId}`, payload)

export const resetAdminPassword = (adminId, password) =>
  request.post(`/api/super/admin-accounts/${adminId}/password`, null, { params: { password } })

export const changeAdminAccountStatus = (adminId, enabled) =>
  request.post(`/api/super/admin-accounts/${adminId}/status`, null, { params: { enabled } })

export const deleteAdminAccount = (adminId) =>
  request.delete(`/api/super/admin-accounts/${adminId}`)
