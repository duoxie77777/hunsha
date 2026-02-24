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
            v-for="chat in filteredChats"
            :key="chat.phone"
            class="session-item"
            :class="{ active: activePhone === chat.phone }"
            @click="selectChat(chat.phone)"
          >
            <div class="session-avatar">
              <img v-if="chat.user.avatar" :src="chat.user.avatar" />
              <el-icon v-else :size="20" color="#fff"><User /></el-icon>
              <span v-if="chat.unreadAdmin" class="unread-dot">{{ chat.unreadAdmin > 9 ? '9+' : chat.unreadAdmin }}</span>
            </div>
            <div class="session-info">
              <div class="session-top">
                <span class="session-name">{{ chat.user.nickname }}</span>
                <span class="session-time">{{ formatTime(chat.lastTime) }}</span>
              </div>
              <p class="session-preview">{{ previewMsg(chat) }}</p>
            </div>
          </div>
          <div v-if="!filteredChats.length" class="empty-sessions">
            <el-icon :size="40" color="#ddd"><ChatDotRound /></el-icon>
            <p>暂无会话</p>
          </div>
        </div>
      </div>

      <!-- 右侧聊天区 -->
      <div class="chat-main" v-if="activePhone && activeChat">
        <!-- 聊天头部 -->
        <div class="chat-main-header">
          <div class="chat-user-info">
            <div class="user-avatar-sm">
              <img v-if="activeChat.user.avatar" :src="activeChat.user.avatar" />
              <el-icon v-else :size="20" color="#fff"><User /></el-icon>
            </div>
            <div>
              <h4>{{ activeChat.user.nickname }}</h4>
              <span class="user-phone-sm">{{ activeChat.user.phone }}</span>
            </div>
          </div>
          <div class="chat-main-actions">
            <el-tooltip content="查看客户订单">
              <button class="action-btn" @click="viewUserOrders">
                <el-icon :size="18"><Document /></el-icon>
              </button>
            </el-tooltip>
          </div>
        </div>

        <!-- 消息区域 -->
        <div class="chat-messages" ref="messagesRef">
          <div class="date-divider"><span>{{ todayStr }}</span></div>
          <div
            v-for="(msg, idx) in activeChat.messages"
            :key="idx"
            class="chat-msg"
            :class="msg.from === 'user' ? 'from-user' : 'from-admin'"
          >
            <div class="msg-avatar-wrap">
              <template v-if="msg.from === 'user'">
                <div class="msg-avatar customer">
                  <img v-if="activeChat.user.avatar" :src="activeChat.user.avatar" />
                  <el-icon v-else :size="16" color="#fff"><User /></el-icon>
                </div>
              </template>
              <template v-else>
                <div class="msg-avatar admin">
                  <el-icon :size="16" color="#fff"><Headset /></el-icon>
                </div>
              </template>
            </div>
            <div class="msg-body" :class="msg.from === 'user' ? 'from-user' : 'from-admin'">
              <div class="msg-bubble" :class="msg.from === 'user' ? 'user-bubble' : 'admin-bubble'">
                <!-- 订单卡片 -->
                <div v-if="msg.type === 'order'" class="order-card-inline">
                  <div class="oc-head">
                    <el-icon :size="14"><Document /></el-icon>
                    <span>预约订单</span>
                  </div>
                  <div class="oc-row"><span>订单号</span><strong>{{ msg.order?.id }}</strong></div>
                  <div class="oc-row"><span>套餐</span><strong>{{ msg.order?.pkgLabel }}</strong></div>
                  <div class="oc-row"><span>日期</span><strong>{{ msg.order?.date || '待定' }}</strong></div>
                  <div class="oc-row"><span>风格</span><strong>{{ msg.order?.styles || '未选择' }}</strong></div>
                </div>
                <div v-else v-html="msg.text"></div>
              </div>
              <span class="msg-time-sm">{{ msg.time }}</span>
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
            :disabled="!inputText.trim()"
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

    <!-- 客户订单抽屉 -->
    <el-drawer v-model="orderDrawerVisible" title="客户订单" size="400px" direction="rtl">
      <div v-if="userOrders.length" class="drawer-orders">
        <div v-for="o in userOrders" :key="o.id" class="drawer-order-card">
          <div class="doc-header">
            <span class="doc-id">{{ o.id }}</span>
            <el-tag :type="statusType(o.status)" size="small" round>{{ o.status }}</el-tag>
          </div>
          <div class="doc-row"><span>套餐</span><span>{{ o.pkgLabel }}</span></div>
          <div class="doc-row"><span>日期</span><span>{{ o.date }}</span></div>
          <div class="doc-row"><span>风格</span><span>{{ o.styles || '未选择' }}</span></div>
          <div class="doc-row"><span>创建</span><span>{{ o.createTime }}</span></div>
          <div class="doc-actions" v-if="o.status === '待确认'">
            <el-button type="primary" size="small" @click="confirmFromDrawer(o)">确认订单</el-button>
          </div>
        </div>
      </div>
      <div v-else class="drawer-empty">
        <p>该客户暂无订单</p>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { Search, User, ChatDotRound, Document, Headset, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { chatStore } from '../../stores/chat'
import { userStore } from '../../stores/user'
import { useRoute } from 'vue-router'

const route = useRoute()
const messagesRef = ref(null)
const searchText = ref('')
const activePhone = ref('')
const inputText = ref('')
const orderDrawerVisible = ref(false)

const quickReplies = [
  '好的，已收到~',
  '稍等，我帮您查一下',
  '已为您安排好档期',
  '欢迎随时咨询！',
  '好的，我帮您备注',
]

const filteredChats = computed(() => {
  let list = chatStore.chatListSorted
  if (searchText.value.trim()) {
    const kw = searchText.value.trim().toLowerCase()
    list = list.filter(c =>
      c.user.nickname?.toLowerCase().includes(kw) ||
      c.phone?.includes(kw)
    )
  }
  return list
})

const activeChat = computed(() => {
  if (!activePhone.value) return null
  return chatStore.getChat(activePhone.value)
})

const userOrders = computed(() => {
  if (!activePhone.value) return []
  return (userStore.orders || []).filter(o => o.phone === activePhone.value)
})

const todayStr = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

function selectChat(phone) {
  activePhone.value = phone
  chatStore.clearUnreadAdmin(phone)
  scrollToBottom()
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function formatTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }
  return `${d.getMonth() + 1}/${d.getDate()}`
}

