<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="(s, i) in stats" :key="i">
        <div class="stat-icon" :style="{ background: s.bg }">
          <el-icon :size="24" color="#fff"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
        <div class="stat-trend" :class="s.trend > 0 ? 'up' : 'down'">
          {{ s.trend > 0 ? '+' : '' }}{{ s.trend }}%
        </div>
      </div>
    </div>

    <!-- 两列布局 -->
    <div class="dash-row">
      <!-- 最近订单 -->
      <div class="dash-card flex2">
        <div class="card-header">
          <h4>最近订单</h4>
          <router-link to="/admin/orders" class="view-all">查看全部 →</router-link>
        </div>
        <div class="order-table-wrap">
          <table class="mini-table">
            <thead>
              <tr><th>订单号</th><th>客户</th><th>套餐</th><th>日期</th><th>状态</th></tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.id">
                <td class="order-id-cell">{{ order.id }}</td>
                <td>{{ order.name }}</td>
                <td>{{ order.pkgLabel }}</td>
                <td>{{ order.date }}</td>
                <td>
                  <span class="status-dot" :class="statusClass(order.status)"></span>
                  {{ order.status }}
                </td>
              </tr>
              <tr v-if="!recentOrders.length">
                <td colspan="5" class="empty-cell">暂无订单数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 消息 + 快捷操作 -->
      <div class="dash-col flex1">
        <div class="dash-card">
          <div class="card-header">
            <h4>未读消息</h4>
            <router-link to="/admin/chat" class="view-all">进入聊天 →</router-link>
          </div>
          <div v-if="unreadChats.length" class="msg-list">
            <div v-for="chat in unreadChats" :key="chat.phone" class="msg-item" @click="$router.push('/admin/chat')">
              <div class="msg-avatar">
                <img v-if="chat.user.avatar" :src="chat.user.avatar" />
                <el-icon v-else :size="18" color="#fff"><User /></el-icon>
              </div>
              <div class="msg-info">
                <span class="msg-name">{{ chat.user.nickname }}</span>
                <span class="msg-preview">{{ lastMsg(chat) }}</span>
              </div>
              <span class="msg-unread">{{ chat.unreadAdmin }}</span>
            </div>
          </div>
          <div v-else class="empty-msg">
            <el-icon :size="36" color="#ddd"><ChatDotRound /></el-icon>
            <p>暂无未读消息</p>
          </div>
        </div>

        <div class="dash-card quick-stats">
          <div class="card-header"><h4>今日概览</h4></div>
          <div class="quick-stat-grid">
            <div class="qs-item">
              <span class="qs-num">{{ todayOrders }}</span>
              <span class="qs-label">今日新增</span>
            </div>
            <div class="qs-item">
              <span class="qs-num">{{ pendingOrders }}</span>
              <span class="qs-label">待确认</span>
            </div>
            <div class="qs-item">
              <span class="qs-num">{{ totalChats }}</span>
              <span class="qs-label">聊天会话</span>
            </div>
            <div class="qs-item">
              <span class="qs-num">{{ chatStore.totalUnreadAdmin }}</span>
              <span class="qs-label">未读消息</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Document, ChatDotRound, User, TrendCharts, Money, Calendar, UserFilled } from '@element-plus/icons-vue'
import { userStore } from '../../stores/user'
import { chatStore } from '../../stores/chat'

const allOrders = computed(() => userStore.orders || [])
const recentOrders = computed(() => allOrders.value.slice(0, 6))

const todayOrders = computed(() => {
  const today = new Date().toLocaleDateString('zh-CN')
  return allOrders.value.filter(o => o.createTime?.startsWith(today)).length
})

const pendingOrders = computed(() => allOrders.value.filter(o => o.status === '待确认').length)
const totalChats = computed(() => Object.keys(chatStore.chats).length)

const unreadChats = computed(() =>
  chatStore.chatListSorted.filter(c => c.unreadAdmin > 0).slice(0, 4)
)

