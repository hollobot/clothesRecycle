import request from './request'

/**
 * 超级管理员视角：校区管理列表（/api/super/** 需要 SUPER_ADMIN）。
 */
export const getCampusList = () => request.get('/api/super/campuses')

/**
 * 通用校区下拉数据（/api/public/** 所有已登录管理角色可用）。
 */
export const getCampusOptions = () => request.get('/api/public/campus/list')

export const createCampus = (payload) => request.post('/api/super/campuses', payload)

export const updateCampus = (campusId, payload) =>
  request.put(`/api/super/campuses/${campusId}`, payload)

export const changeCampusStatus = (campusId, enabled) =>
  request.post(`/api/super/campuses/${campusId}/status`, null, { params: { enabled } })

export const deleteCampus = (campusId) => request.delete(`/api/super/campuses/${campusId}`)
