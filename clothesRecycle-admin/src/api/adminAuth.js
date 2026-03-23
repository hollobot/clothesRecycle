import request from './request'

export const adminLogin = (payload) => request.post('/api/public/admin/login', payload)
