import request from './request'

// 上传用户发布物品图片，返回可直接提交给发布接口的 URL 列表。
export const uploadItemImages = (files) => {
  const formData = new FormData()

  files.forEach((file) => {
    formData.append('files', file)
  })

  return request.post('/api/user/files/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
