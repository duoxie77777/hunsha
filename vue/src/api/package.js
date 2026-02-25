import request from '../utils/request'

// 获取套餐列表（前台，只返回上架的）
export function getPackagesApi(category) {
  return request.get('/packages', { params: { category } })
}

// 获取套餐详情
export function getPackageDetailApi(id) {
  return request.get(`/packages/${id}`)
}

// ========== 后台管理 ==========

// 获取套餐列表（分页）
export function adminGetPackagesApi(params) {
  return request.get('/admin/packages', { params })
}

// 新增套餐
export function adminCreatePackageApi(data) {
  return request.post('/admin/packages', data)
}

// 更新套餐
export function adminUpdatePackageApi(id, data) {
  return request.put(`/admin/packages/${id}`, data)
}

// 删除套餐
export function adminDeletePackageApi(id) {
  return request.delete(`/admin/packages/${id}`)
}

// 上下架
export function adminTogglePackageStatusApi(id, status) {
  return request.put(`/admin/packages/${id}/status`, { status })
}
