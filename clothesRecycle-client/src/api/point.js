import request from './request'

export const getPointAccount = () => request.get('/api/user/points/account')
export const getPointRecords = (pageNum = 1, pageSize = 12) =>
  request.get('/api/user/points/records', { params: { pageNum, pageSize } })
