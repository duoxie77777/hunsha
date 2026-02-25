<template>
  <!-- 悬浮客服按钮 -->
  <Transition name="fab-pop">
    <div v-show="!chatOpen" class="fab-wrapper" @click="openChat">
      <div class="fab-btn">
        <el-icon :size="26"><ChatDotRound /></el-icon>
        <span v-if="unreadCount > 0" class="fab-badge">{{ unreadCount > 9 ? '9+' : unreadCount }}</span>
      </div>
      <div class="fab-pulse"></div>
      <span class="fab-label">在线客服</span>
    </div>
  </Transition>

  <!-- 聊天面板 -->
  <Transition name="chat-slide">
    <div v-show="chatOpen" class="chat-panel">
      <!-- 头部 -->
      <div class="chat-panel-header">
        <div class="chat-header-left">
          <div class="cs-avatar">
            <el-icon :size="24" color="#fff"><Headset /></el-icon>
          </div>
          <div class="chat-header-info">
            <h4>唯爱专属客服</h4>
            <div class="status-row">
              <span class="online-dot"></span>
              <span class="online-text">在线</span>
            </div>
          </div>
        </div>
        <div class="chat-header-actions">
          <button class="header-action-btn" title="最小化" @click="minimizeChat">
            <el-icon :size="18"><Minus /></el-icon>
          </button>
          <button class="header-action-btn close-action" title="关闭" @click="closeChat">
            <el-icon :size="18"><Close /></el-icon>
          </button>
        </div>
      </div>

      <!-- 快捷操作栏 -->
      <div class="quick-actions">
        <button class="quick-btn" @click="quickSend('我想了解拍摄套餐')">
          <el-icon :size="14"><PriceTag /></el-icon> 了解套餐
        </button>
        <button class="quick-btn" @click="quickSend('我想预约拍摄')">
          <el-icon :size="14"><Calendar /></el-icon> 预约拍摄
        </button>
        <button class="quick-btn" @click="quickSend('我想看看作品案例')">
          <el-icon :size="14"><Picture /></el-icon> 作品案例
        </button>
        <button class="quick-btn" @click="quickSend('有什么优惠活动吗')">
          <el-icon :size="14"><Present /></el-icon> 优惠活动
        </button>
      </div>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messagesRef">
        <div v-if="loading" class="loading-tip">加载中...</div>

        <div v-if="!loading && messages.length === 0" class="welcome-msg">
          <div class="msg-avatar">
            <el-icon :size="18" color="#fff"><Headset /></el-icon>
          </div>
          <div class="msg-bubble cs">
            您好！我是唯爱婚纱摄影的客服 ❤️<br/>有任何问题都可以发消息给我们哦~
          </div>
        </div>

        <div
          v-for="msg in messages"
          :key="msg.id"
          class="chat-msg"
          :class="msg.senderId === 0 ? 'cs' : 'user'"
        >
          <div class="msg-avatar" v-if="msg.senderId === 0">
            <el-icon :size="18" color="#fff"><Headset /></el-icon>
          </div>
          <div class="msg-avatar user-avatar" v-else>
            <template v-if="userStore.user?.avatar">
              <img :src="userStore.user.avatar" class="avatar-img" />
            </template>
            <el-icon v-else :size="18" color="#fff"><User /></el-icon>
          </div>

          <div class="msg-content" :class="msg.senderId === 0 ? 'cs' : 'user'">
            <span class="msg-sender" v-if="msg.senderId === 0">客服</span>
            <div class="msg-bubble" :class="msg.senderId === 0 ? 'cs' : 'user'">
              <div v-if="msg.type === 'order'" class="order-card-msg">
                <div class="order-card-header-bar">
                  <el-icon :size="16"><Document /></el-icon>
                  <span>预约订单</span>
                </div>
                <div class="order-card-body">
                  <p>{{ msg.content }}</p>
                </div>
              </div>
              <div v-else class="msg-text" v-html="msg.content"></div>
            </div>
            <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <div class="input-row">
          <el-input
            v-model="inputText"
            :autosize="{ minRows: 1, maxRows: 4 }"
            type="textarea"
            placeholder="输入消息，按 Enter 发送..."
            resize="none"
            @keydown.enter.exact.prevent="sendMessage"
          />
          <button
            class="send-btn"
            :class="{ active: inputText.trim() }"
            :disabled="!inputText.trim() || sending"
            @click="sendMessage"
          >
            <el-icon :size="20"><Promotion /></el-icon>
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { Headset, User, Document, ChatDotRound, Close, Minus, PriceTag, Calendar, Picture, Present, Promotion } from '@element-plus/icons-vue'
import { userStore } from '../stores/user'
import { sendMessageApi, getMessagesApi, getUnreadCountApi } from '../api/chat'

