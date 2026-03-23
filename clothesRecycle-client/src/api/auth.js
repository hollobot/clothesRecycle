import request from './request'

export const sendSms = (phone) => request.post('/api/public/auth/sms', { phone })
export const register = (payload) => request.post('/api/public/auth/register', payload)
export const login = (payload) => request.post('/api/public/auth/login', payload)
