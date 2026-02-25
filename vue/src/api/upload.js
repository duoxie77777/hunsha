import request from '../utils/request'

// 上传单张图片
export function uploadImageApi(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 批量上传图片
export function uploadImagesApi(files) {
  const formData = new FormData()
  files.forEach(f => formData.append('files', f))
  return request.post('/upload/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