function lastMsg(chat) {
  const msgs = chat.messages
  if (!msgs.length) return ''
  const last = msgs[msgs.length - 1]
  if (last.type === 'order') return '[预约订单]'
  return (last.text || '').replace(/<[^>]+>/g, '').substring(0, 30)
}

const stats = computed(() => [
  { icon: Document, label: '总订单数', value: allOrders.value.length, bg: 'linear-gradient(135deg, #667eea, #764ba2)', trend: 12 },
  { icon: Money, label: '预估营收', value: '¥' + (allOrders.value.length * 6999).toLocaleString(), bg: 'linear-gradient(135deg, #f093fb, #f5576c)', trend: 8 },
  { icon: ChatDotRound, label: '咨询客户', value: totalChats.value, bg: 'linear-gradient(135deg, #4facfe, #00f2fe)', trend: 15 },
  { icon: Calendar, label: '本月预约', value: allOrders.value.length, bg: 'linear-gradient(135deg, #43e97b, #38f9d7)', trend: -3 }
])

function statusClass(status) {
  return { '待确认': 'warning', '已确认': 'success', '已完成': 'info', '已取消': 'danger' }[status] || 'info'
}
</script>

<style scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 22px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 800;
  color: #1a1a2e;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.82rem;
  color: #999;
  margin-top: 2px;
}

.stat-trend {
  margin-left: auto;
  font-size: 0.8rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 8px;
}

.stat-trend.up { background: #e8f8ef; color: #27ae60; }
.stat-trend.down { background: #fde8e8; color: #e74c3c; }

/* 两列 */
.dash-row {
  display: flex;
  gap: 20px;
}

.flex2 { flex: 2; }
.flex1 { flex: 1; }

.dash-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dash-card {
  background: #fff;
  border-radius: 14px;
  padding: 22px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.card-header h4 {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1a1a2e;
}

.view-all {
  font-size: 0.82rem;
  color: var(--secondary-color);
  font-weight: 600;
}

/* 迷你表格 */
.order-table-wrap {
  overflow-x: auto;
}

.mini-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.88rem;
}

.mini-table th {
  text-align: left;
  padding: 10px 12px;
  color: #999;
  font-weight: 600;
  font-size: 0.8rem;
  border-bottom: 1px solid #f0f0f0;
  white-space: nowrap;
}

.mini-table td {
  padding: 12px;
  border-bottom: 1px solid #f8f8f8;
  color: #333;
  white-space: nowrap;
}

.order-id-cell {
  font-family: monospace;
  font-weight: 600;
  color: #667eea;
}

.status-dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-dot.warning { background: #f0ad4e; }
.status-dot.success { background: #27ae60; }
.status-dot.info { background: #999; }
.status-dot.danger { background: #e74c3c; }

.empty-cell {
  text-align: center;
  color: #ccc;
  padding: 40px 0 !important;
}

/* 消息列表 */
.msg-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.msg-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.msg-item:hover { background: #f8f9fb; }

.msg-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.msg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.msg-info {
  flex: 1;
  min-width: 0;
}

.msg-name {
  display: block;
  font-weight: 600;
  font-size: 0.9rem;
  color: #333;
}

.msg-preview {
  display: block;
  font-size: 0.78rem;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-unread {
  background: #e74c3c;
  color: #fff;
  font-size: 0.7rem;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  font-weight: 700;
}

.empty-msg {
  text-align: center;
  padding: 30px 0;
}

.empty-msg p { color: #ccc; font-size: 0.88rem; margin-top: 8px; }

/* 快捷统计 */
.quick-stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.qs-item {
  background: #f8f9fb;
  border-radius: 10px;
  padding: 16px;
  text-align: center;
}

.qs-num {
  display: block;
  font-size: 1.5rem;
  font-weight: 800;
  color: #1a1a2e;
}

.qs-label {
  display: block;
  font-size: 0.78rem;
  color: #999;
  margin-top: 2px;
}

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 992px) {
  .dash-row { flex-direction: column; }
}

@media (max-width: 768px) {
  .stat-cards { grid-template-columns: 1fr; }
}
</style>
