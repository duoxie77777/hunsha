<template>
  <!-- 悬浮客服按钮 -->
  <Transition name="fab-pop">
    <div v-show="!chatOpen" class="fab-wrapper" @click="openChat">
      <div class="fab-btn">
        <el-icon :size="26"><ChatDotRound /></el-icon>
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
              <span class="online-text">在线 · 平均响应 &lt; 1分钟</span>
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
        <!-- 日期分隔 -->
        <div class="date-divider">
          <span>{{ todayStr }}</span>
        </div>

        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          class="chat-msg"
          :class="msg.from"
        >
          <div class="msg-avatar" v-if="msg.from === 'cs'">
            <el-icon :size="18" color="#fff"><Headset /></el-icon>
          </div>
          <div class="msg-avatar user-avatar" v-else>
            <template v-if="userStore.user?.avatar">
              <img :src="userStore.user.avatar" class="avatar-img" />
            </template>
            <el-icon v-else :size="18" color="#fff"><User /></el-icon>
          </div>

          <div class="msg-content" :class="msg.from">
            <span class="msg-sender" v-if="msg.from === 'cs'">客服小爱</span>
            <div class="msg-bubble" :class="msg.from">
              <!-- 订单卡片 -->
              <div v-if="msg.type === 'order'" class="order-card-msg">
                <div class="order-card-header-bar">
                  <el-icon :size="16"><Document /></el-icon>
                  <span>预约订单</span>
                  <span class="order-status-tag">待确认</span>
                </div>
                <div class="order-card-body">
                  <div class="order-card-row">
                    <span class="label">订单号</span>
                    <span class="value highlight">{{ msg.order.id }}</span>
                  </div>
                  <div class="order-card-row">
                    <span class="label">套餐</span>
                    <span class="value">{{ msg.order.pkgLabel }}</span>
                  </div>
                  <div class="order-card-row">
                    <span class="label">日期</span>
                    <span class="value">{{ msg.order.date || '待定' }}</span>
                  </div>
                  <div class="order-card-row">
                    <span class="label">风格</span>
                    <span class="value">{{ msg.order.styles || '未选择' }}</span>
                  </div>
                </div>
              </div>
              <!-- 普通文本 -->
              <div v-else class="msg-text" v-html="msg.text"></div>
            </div>
            <span class="msg-time">{{ msg.time }}</span>
          </div>
        </div>

        <!-- 客服正在输入 -->
        <div v-if="csTyping" class="chat-msg cs">
          <div class="msg-avatar">
            <el-icon :size="18" color="#fff"><Headset /></el-icon>
          </div>
          <div class="msg-content cs">
            <span class="msg-sender">客服小爱</span>
            <div class="msg-bubble cs typing-bubble">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <div class="input-toolbar">
          <button class="toolbar-btn" title="表情">😊</button>
          <button class="toolbar-btn" title="图片">📷</button>
        </div>
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
            :disabled="!inputText.trim() || csTyping"
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
import { ref, watch, nextTick, computed } from 'vue'
import { Headset, User, Document, ChatDotRound, Close, Minus, PriceTag, Calendar, Picture, Present, Promotion } from '@element-plus/icons-vue'
import { userStore } from '../stores/user'
import { chatStore } from '../stores/chat'

const messagesRef = ref(null)
const inputText = ref('')
const csTyping = ref(false)
const chatOpen = ref(false)
let pollTimer = null

const todayStr = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

// 从 chatStore 读取当前用户的消息
const messages = computed(() => {
  const phone = userStore.user?.phone
  if (!phone) return []
  const chat = chatStore.getChat(phone)
  return chat?.messages || []
})

