import { reactive } from 'vue'
import { adminGetSessionsApi, adminGetUnreadApi } from '../api/chat'

export const chatStore = reactive({
  // 管理端：会话列表
  sessions: [],
  // 管理端：总未读数
  totalUnreadAdmin: 0,

  // 加载管理端会话列表
  async loadSessions() {
    try {
      const res = await adminGetSessionsApi()
      this.sessions = res.data || []
      this.totalUnreadAdmin = this.sessions.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
    } catch (e) {
      console.error('加载会话列表失败', e)
    }
  },

  // 加载管理端总未读数
  async loadUnreadCount() {
    try {
      const res = await adminGetUnreadApi()
      this.totalUnreadAdmin = res.data || 0
    } catch (e) {
      console.error('加载未读数失败', e)
    }
  },

  // 按最新消息排序的会话列表
  get chatListSorted() {
    return [...this.sessions].sort((a, b) => {
      if ((b.unreadCount || 0) !== (a.unreadCount || 0)) {
        return (b.unreadCount || 0) - (a.unreadCount || 0)
      }
      return new Date(b.lastTime || 0) - new Date(a.lastTime || 0)
    })
  }
})
