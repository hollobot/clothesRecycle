import request from './request'

export const getGiftList = () => request.get('/api/admin/gifts')

export const createGift = (payload) => request.post('/api/admin/gifts', payload)

export const updateGift = (giftId, payload) => request.put(`/api/admin/gifts/${giftId}`, payload)

export const changeGiftStatus = (giftId, enabled) =>
  request.post(`/api/admin/gifts/${giftId}/status`, null, { params: { enabled } })

export const getGiftExchanges = () => request.get('/api/admin/gifts/exchanges')

export const verifyGiftExchange = (exchangeId) =>
  request.post(`/api/admin/gifts/exchanges/${exchangeId}/verify`)
