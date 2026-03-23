import request from './request'

export const getDropPoints = (campusId) =>
  request.get('/api/admin/drop-points', { params: { campusId } })

export const createDropPoint = (payload) => request.post('/api/admin/drop-points', payload)

export const updateDropPoint = (dropPointId, payload) =>
  request.put(`/api/admin/drop-points/${dropPointId}`, payload)

export const changeDropPointStatus = (dropPointId, enabled) =>
  request.post(`/api/admin/drop-points/${dropPointId}/status`, null, { params: { enabled } })
