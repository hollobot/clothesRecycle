import request from './request'

export const getRankList = (type, campusId, limit = 50) =>
  request.get(`/api/public/rank/${type}`, { params: { campusId, limit } })
export const getMyRank = (campusId) => request.get('/api/user/rank/mine', { params: { campusId } })
