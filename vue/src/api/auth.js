import request from '../utils/request'

// 用户注册
export function registerApi(data) {
  return request.post('/auth/register', data)
}

// 用户登录
export function loginApi(data) {
  return request.post('/auth/login', data)
}

// 获取当前用户信息
export function getUserInfoApi() {
  return request.get('/auth/info')
}

// 更新用户资料
export function updateProfileApi(data) {
  return request.put('/auth/profile', data)
}