function previewMsg(chat) {
  const msgs = chat.messages
  if (!msgs || !msgs.length) return '暂无消息'
  const last = msgs[msgs.length - 1]
  if (last.type === 'order') return '[预约订单]'
  const prefix = last.from === 'cs' ? '[客服] ' : ''
  return prefix + (last.text || '').replace(/<[^>]+>/g, '').substring(0, 28)
}

function getTime() {
  const now = new Date()
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
}

function sendAdminMessage() {
  const text = inputText.value.trim()
  if (!text || !activePhone.value) return

  chatStore.addMessage(activePhone.value, {
    from: 'cs',
    text,
    time: getTime()
  })
  inputText.value = ''
  scrollToBottom()
}

function quickReply(text) {
  inputText.value = text
  sendAdminMessage()
}

function viewUserOrders() {
  orderDrawerVisible.value = true
}

function statusType(s) {
  return { '待确认': 'warning', '已确认': 'success', '已完成': 'info', '已取消': 'danger' }[s] || 'info'
}

function confirmFromDrawer(order) {
  order.status = '已确认'
  localStorage.setItem('weiai_orders', JSON.stringify(userStore.orders))
  ElMessage.success('订单已确认')
  // 发一条自动消息
  chatStore.addMessage(activePhone.value, {
    from: 'cs',
    text: `您的订单 <strong>${order.id}</strong> 已确认，我们会在拍摄前一天与您联系 😊`,
    time: getTime()
  })
  scrollToBottom()
}

// 自动选中 URL query 中的用户
onMounted(() => {
  if (route.query.user) {
    activePhone.value = route.query.user
    chatStore.clearUnreadAdmin(route.query.user)
    scrollToBottom()
  }
})

// 监听消息变化自动滚底
watch(() => activeChat.value?.messages?.length, () => {
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

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  background: #fff;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: var(--secondary-color);
  color: var(--secondary-color);
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

.date-divider { text-align: center; padding: 6px 0; }
.date-divider span {
  background: #e8e8e8;
  color: #999;
  font-size: 0.72rem;
  padding: 3px 12px;
  border-radius: 10px;
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
.order-card-inline {
  min-width: 200px;
}

.oc-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  font-size: 0.85rem;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.user-bubble .oc-head { border-bottom-color: #f0f0f0; }

.oc-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  margin-top: 4px;
}

.oc-row span:first-child { opacity: 0.7; }

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

/* 抽屉 */
.drawer-orders {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.drawer-order-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 16px;
}

.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.doc-id {
  font-family: monospace;
  font-weight: 700;
  color: #667eea;
}

.doc-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  padding: 4px 0;
  color: #666;
}

.doc-row span:last-child { font-weight: 600; color: #333; }

.doc-actions { margin-top: 12px; text-align: right; }

.drawer-empty {
  text-align: center;
  padding: 40px;
  color: #ccc;
}

@media (max-width: 768px) {
  .chat-sidebar { width: 80px; }
  .session-info { display: none; }
  .sidebar-search { padding: 8px; }
}
</style>
