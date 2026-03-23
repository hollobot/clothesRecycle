import request from './request'

// 连通性检查接口，当前前端先保留占位，便于后续接入启动自检。
export const pingServer = () => request.get('/api/public/ping')
