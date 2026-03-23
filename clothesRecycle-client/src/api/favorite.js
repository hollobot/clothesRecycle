import request from './request'

export const addFavorite = (itemId) => request.post(`/api/user/favorites/${itemId}`)
export const removeFavorite = (itemId) => request.delete(`/api/user/favorites/${itemId}`)
export const getFavoriteList = () => request.get('/api/user/favorites')
