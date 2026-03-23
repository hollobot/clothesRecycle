import request from './request'

export const createOrder = (payload) => request.post('/api/user/orders', payload)
export const confirmOrder = (orderId) => request.post(`/api/user/orders/${orderId}/confirm`)
export const cancelOrder = (orderId) => request.post(`/api/user/orders/${orderId}/cancel`)
export const completeOrder = (orderId) => request.post(`/api/user/orders/${orderId}/complete`)
export const listMyOrders = () => request.get('/api/user/orders')
