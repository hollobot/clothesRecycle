import request from './request'

export const getCampusList = () => request.get('/api/public/campus/list')