const messagesRef = ref(null)
const inputText = ref('')
const chatOpen = ref(false)
const loading = ref(false)
const sending = ref(false)
const messages = ref([])
const unreadCount = ref(0)
let pollTimer = null

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

async function loadMessages() {
  if (!userStore.user) return
  loading.value = true
  try {
    const res = await getMessagesApi()
    messages.value = res.data || []
    unreadCount.value = 0
  } catch (e) {
    console.error('加载消息失败', e)
  }
  loading.value = false
  scrollToBottom()
}

async function loadUnread() {
  if (!userStore.user) return
  try {
    const res = await getUnreadCountApi()
    unreadCount.value = res.data || 0
  } catch (e) {}
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value || !userStore.user) return

  sending.value = true
  try {
    const res = await sendMessageApi({ content: text, type: 'text' })
    messages.value.push(res.data)
    inputText.value = ''
    scrollToBottom()
  } catch (e) {
    console.error('发送失败', e)
  }
  sending.value = false
}

async function sendOrderCard(order) {
  if (!userStore.user) return
  const content = `预约订单: ${order.pkgLabel || order.packageName || ''}, 日期: ${order.date || order.shootDate || '待定'}`
  try {
    const res = await sendMessageApi({ content, type: 'order' })
    messages.value.push(res.data)
    scrollToBottom()
  } catch (e) {
    console.error('发送订单卡片失败', e)
  }
}

function quickSend(text) {
  inputText.value = text
  sendMessage()
}

function openChat() {
  chatOpen.value = true
  loadMessages()
  startPoll()
}

function minimizeChat() {
  chatOpen.value = false
  stopPoll()
}

function closeChat() {
  chatOpen.value = false
  userStore.showChat = false
  stopPoll()
}

// 轮询新消息（每1秒）
function startPoll() {
  stopPoll()
  pollTimer = setInterval(async () => {
    if (!chatOpen.value || !userStore.user) return
    try {
      const res = await getMessagesApi()
      messages.value = res.data || []
      scrollToBottom()
    } catch (e) {}
  }, 1000)
}

function stopPoll() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 未登录时定期检查未读数
let unreadTimer = null
onMounted(() => {
  loadUnread()
  unreadTimer = setInterval(() => {
    if (!chatOpen.value) loadUnread()
  }, 15000)
})

onUnmounted(() => {
  stopPoll()
  if (unreadTimer) clearInterval(unreadTimer)
})

// 监听 showChat（预约后自动弹出）
watch(() => userStore.showChat, (val) => {
  if (val && userStore.user) {
    chatOpen.value = true
    loadMessages()
    if (userStore.currentOrder) {
      setTimeout(() => {
        sendOrderCard(userStore.currentOrder)
      }, 500)
    }
    startPoll()
  }
})

watch(() => messages.value.length, () => {
  scrollToBottom()
})
</script>

<style scoped>
/* ========== 悬浮按钮 ========== */
.fab-wrapper {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 0;
  cursor: pointer;
}

.fab-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 6px 24px rgba(241, 174, 181, 0.5);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 2;
}

.fab-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 30px rgba(241, 174, 181, 0.65);
}

