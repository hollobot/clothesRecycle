import request from './request'

export const getCampusList = () => request.get('/api/super/campuses')

export const createCampus = (payload) => request.post('/api/super/campuses', payload)

export const updateCampus = (campusId, payload) =>
  request.put(`/api/super/campuses/${campusId}`, payload)

export const changeCampusStatus = (campusId, enabled) =>
  request.post(`/api/super/campuses/${campusId}/status`, null, { params: { enabled } })

export const deleteCampus = (campusId) => request.delete(`/api/super/campuses/${campusId}`)
