<template>
  <div class="profile-page">
    <!-- 面包屑 -->
    <div class="breadcrumb-section">
      <div class="container">
        <ul class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item active">个人中心</li>
        </ul>
      </div>
    </div>

    <div class="section">
      <div class="container">
        <div class="profile-layout">
          <!-- 左侧菜单 -->
          <div class="profile-sidebar">
            <div class="user-card">
              <div class="avatar-wrapper" @click="triggerUpload">
                <img v-if="userStore.user?.avatar" :src="userStore.user.avatar" class="avatar-img" />
                <div v-else class="avatar-placeholder">
                  <el-icon :size="36" color="#fff"><UserFilled /></el-icon>
                </div>
                <div class="avatar-overlay">
                  <el-icon :size="16" color="#fff"><Camera /></el-icon>
                </div>
                <input ref="fileInput" type="file" accept="image/*" hidden @change="handleAvatarChange" />
              </div>
              <h3 class="user-name">{{ profile.nickname || userStore.user?.nickname }}</h3>
              <p class="user-phone">{{ userStore.user?.phone }}</p>
            </div>

            <div class="sidebar-menu">
              <div
                class="menu-item"
                :class="{ active: activeTab === 'orders' }"
                @click="activeTab = 'orders'"
              >
                <el-icon><Document /></el-icon>
                <span>我的订单</span>
                <span v-if="userStore.orders.length" class="badge">{{ userStore.orders.length }}</span>
              </div>
              <div
                class="menu-item"
                :class="{ active: activeTab === 'profile' }"
                @click="activeTab = 'profile'"
              >
                <el-icon><Setting /></el-icon>
                <span>个人资料</span>
              </div>
            </div>
          </div>

          <!-- 右侧内容 -->
          <div class="profile-content">
            <!-- 我的订单 -->
            <div v-if="activeTab === 'orders'" class="content-panel">
              <div class="panel-header">
                <h2>我的订单</h2>
                <el-button type="primary" size="small" round
                  style="background:var(--secondary-color);border-color:var(--secondary-color);"
                  @click="userStore.openBooking()"
                >
                  <el-icon style="margin-right:4px"><Plus /></el-icon>新建预约
                </el-button>
              </div>

              <div v-if="!userStore.orders.length" class="empty-state">
                <el-icon :size="64" color="#ddd"><Document /></el-icon>
                <p>暂无订单</p>
                <el-button type="primary" round
                  style="background:var(--secondary-color);border-color:var(--secondary-color);"
                  @click="userStore.openBooking()"
                >
                  立即预约
                </el-button>
              </div>

              <div v-else class="order-list">
                <div v-for="order in userStore.orders" :key="order.id" class="order-card">
                  <div class="order-card-header">
                    <div class="order-id">
                      <el-icon><Document /></el-icon>
                      <span>{{ order.id }}</span>
                    </div>
                    <el-tag
                      :type="statusType(order.status)"
                      size="small"
                      round
                    >
                      {{ order.status }}
                    </el-tag>
                  </div>

                  <div class="order-card-body">
                    <div class="order-info-grid">
                      <div class="order-info-item">
                        <span class="label">套餐</span>
                        <span class="value">{{ order.pkgLabel }}</span>
                      </div>
                      <div class="order-info-item">
                        <span class="label">日期</span>
                        <span class="value">{{ order.date }}</span>
                      </div>
                      <div class="order-info-item">
                        <span class="label">姓名</span>
                        <span class="value">{{ order.name }}</span>
                      </div>
                      <div class="order-info-item">
                        <span class="label">风格</span>
                        <span class="value">{{ order.styles || '未选择' }}</span>
                      </div>
                    </div>
                    <div v-if="order.remark" class="order-remark">
                      <span class="label">备注：</span>{{ order.remark }}
                    </div>
                  </div>

                  <div class="order-card-footer">
                    <span class="order-time">{{ order.createTime }}</span>
                    <div class="order-actions">
                      <el-button
                        size="small"
                        round
                        @click="userStore.openChat(order)"
                      >
                        <el-icon style="margin-right:4px"><ChatDotRound /></el-icon>联系客服
                      </el-button>
                      <el-button
                        v-if="order.status === '已完成'"
                        size="small"
                        type="warning"
                        round
                        plain
                        @click="goReview(order)"
                      >
                        <el-icon style="margin-right:4px"><Star /></el-icon>去评价
                      </el-button>
                      <el-button
                        v-if="order.status === '待确认'"
                        size="small"
                        type="danger"
                        round
                        plain
                        @click="cancelOrder(order)"
                      >
                        取消订单
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 个人资料 -->
            <div v-if="activeTab === 'profile'" class="content-panel">
              <div class="panel-header">
                <h2>个人资料</h2>
              </div>

              <el-form :model="profile" label-position="top" class="profile-form">
                <div class="form-section-title">基本信息</div>

                <div class="form-row">
                  <el-form-item label="昵称" class="form-half">
                    <el-input v-model="profile.nickname" placeholder="请输入昵称" size="large" :prefix-icon="UserFilled" />
                  </el-form-item>
                  <el-form-item label="年龄" class="form-half">
                    <el-input-number v-model="profile.age" :min="18" :max="99" size="large" style="width:100%" />
                  </el-form-item>
                </div>

                <el-form-item label="手机号码">
                  <el-input :model-value="userStore.user?.phone" disabled size="large" :prefix-icon="Phone">
                    <template #append>
                      <el-tag type="success" size="small">已验证</el-tag>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="个人简介">
                  <el-input
                    v-model="profile.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="介绍一下自己吧..."
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>

                <div class="form-section-title" style="margin-top:10px;">伴侣信息（选填）</div>

                <div class="form-row">
                  <el-form-item label="伴侣姓名" class="form-half">
                    <el-input v-model="profile.partnerName" placeholder="请输入伴侣姓名" size="large" :prefix-icon="User" />
                  </el-form-item>
                  <el-form-item label="婚期" class="form-half">
                    <el-date-picker v-model="profile.weddingDate" placeholder="选择婚期" size="large" style="width:100%" />
                  </el-form-item>
                </div>

                <el-button
                  type="primary"
                  size="large"
                  class="save-btn"
                  :loading="saving"
                  @click="saveProfile"
                >
                  保存修改
                </el-button>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { UserFilled, User, Camera, Document, Setting, Plus, ChatDotRound, Phone, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userStore } from '../stores/user'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeTab = ref(route.query.tab === 'profile' ? 'profile' : 'orders')
