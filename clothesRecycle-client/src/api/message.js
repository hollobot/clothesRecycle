import request from './request'

export const getMessagePage = (pageNum = 1, pageSize = 12) =>
  request.get('/api/user/messages', { params: { pageNum, pageSize } })
export const getUnreadCount = () => request.get('/api/user/messages/unread-count')
export const markMessageRead = (messageId) => request.post(`/api/user/messages/${messageId}/read`)
export const markAllMessagesRead = () => request.post('/api/user/messages/read-all')
