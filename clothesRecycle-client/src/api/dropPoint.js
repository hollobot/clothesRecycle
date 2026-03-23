import request from './request'

export const getDropPoints = (campusId) =>
  request.get('/api/public/drop-points', { params: { campusId } })