const saving = ref(false)
const fileInput = ref(null)

const profile = reactive({
  nickname: '',
  age: null,
  bio: '',
  partnerName: '',
  weddingDate: ''
})

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/')
    return
  }
  // 载入已保存的资料
  const u = userStore.user
  if (u) {
    profile.nickname = u.nickname || ''
    profile.age = u.age || null
    profile.bio = u.bio || ''
    profile.partnerName = u.partnerName || ''
    profile.weddingDate = u.weddingDate || ''
  }
})

function statusType(status) {
  const map = { '待确认': 'warning', '已确认': 'success', '已完成': 'info', '已取消': 'danger' }
  return map[status] || 'info'
}

function triggerUpload() {
  fileInput.value?.click()
}

function handleAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像图片不能超过2MB')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    userStore.updateProfile({ avatar: ev.target.result })
    ElMessage.success('头像已更新')
  }
  reader.readAsDataURL(file)
}

function saveProfile() {
  saving.value = true
  setTimeout(() => {
    userStore.updateProfile({
      nickname: profile.nickname,
      age: profile.age,
      bio: profile.bio,
      partnerName: profile.partnerName,
      weddingDate: profile.weddingDate
    })
    saving.value = false
    ElMessage.success('资料保存成功！')
  }, 500)
}

function goReview(order) {
  router.push({ path: '/reviews', query: { orderId: order.id } })
}

function cancelOrder(order) {
  ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
    confirmButtonText: '确定取消',
    cancelButtonText: '再想想',
    type: 'warning'
  }).then(() => {
    userStore.cancelOrder(order.id)
    ElMessage.success('订单已取消')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-layout {
  display: flex;
  gap: 30px;
  min-height: 600px;
}

/* 左侧 */
.profile-sidebar {
  width: 260px;
  flex-shrink: 0;
}

.user-card {
  background: #fff;
  border-radius: 16px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.avatar-wrapper {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  margin: 0 auto 15px;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--secondary-color), #e88a94);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 30px;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 4px;
}

.user-phone {
  font-size: 0.85rem;
  color: #999;
}

.sidebar-menu {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  color: var(--dark-color);
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background: var(--primary-color);
}

.menu-item.active {
  background: var(--primary-color);
  color: var(--secondary-color);
  border-left-color: var(--secondary-color);
}

.badge {
  margin-left: auto;
  background: var(--secondary-color);
  color: #fff;
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

/* 右侧 */
.profile-content {
  flex: 1;
  min-width: 0;
}

.content-panel {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-header h2 {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--dark-color);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-state p {
  color: #999;
  margin: 15px 0 20px;
  font-size: 1rem;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: var(--secondary-color);
  box-shadow: 0 4px 15px rgba(241, 174, 181, 0.15);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  font-size: 0.95rem;
  color: var(--dark-color);
}

.order-card-body {
  padding: 16px 20px;
}

.order-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.order-info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-info-item .label {
  font-size: 0.8rem;
  color: #999;
}

.order-info-item .value {
  font-weight: 600;
  color: var(--dark-color);
  font-size: 0.92rem;
}

.order-remark {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #f0f0f0;
  font-size: 0.85rem;
  color: #666;
}

.order-remark .label {
  color: #999;
}

.order-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.order-time {
  font-size: 0.8rem;
  color: #bbb;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* 个人资料 */
.profile-form {
  max-width: 600px;
}

.form-section-title {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary-color);
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-half {
  flex: 1;
}

.save-btn {
  width: 200px;
  background: linear-gradient(135deg, var(--secondary-color), #e88a94) !important;
  border: none !important;
  font-weight: 600;
  font-size: 1rem;
  height: 46px;
  border-radius: 8px;
  margin-top: 10px;
}

.save-btn:hover {
  opacity: 0.9;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--dark-color);
}

/* 响应式 */
@media (max-width: 992px) {
  .profile-layout {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
  }

  .sidebar-menu {
    display: flex;
  }

  .menu-item {
    flex: 1;
    justify-content: center;
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .menu-item.active {
    border-left: none;
    border-bottom-color: var(--secondary-color);
  }
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .order-info-grid {
    grid-template-columns: 1fr;
  }

  .order-card-footer {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .content-panel {
    padding: 20px 15px;
  }

  .save-btn {
    width: 100%;
  }
}
</style>
