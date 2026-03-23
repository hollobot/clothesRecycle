import request from './request'

/**
 * 兼容旧页面调用：用户列表应走 /api/admin/users，而非物品接口。
 */
export const getUserList = (params) => request.get('/api/admin/users', { params })
