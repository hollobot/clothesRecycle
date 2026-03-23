import request from './request'

export const getAdminUsers = (params) => request.get('/api/admin/users', { params })

export const getAdminUserDetail = (userId) => request.get(`/api/admin/users/${userId}`)

export const createAdminUser = (payload) => request.post('/api/admin/users', payload)

export const updateAdminUser = (userId, payload) =>
  request.put(`/api/admin/users/${userId}`, payload)

export const changeAdminUserStatus = (userId, enabled) =>
  request.post(`/api/admin/users/${userId}/status`, null, { params: { enabled } })

export const deleteAdminUser = (userId) => request.delete(`/api/admin/users/${userId}`)
