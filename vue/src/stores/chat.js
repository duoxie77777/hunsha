import { reactive } from 'vue'

const CHAT_KEY = 'weiai_chats'

function loadChats() {
  try {
    const data = localStorage.getItem(CHAT_KEY)
    return data ? JSON.parse(data) : {}
  } catch {
    return {}
  }
}

function save(chats) {
  localStorage.setItem(CHAT_KEY, JSON.stringify(chats))
}

// chats: { [userPhone]: { user: { phone, nickname, avatar }, messages: [...], unreadAdmin: 0, unreadUser: 0 } }
export const chatStore = reactive({
  chats: loadChats(),

  getChat(userPhone) {
    return this.chats[userPhone] || null
  },

  ensureChat(userPhone, userInfo) {
    if (!this.chats[userPhone]) {
      this.chats[userPhone] = {
        user: { phone: userPhone, nickname: userInfo?.nickname || userPhone, avatar: userInfo?.avatar || '' },
        messages: [],
        unreadAdmin: 0,
        unreadUser: 0,
        lastTime: Date.now()
      }
    } else if (userInfo) {
      Object.assign(this.chats[userPhone].user, userInfo)
    }
    save(this.chats)
    return this.chats[userPhone]
  },

  addMessage(userPhone, msg, userInfo) {
    const chat = this.ensureChat(userPhone, userInfo)
    chat.messages.push({ ...msg, timestamp: Date.now() })
    chat.lastTime = Date.now()
    if (msg.from === 'user') {
      chat.unreadAdmin = (chat.unreadAdmin || 0) + 1
    } else {
      chat.unreadUser = (chat.unreadUser || 0) + 1
    }
    save(this.chats)
  },

  clearUnreadAdmin(userPhone) {
    const chat = this.chats[userPhone]
    if (chat) {
      chat.unreadAdmin = 0
      save(this.chats)
    }
  },

  clearUnreadUser(userPhone) {
    const chat = this.chats[userPhone]
    if (chat) {
      chat.unreadUser = 0
      save(this.chats)
    }
  },

  get totalUnreadAdmin() {
    return Object.values(this.chats).reduce((sum, c) => sum + (c.unreadAdmin || 0), 0)
  },

  get chatListSorted() {
    return Object.entries(this.chats)
      .map(([phone, data]) => ({ phone, ...data }))
      .sort((a, b) => (b.lastTime || 0) - (a.lastTime || 0))
  }
})
