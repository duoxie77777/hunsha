<template>
  <div class="chat-page">
    <div class="chat-layout">
      <!-- 左侧会话列表 -->
      <div class="chat-sidebar">
        <div class="sidebar-search">
          <el-input v-model="searchText" placeholder="搜索客户" clearable size="default" :prefix-icon="Search" />
        </div>
        <div class="session-list">
          <div
            v-for="session in filteredSessions"
            :key="session.userId"
            class="session-item"
            :class="{ active: activeUserId === session.userId }"
            @click="selectSession(session)"
          >
            <div class="session-avatar">
              <img v-if="session.avatar" :src="session.avatar" />
              <el-icon v-else :size="20" color="#fff"><User /></el-icon>
              <span v-if="session.unreadCount" class="unread-dot">{{ session.unreadCount > 9 ? '9+' : session.unreadCount }}</span>
            </div>
            <div class="session-info">
              <div class="session-top">
                <span class="session-name">{{ session.nickname }}</span>
                <span class="session-time">{{ formatTime(session.lastTime) }}</span>
              </div>
              <p class="session-preview">{{ previewMsg(session) }}</p>
            </div>
          </div>
          <div v-if="sessionsLoading" class="empty-sessions">
            <p>加载中...</p>
          </div>
          <div v-else-if="!filteredSessions.length" class="empty-sessions">
            <el-icon :size="40" color="#ddd"><ChatDotRound /></el-icon>
            <p>暂无会话</p>
          </div>
        </div>
      </div>

      <!-- 右侧聊天区 -->
      <div class="chat-main" v-if="activeUserId && activeSession">
        <!-- 聊天头部 -->
        <div class="chat-main-header">
          <div class="chat-user-info">
            <div class="user-avatar-sm">
              <img v-if="activeSession.avatar" :src="activeSession.avatar" />
              <el-icon v-else :size="20" color="#fff"><User /></el-icon>
            </div>
            <div>
              <h4>{{ activeSession.nickname }}</h4>
              <span class="user-phone-sm">{{ activeSession.phone }}</span>
            </div>
          </div>
        </div>

        <!-- 消息区域 -->
        <div class="chat-messages" ref="messagesRef">
          <div v-if="messagesLoading" class="loading-tip">加载中...</div>
          <div
            v-for="msg in chatMessages"
            :key="msg.id"
            class="chat-msg"
            :class="msg.senderId === 0 ? 'from-admin' : 'from-user'"
          >
            <div class="msg-avatar-wrap">
              <template v-if="msg.senderId !== 0">
                <div class="msg-avatar customer">
                  <img v-if="activeSession.avatar" :src="activeSession.avatar" />
                  <el-icon v-else :size="16" color="#fff"><User /></el-icon>
                </div>
              </template>
              <template v-else>
                <div class="msg-avatar admin">
                  <el-icon :size="16" color="#fff"><Headset /></el-icon>
                </div>
              </template>
            </div>
            <div class="msg-body" :class="msg.senderId === 0 ? 'from-admin' : 'from-user'">
              <div class="msg-bubble" :class="msg.senderId === 0 ? 'admin-bubble' : 'user-bubble'">
                <div v-if="msg.type === 'order'" class="order-card-inline">
                  <div class="oc-head">
                    <el-icon :size="14"><Document /></el-icon>
                    <span>预约订单</span>
                  </div>
                  <p class="oc-content">{{ msg.content }}</p>
                </div>
                <div v-else v-html="msg.content"></div>
              </div>
              <span class="msg-time-sm">{{ formatMsgTime(msg.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 快捷回复 -->
        <div class="quick-replies">
          <button v-for="(r, i) in quickReplies" :key="i" class="qr-btn" @click="quickReply(r)">{{ r }}</button>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-bar">
          <el-input
            v-model="inputText"
            :autosize="{ minRows: 1, maxRows: 4 }"
            type="textarea"
            placeholder="输入回复内容..."
            resize="none"
            @keydown.enter.exact.prevent="sendAdminMessage"
          />
          <el-button
            type="primary"
            :disabled="!inputText.trim() || sending"
            class="send-msg-btn"
            @click="sendAdminMessage"
          >
            <el-icon :size="18"><Promotion /></el-icon>
            发送
          </el-button>
        </div>
      </div>

      <!-- 未选中 -->
      <div v-else class="chat-empty">
        <el-icon :size="64" color="#e0e0e0"><ChatDotRound /></el-icon>
        <h3>选择一个会话开始聊天</h3>
        <p>左侧选择客户会话，在这里回复消息</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { Search, User, ChatDotRound, Document, Headset, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { chatStore } from '../../stores/chat'
import { adminGetSessionsApi, adminGetMessagesApi, adminSendMessageApi } from '../../api/chat'
import { useRoute } from 'vue-router'

const route = useRoute()
const messagesRef = ref(null)
const searchText = ref('')
const activeUserId = ref(null)
const activeSession = ref(null)
const inputText = ref('')
const sending = ref(false)
const sessionsLoading = ref(false)
const messagesLoading = ref(false)
const chatMessages = ref([])
const sessions = ref([])
let pollTimer = null

const quickReplies = [
  '好的，已收到~',
  '稍等，我帮您查一下',
  '已为您安排好档期',
  '欢迎随时咨询！',
  '好的，我帮您备注',
]

const filteredSessions = computed(() => {
  let list = sessions.value
  if (searchText.value.trim()) {
    const kw = searchText.value.trim().toLowerCase()
    list = list.filter(s =>
      s.nickname?.toLowerCase().includes(kw) ||
      s.phone?.includes(kw)
    )
  }
  return list
})

async function loadSessions() {
  sessionsLoading.value = true
  try {
    const res = await adminGetSessionsApi()
    sessions.value = res.data || []
    chatStore.totalUnreadAdmin = sessions.value.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
  } catch (e) {
    console.error('加载会话列表失败', e)
  }
  sessionsLoading.value = false
}

async function loadMessages(userId) {
  messagesLoading.value = true
  try {
    const res = await adminGetMessagesApi(userId)
    chatMessages.value = res.data || []
    // 更新会话未读数
    const session = sessions.value.find(s => s.userId === userId)
    if (session) {
      session.unreadCount = 0
    }
    chatStore.totalUnreadAdmin = sessions.value.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
  } catch (e) {
    console.error('加载消息失败', e)
  }
  messagesLoading.value = false
  scrollToBottom()
}

function selectSession(session) {
  activeUserId.value = session.userId
  activeSession.value = session
  loadMessages(session.userId)
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }
  return `${d.getMonth() + 1}/${d.getDate()}`
}

function formatMsgTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function previewMsg(session) {
  if (!session.lastMessage) return '暂无消息'
  if (session.lastMessageType === 'order') return '[预约订单]'
  return session.lastMessage.replace(/<[^>]+>/g, '').substring(0, 28)
}

async function sendAdminMessage() {
  const text = inputText.value.trim()
  if (!text || !activeUserId.value || sending.value) return

  sending.value = true
  try {
    const res = await adminSendMessageApi(activeUserId.value, { content: text, type: 'text' })
    chatMessages.value.push(res.data)
    inputText.value = ''
    scrollToBottom()
    // 更新会话列表中的最后一条消息
    const session = sessions.value.find(s => s.userId === activeUserId.value)
    if (session) {
      session.lastMessage = text
      session.lastMessageType = 'text'
      session.lastTime = new Date().toISOString()
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
  sending.value = false
}

function quickReply(text) {
  inputText.value = text
  sendAdminMessage()
}

// 轮询：刷新当前会话消息 + 会话列表
function startPoll() {
  stopPoll()
  pollTimer = setInterval(async () => {
    // 刷新会话列表
    try {
      const res = await adminGetSessionsApi()
      sessions.value = res.data || []
      chatStore.totalUnreadAdmin = sessions.value.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
    } catch (e) {}
    // 如果有选中的会话，也刷新消息
    if (activeUserId.value) {
      try {
        const res = await adminGetMessagesApi(activeUserId.value)
        chatMessages.value = res.data || []
        const session = sessions.value.find(s => s.userId === activeUserId.value)
        if (session) session.unreadCount = 0
        chatStore.totalUnreadAdmin = sessions.value.reduce((sum, s) => sum + (s.unreadCount || 0), 0)
        scrollToBottom()
      } catch (e) {}
    }
  }, 1000)
}

function stopPoll() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

onMounted(async () => {
  await loadSessions()
  // 自动选中 URL query 中的用户
  if (route.query.user) {
    const userId = Number(route.query.user)
    const session = sessions.value.find(s => s.userId === userId)
    if (session) {
      selectSession(session)
    }
  }
  startPoll()
})

onUnmounted(() => {
  stopPoll()
})

watch(() => chatMessages.value.length, () => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-page {
  height: calc(100vh - 64px - 48px);
}

.chat-layout {
  display: flex;
  height: 100%;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 左侧会话 */
.chat-sidebar {
  width: 300px;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-search {
  padding: 14px;
  border-bottom: 1px solid #f0f0f0;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid #fafafa;
  transition: background 0.15s;
}

.session-item:hover { background: #f8f9fb; }

.session-item.active {
  background: linear-gradient(135deg, rgba(241, 174, 181, 0.1), rgba(232, 138, 148, 0.06));
  border-left: 3px solid var(--secondary-color);
}

.session-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  overflow: visible;
}

.session-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.unread-dot {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #e74c3c;
  color: #fff;
  font-size: 0.65rem;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  font-weight: 700;
  border: 2px solid #fff;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-name {
  font-weight: 700;
  font-size: 0.92rem;
  color: #333;
}

.session-time {
  font-size: 0.72rem;
  color: #bbb;
}

.session-preview {
  font-size: 0.8rem;
  color: #999;
  margin-top: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-sessions {
  text-align: center;
  padding: 60px 20px;
}

.empty-sessions p { color: #ccc; margin-top: 8px; }

/* 右侧聊天 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar-sm {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar-sm img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-user-info h4 {
  font-size: 1rem;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.user-phone-sm {
  font-size: 0.78rem;
  color: #999;
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background: #f8f9fb;
}

.chat-messages::-webkit-scrollbar { width: 5px; }
.chat-messages::-webkit-scrollbar-thumb { background: #ddd; border-radius: 3px; }

.loading-tip {
  text-align: center;
  color: #ccc;
  font-size: 0.85rem;
  padding: 20px;
}

.chat-msg {
  display: flex;
  gap: 10px;
  max-width: 70%;
  animation: msgIn 0.25s ease;
}

@keyframes msgIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-msg.from-user { align-self: flex-start; }
.chat-msg.from-admin { align-self: flex-end; flex-direction: row-reverse; }

.msg-avatar-wrap { flex-shrink: 0; }

.msg-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.msg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.msg-avatar.customer { background: linear-gradient(135deg, #667eea, #764ba2); }
.msg-avatar.admin { background: linear-gradient(135deg, #f1aeb5, #e88a94); }

.msg-body {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.msg-body.from-admin { align-items: flex-end; }

.msg-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 0.9rem;
  line-height: 1.6;
  word-break: break-word;
}

.user-bubble {
  background: #fff;
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.admin-bubble {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  color: #fff;
  border-top-right-radius: 4px;
}

.msg-time-sm {
  font-size: 0.68rem;
  color: #c0c0c0;
  padding: 0 4px;
}

/* 订单卡片 */
.order-card-inline { min-width: 200px; }

.oc-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  font-size: 0.85rem;
  margin-bottom: 6px;
  padding-bottom: 6px;
  border-bottom: 1px solid #f0f0f0;
}

.oc-content {
  font-size: 0.82rem;
  color: #555;
  margin: 0;
  line-height: 1.5;
}

/* 快捷回复 */
.quick-replies {
  display: flex;
  gap: 8px;
  padding: 8px 20px;
  overflow-x: auto;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.quick-replies::-webkit-scrollbar { height: 0; }

.qr-btn {
  padding: 5px 14px;
  border-radius: 16px;
  border: 1px solid #e0e0e0;
  background: #fff;
  font-size: 0.78rem;
  color: #666;
  cursor: pointer;
  white-space: nowrap;
  font-weight: 600;
  transition: all 0.2s;
}

.qr-btn:hover {
  border-color: #1a1a2e;
  color: #1a1a2e;
  background: #f0f2f5;
}

/* 输入 */
.chat-input-bar {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 12px 20px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.chat-input-bar :deep(.el-textarea__inner) {
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 0.9rem;
  font-family: inherit;
  border-color: #e0e0e0;
  box-shadow: none !important;
}

.chat-input-bar :deep(.el-textarea__inner:focus) {
  border-color: #1a1a2e;
}

.send-msg-btn {
  height: 42px;
  padding: 0 20px;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: none !important;
  font-weight: 600;
  flex-shrink: 0;
}

.send-msg-btn:hover { opacity: 0.85; }

/* 空状态 */
.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.chat-empty h3 { color: #999; font-size: 1.1rem; }
.chat-empty p { color: #ccc; font-size: 0.9rem; }

@media (max-width: 768px) {
  .chat-sidebar { width: 80px; }
  .session-info { display: none; }
  .sidebar-search { padding: 8px; }
}
</style>
