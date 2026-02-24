import axios from 'axios'
import { ElMessage } from 'element-plus'
import { userStore } from '../stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 自动携带 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 业务状态码不为 200，视为业务错误
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')

      // 401 未登录或 token 过期
      if (res.code === 401) {
        localStorage.removeItem('token')
        userStore.user = null
        userStore.showLogin = true
      }

      return Promise.reject(new Error(res.msg || '请求失败'))
    }

    return res
  },
  (error) => {
    // HTTP 错误
    let message = '网络异常，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未登录或登录已过期'
          localStorage.removeItem('token')
          userStore.user = null
          userStore.showLogin = true
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器异常'
          break
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请重试'
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
