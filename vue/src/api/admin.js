import request from '../utils/request'

// 看板统计数据
export function getDashboardStatsApi() {
  return request.get('/admin/dashboard/stats')
}

// ========== 客户管理 ==========

// 获取客户列表（分页）
export function getCustomersApi(params) {
  return request.get('/admin/customers', { params })
}

// 获取客户详情
export function getCustomerDetailApi(id) {
  return request.get(`/admin/customers/${id}`)
}

// 启用/禁用客户
export function updateCustomerStatusApi(id, status) {
  return request.put(`/admin/customers/${id}/status`, { status })
}

// ========== 档期管理 ==========

// 获取档期列表（分页）
export function getSchedulesApi(params) {
  return request.get('/admin/schedules', { params })
}

// 新增档期
export function createScheduleApi(data) {
  return request.post('/admin/schedules', data)
}

// 更新档期
export function updateScheduleApi(id, data) {
  return request.put(`/admin/schedules/${id}`, data)
}

// 删除档期
export function deleteScheduleApi(id) {
  return request.delete(`/admin/schedules/${id}`)
}