function getTime() {
  const now = new Date()
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function addUserMessage(msg) {
  const phone = userStore.user?.phone
  if (!phone) return
  chatStore.addMessage(phone, msg, {
    nickname: userStore.user.nickname,
    avatar: userStore.user.avatar
  })
  scrollToBottom()
}

function sendOrderCard(order) {
  addUserMessage({
    from: 'user',
    type: 'order',
    order: {
      id: order.id,
      pkgLabel: order.pkgLabel,
      date: order.date,
      styles: order.styles
    },
    time: getTime()
  })

  // 自动回复（如果商家未在线则模拟）
  csTyping.value = true
  scrollToBottom()
  setTimeout(() => {
    csTyping.value = false
    addCsAutoReply(`收到您的预约订单 <strong>${order.id}</strong>，我马上为您安排！`)
  }, 1500)
  setTimeout(() => {
    addCsAutoReply('请问您对拍摄场地有偏好吗？我们有室内影棚、户外花园、海边沙滩等多种场景可选 😊')
  }, 3500)
}

function addCsAutoReply(text) {
  const phone = userStore.user?.phone
  if (!phone) return
  chatStore.addMessage(phone, { from: 'cs', text, time: getTime() }, {
    nickname: userStore.user.nickname,
    avatar: userStore.user.avatar
  })
  scrollToBottom()
}

function sendMessage() {
  const text = inputText.value.trim()
  if (!text || csTyping.value) return

  addUserMessage({ from: 'user', text, time: getTime() })
  inputText.value = ''

  // 模拟客服自动回复（当商家端未回复时）
  const replies = [
    '好的呢，我已经帮您记录了，稍后专属顾问会与您联系确认细节~',
    '没问题！我们的摄影团队经验非常丰富，一定能拍出您满意的效果 😊',
    '您的预约我们非常重视，会优先为您安排最合适的档期哦~',
    '感谢您的信任！还有其他问题的话，随时可以问我~',
    '收到！我已经备注好了，拍摄当天我们会提前跟您确认的~',
    '这个套餐非常受欢迎呢！很多新人选了都特别满意 ❤️',
    '没问题，我帮您查看下最近的可用档期，稍等一下~'
  ]
  csTyping.value = true
  scrollToBottom()
  const delay = 1200 + Math.random() * 800
  setTimeout(() => {
    csTyping.value = false
    const reply = replies[Math.floor(Math.random() * replies.length)]
    addCsAutoReply(reply)
  }, delay)
}

function quickSend(text) {
  inputText.value = text
  sendMessage()
}

function openChat() {
  chatOpen.value = true
  const phone = userStore.user?.phone
  if (phone) {
    const chat = chatStore.getChat(phone)
    if (!chat || !chat.messages.length) {
      initWelcome()
    }
    chatStore.clearUnreadUser(phone)
  }
  scrollToBottom()
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

function initWelcome() {
  const phone = userStore.user?.phone
  if (!phone) return
  const name = userStore.user?.nickname || '亲'
  chatStore.ensureChat(phone, { nickname: userStore.user.nickname, avatar: userStore.user.avatar })
  chatStore.addMessage(phone, {
    from: 'cs',
    text: `${name}，您好！我是唯爱婚纱摄影的专属客服小爱 ❤️<br/>很高兴为您服务！有任何关于婚纱拍摄的问题都可以问我哦~`,
    time: getTime()
  }, { nickname: userStore.user.nickname, avatar: userStore.user.avatar })
}

// 轮询检测商家发来的新消息（模拟实时）
function startPoll() {
  stopPoll()
  pollTimer = setInterval(() => {
    const phone = userStore.user?.phone
    if (!phone || !chatOpen.value) return
    chatStore.clearUnreadUser(phone)
    scrollToBottom()
  }, 2000)
}

function stopPoll() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 监听 store 的 showChat 变化（预约提交后会触发）
watch(() => userStore.showChat, (val) => {
  if (val) {
    chatOpen.value = true
    const phone = userStore.user?.phone
    if (phone) {
      const chat = chatStore.getChat(phone)
      if (!chat || !chat.messages.length) {
        initWelcome()
      }
    }
    if (userStore.currentOrder) {
      setTimeout(() => {
        sendOrderCard(userStore.currentOrder)
      }, 1000)
    }
    startPoll()
    scrollToBottom()
  }
})

// 监听消息数量变化自动滚底
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

/* 头部 */
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

.header-action-btn:hover {
  background: rgba(255, 255, 255, 0.35);
}

.close-action:hover {
  background: rgba(220, 53, 69, 0.6);
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  overflow-x: auto;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.quick-actions::-webkit-scrollbar {
  height: 0;
}

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

.chat-messages::-webkit-scrollbar {
  width: 5px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.date-divider {
  text-align: center;
  padding: 8px 0;
}

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
  max-width: 82%;
  animation: msgFadeIn 0.3s ease;
}

@keyframes msgFadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-msg.cs {
  align-self: flex-start;
}

.chat-msg.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

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

.msg-content.user {
  align-items: flex-end;
}

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

.msg-content.user .msg-time {
  text-align: right;
}

/* 订单卡片 */
.order-card-msg {
  min-width: 240px;
}

.order-card-header-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.25);
  color: #fff;
}

.order-status-tag {
  margin-left: auto;
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.25);
  font-weight: 600;
}

.order-card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-card-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.82rem;
}

.order-card-row .label {
  opacity: 0.75;
}

.order-card-row .value {
  font-weight: 600;
  max-width: 150px;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-card-row .value.highlight {
  font-family: monospace;
  letter-spacing: 0.5px;
}

/* 打字动画 */
.typing-bubble {
  display: flex;
  gap: 5px;
  align-items: center;
  padding: 14px 20px !important;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(2) { animation-delay: 0.2s; }
.typing-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

/* ========== 输入区域 ========== */
.chat-input-area {
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.input-toolbar {
  display: flex;
  gap: 4px;
  padding: 8px 16px 0;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  font-size: 1.1rem;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.toolbar-btn:hover {
  background: #f5f5f5;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 4px 16px 14px;
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

.send-btn.active:hover {
  transform: scale(1.05);
}

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

  .fab-label {
    display: none;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .chat-panel {
    width: 380px;
    height: 580px;
  }
}
</style>
