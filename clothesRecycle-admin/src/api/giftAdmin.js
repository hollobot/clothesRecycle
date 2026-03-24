import request from './request'

export const getGiftList = () => request.get('/api/admin/gifts')

export const createGift = (payload) => request.post('/api/admin/gifts', payload)

export const updateGift = (giftId, payload) => request.put(`/api/admin/gifts/${giftId}`, payload)

export const changeGiftStatus = (giftId, enabled) =>
  request.post(`/api/admin/gifts/${giftId}/status`, null, { params: { enabled } })

export const getGiftExchanges = () => request.get('/api/admin/gifts/exchanges')

export const verifyGiftExchange = (exchangeId) =>
  request.post(`/api/admin/gifts/exchanges/${exchangeId}/verify`)

/**
 * 上传礼品图片，返回图片 URL 列表。
 *
 * @param {File[]} files 图片文件数组
 */
export const uploadGiftImages = (files) => {
  const formData = new FormData()
  files.forEach((file) => {
    formData.append('files', file)
  })

  return request.post('/api/admin/files/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
