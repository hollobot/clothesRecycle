import request from './request'

export const publishItem = (payload) => request.post('/api/user/items', payload)
export const getMyPublishedItems = () => request.get('/api/user/items/mine')
export const cancelMyPublishedItem = (itemId, reason) =>
  request.post(`/api/user/items/${itemId}/cancel`, null, { params: { reason } })

/**
 * 衣物广场查询参数。
 * @param {Object} params 查询参数
 * @param {number=} params.campusId 校区 ID
 * @param {string=} params.keyword 关键词（标题/描述）
 * @param {string=} params.category 分类
 */
export const getItemList = (params = {}) => request.get('/api/public/items', { params })
export const getItemDetail = (itemId) => request.get(`/api/public/items/${itemId}`)
