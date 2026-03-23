import request from './request'

export const getUserList = () => request.get('/api/admin/items')
