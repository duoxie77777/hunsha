import request from '../utils/request'

// 获取作品列表（前台，分页）
export function getPortfoliosApi(params) {
  return request.get('/portfolios', { params })
}

// 获取作品详情
export function getPortfolioDetailApi(id) {
  return request.get(`/portfolios/${id}`)
}

// ========== 后台管理 ==========

// 获取作品列表（分页）
export function adminGetPortfoliosApi(params) {
  return request.get('/admin/portfolios', { params })
}

// 新增作品
export function adminCreatePortfolioApi(data) {
  return request.post('/admin/portfolios', data)
}

// 更新作品
export function adminUpdatePortfolioApi(id, data) {
  return request.put(`/admin/portfolios/${id}`, data)
}

// 删除作品
export function adminDeletePortfolioApi(id) {
  return request.delete(`/admin/portfolios/${id}`)
}