.fab-badge {
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

.fab-pulse {
  position: absolute;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(241, 174, 181, 0.3);
  animation: fabPulse 2s infinite;
  z-index: 1;
  right: 0;
}

@keyframes fabPulse {
  0% { transform: scale(1); opacity: 0.6; }
  100% { transform: scale(1.8); opacity: 0; }
}

.fab-label {
  position: absolute;
  right: 66px;
  background: #fff;
  color: var(--dark-color);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s;
  pointer-events: none;
}

.fab-wrapper:hover .fab-label {
  opacity: 1;
  transform: translateX(0);
}

/* ========== 聊天面板 ========== */
.chat-panel {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 420px;
  height: 640px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.04);
  z-index: 2001;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  color: #fff;
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cs-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.chat-header-info h4 {
  font-size: 1.05rem;
  font-weight: 700;
  margin: 0 0 2px;
  color: #fff;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 5px;
}

.online-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #a0ffa0;
  display: inline-block;
  box-shadow: 0 0 6px rgba(160, 255, 160, 0.6);
}

.online-text {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.85);
}

.chat-header-actions {
  display: flex;
  gap: 4px;
}

.header-action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.header-action-btn:hover { background: rgba(255, 255, 255, 0.35); }
.close-action:hover { background: rgba(220, 53, 69, 0.6); }

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  overflow-x: auto;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.quick-actions::-webkit-scrollbar { height: 0; }

.quick-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 20px;
  border: 1px solid #eee;
  background: #fff;
  font-size: 0.8rem;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.quick-btn:hover {
  border-color: var(--secondary-color);
  color: var(--secondary-color);
  background: #fdf5f6;
}

/* ========== 消息区域 ========== */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f8f9fb;
}

.chat-messages::-webkit-scrollbar { width: 5px; }
.chat-messages::-webkit-scrollbar-track { background: transparent; }
.chat-messages::-webkit-scrollbar-thumb { background: #ddd; border-radius: 3px; }

.loading-tip {
  text-align: center;
  color: #ccc;
  font-size: 0.85rem;
  padding: 20px;
}

.welcome-msg {
  display: flex;
  gap: 10px;
  align-self: flex-start;
}

.welcome-msg .msg-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  background: #fff;
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  font-size: 0.9rem;
  line-height: 1.6;
}

.chat-msg {
  display: flex;
  gap: 10px;
  max-width: 82%;
  animation: msgFadeIn 0.3s ease;
}

@keyframes msgFadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-msg.cs { align-self: flex-start; }
.chat-msg.user { align-self: flex-end; flex-direction: row-reverse; }

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.msg-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.msg-avatar.user-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.msg-content {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.msg-content.user { align-items: flex-end; }

.msg-sender {
  font-size: 0.72rem;
  color: #aaa;
  margin-bottom: 1px;
  padding-left: 2px;
}

.msg-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 0.9rem;
  line-height: 1.6;
  word-break: break-word;
}

.msg-bubble.cs {
  background: #fff;
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.msg-bubble.user {
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(241, 174, 181, 0.3);
}

.msg-time {
  font-size: 0.68rem;
  color: #c0c0c0;
  padding: 0 2px;
}

.msg-content.user .msg-time { text-align: right; }

/* 订单卡片 */
.order-card-msg { min-width: 240px; }

.order-card-header-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.25);
  color: #fff;
}

.order-card-body p {
  margin: 0;
  font-size: 0.85rem;
  line-height: 1.5;
}

/* ========== 输入区域 ========== */
.chat-input-area {
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 10px 16px 14px;
}

.input-row :deep(.el-textarea__inner) {
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 0.9rem;
  border-color: #e8e8e8;
  box-shadow: none !important;
  font-family: inherit;
}

.input-row :deep(.el-textarea__inner:focus) {
  border-color: var(--secondary-color);
}

.send-btn {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: none;
  background: #e8e8e8;
  color: #bbb;
  cursor: not-allowed;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
}

.send-btn.active {
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  color: #fff;
  cursor: pointer;
  box-shadow: 0 3px 12px rgba(241, 174, 181, 0.4);
}

.send-btn.active:hover { transform: scale(1.05); }

/* ========== 动画 ========== */
.fab-pop-enter-active,
.fab-pop-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fab-pop-enter-from,
.fab-pop-leave-to {
  opacity: 0;
  transform: scale(0.6);
}

.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .chat-panel {
    right: 0;
    bottom: 0;
    width: 100%;
    height: 100%;
    border-radius: 0;
  }

  .fab-wrapper {
    right: 16px;
    bottom: 16px;
  }

  .fab-label { display: none; }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .chat-panel {
    width: 380px;
    height: 580px;
  }
}
</style>
