import request from './request'

export const signToday = () => request.post('/api/user/sign/today')
export const getTodaySignStatus = () => request.get('/api/user/sign/today')
export const getYearSignData = (year) => request.get('/api/user/sign/year', { params: { year } })
