import { reactive } from 'vue'
import { loginApi, getUserInfoApi } from '../api/auth'

const ADMIN_KEY = 'weiai_admin'
const TOKEN_KEY = 'token'

function loadAdmin() {
  try {
    const data = localStorage.getItem(ADMIN_KEY)
    return data ? JSON.parse(data) : null
  } catch {
    return null
  }
}

export const adminStore = reactive({
  admin: loadAdmin(),

  get isLoggedIn() {
    return !!this.admin && !!localStorage.getItem(TOKEN_KEY)
  },

  // 登录 - 调用后端接口（管理员和普通用户共用登录接口，通过 role 区分）
  async login(username, password) {
    const res = await loginApi({ username, password })
    const data = res.data

    // 验证是否为管理员
    if (data.role !== 1) {
      throw new Error('该账号不是管理员')
    }

    // 保存 token
    localStorage.setItem(TOKEN_KEY, data.token)

    // 保存管理员信息
    this.admin = {
      id: data.id,
      username: data.username,
      name: data.nickname || data.username,
      role: '超级管理员',
      loginTime: Date.now()
    }
    localStorage.setItem(ADMIN_KEY, JSON.stringify(this.admin))

    return true
  },

  logout() {
    this.admin = null
    localStorage.removeItem(ADMIN_KEY)
    localStorage.removeItem(TOKEN_KEY)
  }
})
