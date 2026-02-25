import { reactive } from 'vue'
import { loginApi, registerApi, getUserInfoApi } from '../api/auth'
import { createOrderApi, getMyOrdersApi, cancelOrderApi } from '../api/order'

const TOKEN_KEY = 'token'
const USER_KEY = 'weiai_user'

function loadUser() {
  try {
    const data = localStorage.getItem(USER_KEY)
    return data ? JSON.parse(data) : null
  } catch {
    return null
  }
}

export const userStore = reactive({
  user: loadUser(),
  orders: [],
  showLogin: false,
  showBooking: false,
  showChat: false,
  currentOrder: null,

  get isLoggedIn() {
    return !!this.user && !!localStorage.getItem(TOKEN_KEY)
  },

  // 登录 - 调用后端接口
  async login(username, password) {
    const res = await loginApi({ username, password })
    const data = res.data

    // 保存 token
    localStorage.setItem(TOKEN_KEY, data.token)

    // 保存用户信息
    this.user = {
      id: data.id,
      username: data.username,
      nickname: data.nickname,
      phone: data.phone,
      email: data.email,
      avatar: data.avatar,
      role: data.role
    }
    localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    this.showLogin = false

    // 登录后拉取订单
    this.fetchOrders()

    return data
  },

  // 注册 - 调用后端接口
  async register(data) {
    await registerApi(data)
  },

  // 获取用户信息 - 调用后端接口
  async fetchUserInfo() {
    try {
      const res = await getUserInfoApi()
      this.user = res.data
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    } catch {
      // token 失效，清除登录状态
      this.logout()
    }
  },

  // 获取我的订单列表
  async fetchOrders() {
    if (!this.isLoggedIn) return
    try {
      const res = await getMyOrdersApi()
      this.orders = res.data || []
    } catch {
      this.orders = []
    }
  },

  logout() {
    this.user = null
    this.orders = []
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },

  updateProfile(profile) {
    if (!this.user) return
    Object.assign(this.user, profile)
    localStorage.setItem(USER_KEY, JSON.stringify(this.user))
  },

  // 创建订单 - 调用后端接口
  async addOrder(orderData) {
    const res = await createOrderApi(orderData)
    const newOrder = res.data
    this.orders.unshift(newOrder)
    this.currentOrder = newOrder
    return newOrder
  },

  // 取消订单 - 调用后端接口
  async cancelOrder(orderId) {
    await cancelOrderApi(orderId)
    const order = this.orders.find(o => o.id === orderId)
    if (order) {
      order.status = '已取消'
    }
  },

  requireLogin(callback) {
    if (this.isLoggedIn) {
      callback?.()
    } else {
      this._pendingCallback = callback
      this.showLogin = true
    }
  },

  onLoginSuccess() {
    if (this._pendingCallback) {
      this._pendingCallback()
      this._pendingCallback = null
    }
  },

  openBooking() {
    this.requireLogin(() => {
      this.showBooking = true
    })
  },

  openChat(order) {
    this.currentOrder = order || this.currentOrder
    this.showChat = true
  }
})
