import request from './request'

export const getAdminItems = (status) => request.get('/api/admin/items', { params: { status } })

export const auditItem = (itemId, approved, reason) =>
  request.post(`/api/admin/items/${itemId}/audit`, null, { params: { approved, reason } })

export const forceOffShelf = (itemId, reason) =>
  request.post(`/api/admin/items/${itemId}/force-off-shelf`, null, { params: { reason } })
