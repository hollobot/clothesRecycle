import request from './request'

export const getGiftList = () => request.get('/api/public/gifts')
export const exchangeGift = (giftId) => request.post(`/api/user/gifts/${giftId}/exchange`)
export const getMyGiftExchanges = () => request.get('/api/user/gifts/exchanges')
