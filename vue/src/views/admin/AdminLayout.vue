<template>
  <!-- 未登录：登录页 -->
  <div v-if="!adminStore.isLoggedIn" class="admin-login-page">
    <div class="login-card">
      <div class="login-brand">
        <div class="brand-icon">
          <el-icon :size="36" color="#fff"><Shop /></el-icon>
        </div>
        <h2>唯<span>爱</span>商家后台</h2>
        <p>Wedding Photography Admin</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="管理员账号" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="登录密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" class="login-submit" size="large" :loading="loginLoading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <div class="login-hint">
        <el-icon :size="14"><InfoFilled /></el-icon>
        演示账号：admin / admin123
      </div>
    </div>
  </div>

  <!-- 已登录：后台布局 -->
  <div v-else class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <el-icon :size="24" color="var(--secondary-color)"><Shop /></el-icon>
          <span v-show="!sidebarCollapsed" class="logo-text">唯<em>爱</em>后台</span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <el-icon :size="18"><Fold v-if="!sidebarCollapsed" /><Expand v-else /></el-icon>
        </button>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: item.path === '/admin' ? $route.path === '/admin' : $route.path.startsWith(item.path) }"
        >
          <el-icon :size="20"><component :is="item.icon" /></el-icon>
          <span v-show="!sidebarCollapsed" class="nav-label">{{ item.label }}</span>
          <span v-if="item.badge && item.badge() > 0 && !sidebarCollapsed" class="nav-badge">{{ item.badge() }}</span>
          <span v-if="item.badge && item.badge() > 0 && sidebarCollapsed" class="nav-badge-dot"></span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/" class="nav-item" target="_blank">
          <el-icon :size="20"><Monitor /></el-icon>
          <span v-show="!sidebarCollapsed" class="nav-label">前台首页</span>
        </router-link>
        <div class="nav-item" @click="handleLogout">
          <el-icon :size="20"><SwitchButton /></el-icon>
          <span v-show="!sidebarCollapsed" class="nav-label">退出登录</span>
        </div>
      </div>
    </aside>

    <!-- 主区域 -->
    <div class="admin-main">
      <!-- 顶栏 -->
      <header class="admin-topbar">
        <div class="topbar-left">
          <h3>{{ currentTitle }}</h3>
        </div>
        <div class="topbar-right">
          <el-badge :value="chatStore.totalUnreadAdmin" :hidden="!chatStore.totalUnreadAdmin" :max="99">
            <router-link to="/admin/chat" class="topbar-icon-btn">
              <el-icon :size="20"><ChatDotRound /></el-icon>
            </router-link>
          </el-badge>
          <div class="admin-info">
            <el-icon :size="18"><UserFilled /></el-icon>
            <span>{{ adminStore.admin?.name }}</span>
          </div>
        </div>
      </header>

      <!-- 内容 -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Shop, User, Lock, InfoFilled, DataAnalysis, Document, ChatDotRound, Fold, Expand, Monitor, SwitchButton, UserFilled, Avatar, Picture, PriceTag, Calendar, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { adminStore } from '../../stores/admin'
import { chatStore } from '../../stores/chat'

const route = useRoute()
const router = useRouter()
const sidebarCollapsed = ref(false)
const loginLoading = ref(false)
const loginRef = ref(null)

const loginForm = reactive({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const menuItems = [
  { path: '/admin', label: '数据看板', icon: DataAnalysis, badge: null },
  { path: '/admin/orders', label: '订单管理', icon: Document, badge: null },
  { path: '/admin/customers', label: '客户管理', icon: Avatar, badge: null },
  { path: '/admin/chat', label: '客户消息', icon: ChatDotRound, badge: () => chatStore.totalUnreadAdmin },
  { path: '/admin/schedule', label: '档期日历', icon: Calendar, badge: null },
  { path: '/admin/portfolio', label: '作品管理', icon: Picture, badge: null },
  { path: '/admin/packages', label: '套餐管理', icon: PriceTag, badge: null },
  { path: '/admin/reviews', label: '评价管理', icon: StarFilled, badge: null }
]

const currentTitle = computed(() => {
  const item = menuItems.find(i => {
    if (i.path === '/admin') return route.path === '/admin'
    return route.path.startsWith(i.path)
  })
  return item?.label || '商家后台'
})

async function handleLogin() {
  const valid = await loginRef.value?.validate().catch(() => false)
  if (!valid) return

  loginLoading.value = true
  try {
    await adminStore.login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
  } catch (e) {
    if (e.message === '该账号不是管理员') {
      ElMessage.error('该账号不是管理员')
    }
  } finally {
    loginLoading.value = false
  }
}

function handleLogout() {
  adminStore.logout()
  ElMessage.success('已退出')
}
</script>

<style scoped>
/* ========== 登录页 ========== */
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 20px;
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 20px;
  padding: 48px 40px 36px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-brand {
  text-align: center;
  margin-bottom: 36px;
}

.brand-icon {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: linear-gradient(135deg, #f1aeb5, #e88a94);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(241, 174, 181, 0.4);
}

.login-brand h2 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.login-brand h2 span { color: var(--secondary-color); }

.login-brand p {
  color: #aaa;
  font-size: 0.85rem;
  letter-spacing: 1px;
}

.login-submit {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: none !important;
  font-weight: 600;
  font-size: 1rem;
  border-radius: 12px;
  margin-top: 8px;
}

.login-submit:hover { opacity: 0.9; }

.login-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 20px;
  color: #bbb;
  font-size: 0.82rem;
}

/* ========== 后台布局 ========== */
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* 侧边栏 */
.admin-sidebar {
  width: 240px;
  background: #1a1a2e;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.admin-sidebar.collapsed {
  width: 72px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-text {
  font-size: 1.15rem;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.logo-text em {
  color: var(--secondary-color);
  font-style: normal;
}

.collapse-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: #888;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  position: relative;
  white-space: nowrap;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.85);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(241, 174, 181, 0.2), rgba(232, 138, 148, 0.15));
  color: var(--secondary-color);
}

.nav-badge {
  margin-left: auto;
  background: #e74c3c;
  color: #fff;
  font-size: 0.7rem;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.nav-badge-dot {
  position: absolute;
  top: 8px;
  right: 12px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e74c3c;
}

.sidebar-footer {
  padding: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

/* 主区域 */
.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s;
}

.admin-sidebar.collapsed + .admin-main {
  margin-left: 72px;
}

.admin-topbar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-left h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1a2e;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.topbar-icon-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.topbar-icon-btn:hover {
  background: #eee;
  color: var(--secondary-color);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 0.9rem;
  color: #333;
}

.admin-content {
  flex: 1;
  padding: 24px 28px;
}

/* 响应式 */
@media (max-width: 992px) {
  .admin-sidebar {
    width: 72px;
  }
  .admin-sidebar .nav-label,
  .admin-sidebar .logo-text,
  .admin-sidebar .nav-badge {
    display: none;
  }
  .admin-main {
    margin-left: 72px;
  }
}

@media (max-width: 768px) {
  .login-card {
    width: 100%;
    padding: 36px 24px 28px;
  }
}
</style>
