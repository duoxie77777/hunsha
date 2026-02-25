import request from '../utils/request'

// 获取评价列表（前台）
export function getReviewsApi(params) {
  return request.get('/reviews', { params })
}

// 提交评价
export function submitReviewApi(data) {
  return request.post('/reviews', data)
}

// ========== 后台管理 ==========

// 获取评价列表（分页）
export function adminGetReviewsApi(params) {
  return request.get('/admin/reviews', { params })
}

// 回复评价
export function adminReplyReviewApi(id, reply) {
  return request.put(`/admin/reviews/${id}/reply`, { reply })
}

// 设为/取消精选
export function adminToggleFeaturedApi(id) {
  return request.put(`/admin/reviews/${id}/featured`)
}

// 显示/隐藏评价
export function adminUpdateReviewStatusApi(id, status) {
  return request.put(`/admin/reviews/${id}/status`, { status })
}
