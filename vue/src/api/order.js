import request from '../utils/request'

// 创建订单
export function createOrderApi(data) {
  return request.post('/orders', data)
}

// 获取我的订单
export function getMyOrdersApi() {
  return request.get('/orders/mine')
}

// 取消订单
export function cancelOrderApi(id) {
  return request.put(`/orders/${id}/cancel`)
}

// 获取订单详情
export function getOrderDetailApi(id) {
  return request.get(`/orders/${id}`)
}

// ========== 后台管理 ==========

// 获取订单列表（分页）
export function adminGetOrdersApi(params) {
  return request.get('/admin/orders', { params })
}

// 获取订单详情
export function adminGetOrderDetailApi(id) {
  return request.get(`/admin/orders/${id}`)
}

// 更新订单状态
export function adminUpdateOrderStatusApi(id, status) {
  return request.put(`/admin/orders/${id}/status`, { status })
}
