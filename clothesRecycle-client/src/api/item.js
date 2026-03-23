import request from './request'

export const publishItem = (payload) => request.post('/api/user/items', payload)
export const getItemList = (campusId) => request.get('/api/public/items', { params: { campusId } })
export const getItemDetail = (itemId) => request.get(`/api/public/items/${itemId}`)
