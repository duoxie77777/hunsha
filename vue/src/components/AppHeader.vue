<template>
  <header class="app-header" :class="{ scrolled: isScrolled }">
    <div class="container header-inner">
      <router-link to="/" class="brand">唯<span>爱</span>婚纱摄影</router-link>

      <button class="mobile-toggle" @click="menuOpen = !menuOpen">
        <el-icon :size="24"><Fold v-if="menuOpen" /><Expand v-else /></el-icon>
      </button>

      <nav class="nav-menu" :class="{ open: menuOpen }">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: $route.path === item.path }"
          @click="menuOpen = false"
        >
          <el-icon style="margin-right: 4px;"><component :is="item.icon" /></el-icon>
          {{ item.label }}
        </router-link>
      </nav>

      <el-button class="book-btn" type="primary" round @click="userStore.openBooking()">
        <el-icon style="margin-right: 4px;"><Calendar /></el-icon>立即预约
      </el-button>

      <div class="user-area">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-info-btn">
              <el-icon :size="18"><UserFilled /></el-icon>
              <span class="user-nickname">{{ userStore.user.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><Document /></el-icon> 我的订单
                </el-dropdown-item>
                <el-dropdown-item @click="goProfile">
                  <el-icon><Setting /></el-icon> 个人资料
                </el-dropdown-item>
                <el-dropdown-item @click="userStore.openBooking()">
                  <el-icon><Calendar /></el-icon> 我要预约
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button class="login-btn" text @click="userStore.showLogin = true">
            <el-icon style="margin-right: 4px;"><User /></el-icon>登录
          </el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { HomeFilled, Service, Picture, PriceTag, ChatDotRound, InfoFilled, Calendar, Fold, Expand, User, UserFilled, SwitchButton, Document, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userStore } from '../stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()

const navItems = [
  { path: '/', label: '首页', icon: HomeFilled },
  { path: '/services', label: '服务项目', icon: Service },
  { path: '/portfolio', label: '作品展示', icon: Picture },
  { path: '/pricing', label: '套餐价格', icon: PriceTag },
  { path: '/reviews', label: '客户评价', icon: ChatDotRound },
  { path: '/about', label: '关于我们', icon: InfoFilled }
]

const isScrolled = ref(false)
const menuOpen = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  if (router.currentRoute.value.path === '/profile') {
    router.push('/')
  }
}

function goProfile() {
  router.push({ path: '/profile', query: { tab: 'profile' } })
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  padding: 15px 0;
}

.app-header.scrolled {
  padding: 8px 0;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--dark-color);
  white-space: nowrap;
}

.brand span {
  color: var(--secondary-color);
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  color: var(--dark-color);
  font-weight: 600;
  padding: 8px 12px;
  border-radius: 6px;
  position: relative;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  font-size: 0.95rem;
  white-space: nowrap;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 12px;
  right: 12px;
  height: 2px;
  background-color: var(--secondary-color);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.nav-link:hover::after,
.nav-link.active::after {
  transform: scaleX(1);
}

.nav-link.active {
  color: var(--secondary-color);
}

.book-btn {
  background-color: var(--secondary-color) !important;
  border-color: var(--secondary-color) !important;
  font-weight: 600;
}

.book-btn:hover {
  background-color: #e88a94 !important;
  border-color: #e88a94 !important;
}

.user-area {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.user-info-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--dark-color);
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info-btn:hover {
  background: var(--primary-color);
  color: var(--secondary-color);
}

.user-nickname {
  font-size: 0.9rem;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-btn {
  color: var(--dark-color) !important;
  font-weight: 600;
}

.login-btn:hover {
  color: var(--secondary-color) !important;
}

.mobile-toggle {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--dark-color);
}

@media (max-width: 992px) {
  .mobile-toggle {
    display: flex;
    align-items: center;
  }

  .nav-menu {
    display: none;
    position: absolute;
    top: 100%;
    left: 0;
    width: 100%;
    background: #fff;
    flex-direction: column;
    padding: 15px 0;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }

  .nav-menu.open {
    display: flex;
  }

  .nav-link {
    width: 100%;
    padding: 12px 30px;
    border-radius: 0;
  }

  .nav-link::after {
    display: none;
  }

  .book-btn {
    display: none;
  }
}
</style>
