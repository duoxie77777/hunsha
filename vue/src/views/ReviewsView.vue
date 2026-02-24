<template>
  <div class="reviews-page">
    <section class="breadcrumb-section">
      <div class="container">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item active">客户评价</li>
        </ol>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>客户真实评价</h2>
          <p>每一份评价都是新人真实的体验分享，也是我们不断进步的动力</p>
        </div>

        <!-- 评分概览 -->
        <div class="rating-overview">
          <div class="overall-rating">{{ overallRating }}</div>
          <el-rate :model-value="Number(overallRating)" disabled show-score text-color="#f8c145" :colors="['#f8c145','#f8c145','#f8c145']" />
          <div class="rating-count">基于 {{ allReviews.length }} 位客户的真实评价</div>
          <div class="rating-details">
            <div class="rating-item" v-for="r in ratingItems" :key="r.label">
              <div class="rating-item-value">{{ r.value }}</div>
              <div class="rating-item-label">{{ r.label }}</div>
            </div>
          </div>
        </div>

        <!-- 筛选 -->
        <div class="review-filter">
          <el-radio-group v-model="ratingFilter" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="5">5星</el-radio-button>
            <el-radio-button label="4">4星</el-radio-button>
            <el-radio-button label="3">3星及以下</el-radio-button>
          </el-radio-group>
          <el-select v-model="sortBy" size="small" style="width: 150px;">
            <el-option label="最新评价" value="latest" />
            <el-option label="评分最高" value="highest" />
          </el-select>
        </div>

        <!-- 评价列表 -->
        <div class="review-list">
          <div class="review-card" v-for="(review, i) in filteredReviews" :key="review.id || i">
            <div class="review-header">
              <div class="reviewer-info">
                <div class="reviewer-avatar">
                  <img v-if="review.avatar" :src="review.avatar" alt="" />
                  <el-icon v-else :size="28" color="#ccc"><UserFilled /></el-icon>
                </div>
                <div>
                  <div class="reviewer-name">{{ review.name }}</div>
                  <div class="review-date">{{ review.date }}</div>
                </div>
              </div>
              <div class="review-header-right">
                <el-rate :model-value="review.rating" disabled :colors="['#f8c145','#f8c145','#f8c145']" />
                <el-tag v-if="review.orderId" size="small" type="info" effect="plain">{{ review.orderId }}</el-tag>
              </div>
            </div>
            <el-tag size="small" type="danger" effect="plain" style="margin-bottom:15px;">{{ review.pkg }}</el-tag>
            <div class="review-content">
              <span v-if="review.highlight" class="review-highlight">{{ review.highlight }}</span> {{ review.text }}
            </div>
            <div class="review-images" v-if="review.images && review.images.length">
              <el-image v-for="(img, j) in review.images" :key="j" :src="img" fit="cover" :preview-src-list="review.images" class="review-image" />
            </div>
            <div class="review-tags" v-if="review.tags && review.tags.length">
              <el-tag v-for="t in review.tags" :key="t" size="small" type="info" effect="plain">{{ t }}</el-tag>
            </div>
            <div class="review-actions">
              <span @click="likeReview(review)"><el-icon><Top /></el-icon> 有用 ({{ review.likes || 0 }})</span>
              <span><el-icon><Share /></el-icon> 分享</span>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div style="text-align:center;margin:40px 0;">
          <el-pagination layout="prev, pager, next" :total="filteredReviews.length" :page-size="10" />
        </div>

        <!-- 提交评价 -->
        <div class="submit-review">
          <h3>分享您的拍摄体验</h3>
          <p>完成拍摄后，为您的订单留下真实评价吧</p>

          <!-- 选择已完成的订单 -->
          <div v-if="!userStore.isLoggedIn" class="login-hint-box">
            <el-icon :size="24" color="#e88a94"><UserFilled /></el-icon>
            <span>请先登录后再进行评价</span>
            <el-button type="primary" size="small" round style="background:var(--secondary-color);border-color:var(--secondary-color);" @click="userStore.showLogin = true">去登录</el-button>
          </div>

          <div v-else-if="completedOrders.length === 0" class="no-orders-hint">
            <el-icon :size="24" color="#ccc"><Document /></el-icon>
            <span>暂无已完成的订单，只有已完成的订单才能评价哦~</span>
          </div>

          <template v-else>
            <!-- 订单选择 -->
            <div class="order-select-section">
              <label class="field-label">选择要评价的订单</label>
              <div class="order-select-list">
                <div
                  v-for="order in completedOrders"
                  :key="order.id"
                  class="order-select-card"
                  :class="{ selected: reviewForm.orderId === order.id, reviewed: isOrderReviewed(order.id) }"
                  @click="selectOrder(order)"
                >
                  <div class="osc-top">
                    <span class="osc-id">{{ order.id }}</span>
                    <el-tag v-if="isOrderReviewed(order.id)" type="success" size="small">已评价</el-tag>
                    <el-tag v-else type="warning" size="small">待评价</el-tag>
                  </div>
                  <div class="osc-body">
                    <span>{{ order.pkgLabel }}</span>
                    <span>{{ order.date }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 评价表单 -->
            <div v-if="reviewForm.orderId" class="review-form-area">
              <div v-if="isOrderReviewed(reviewForm.orderId)" class="already-reviewed">
                <el-icon :size="20" color="#67C23A"><CircleCheckFilled /></el-icon>
                <span>此订单您已评价过了~</span>
              </div>
              <el-form v-else :model="reviewForm" label-width="80px">
                <el-form-item label="评分">
                  <el-rate v-model="reviewForm.rating" :colors="['#f8c145','#f8c145','#f8c145']" show-text :texts="['很差', '较差', '一般', '不错', '非常棒']" />
                </el-form-item>
                <el-form-item label="评价内容">
                  <el-input v-model="reviewForm.text" type="textarea" :rows="5" placeholder="请分享您的拍摄体验，帮助更多新人做出选择~" maxlength="500" show-word-limit />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload-area">
                    <div v-for="(img, idx) in reviewForm.images" :key="idx" class="upload-preview">
                      <img :src="img" />
                      <div class="upload-preview-remove" @click="removeImage(idx)">
                        <el-icon :size="14"><Close /></el-icon>
                      </div>
                    </div>
                    <div v-if="reviewForm.images.length < 9" class="upload-btn" @click="triggerImageUpload">
                      <el-icon :size="24"><Plus /></el-icon>
                      <span>上传照片</span>
                      <span class="upload-hint">{{ reviewForm.images.length }}/9</span>
                    </div>
                    <input ref="imageInputRef" type="file" accept="image/*" multiple @change="handleImageUpload" style="display:none;" />
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    style="width: 200px; background:var(--secondary-color);border-color:var(--secondary-color);"
                    :loading="submitting"
                    @click="submitReview"
                  >
                    提交评价
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </template>
        </div>
      </div>
    </section>

    <!-- 客户故事 -->
    <section class="section bg-light">
      <div class="container">
        <div class="section-title">
          <h2>客户幸福故事</h2>
          <p>每一对新人都有属于自己的爱情故事</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(c, i) in caseList" :key="i">
            <el-card :body-style="{ padding: '0' }" shadow="hover">
              <div class="case-img">
                <el-tag class="case-badge" effect="dark" type="danger">{{ c.badge }}</el-tag>
                <img :src="c.img" />
              </div>
              <div style="padding:20px;">
                <h4 style="font-weight:700;margin-bottom:10px;">{{ c.title }}</h4>
                <p style="color:#666;line-height:1.8;margin-bottom:15px;">{{ c.story }}</p>
                <el-button type="primary" class="w-100" style="background:var(--secondary-color);border-color:var(--secondary-color);">查看完整故事</el-button>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Top, Share, UserFilled, Document, Plus, Close, CircleCheckFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userStore } from '../stores/user'
import { useRoute } from 'vue-router'

const route = useRoute()

const REVIEWS_KEY = 'weiai_user_reviews'
function loadUserReviews() {
  try { return JSON.parse(localStorage.getItem(REVIEWS_KEY) || '[]') } catch { return [] }
}
function saveUserReviews(data) {
  localStorage.setItem(REVIEWS_KEY, JSON.stringify(data))
}

const ratingFilter = ref('all')
const sortBy = ref('latest')
const submitting = ref(false)
const imageInputRef = ref(null)

// 默认评价数据
const defaultReviews = [
  { id: 'd1', avatar: 'https://picsum.photos/id/1005/200/200', name: '张先生 & 李女士', date: '2025年12月15日', rating: 5, pkg: '经典挚爱套餐', highlight: '超出预期的拍摄体验！', text: '从前期沟通到后期选片，整个过程都非常顺畅。摄影师非常专业，化妆师的技术超棒，照片出来的效果非常自然。', images: ['https://picsum.photos/id/1011/200/200', 'https://picsum.photos/id/1015/200/200', 'https://picsum.photos/id/1018/200/200'], tags: ['服务周到', '拍摄专业', '化妆精致'], likes: 128, timestamp: 1702627200000 },
  { id: 'd2', avatar: 'https://picsum.photos/id/1006/200/200', name: '王先生 & 陈女士', date: '2025年11月28日', rating: 5, pkg: '高端定制套餐', highlight: '三亚旅拍体验超棒！', text: '选择了唯爱的高端定制旅拍套餐，整个过程非常省心，总监级摄影师的技术果然名不虚传。', images: ['https://picsum.photos/id/1019/200/200', 'https://picsum.photos/id/1035/200/200'], tags: ['旅拍推荐', '技术精湛', '服务贴心'], likes: 98, timestamp: 1701158400000 },
  { id: 'd3', avatar: 'https://picsum.photos/id/1010/200/200', name: '刘先生 & 赵女士', date: '2025年10月18日', rating: 4, pkg: '浪漫体验套餐', highlight: '整体满意，略有小遗憾', text: '作为性价比很高的体验套餐，整体拍摄效果还是很满意的。摄影师很有耐心。', images: ['https://picsum.photos/id/1047/200/200'], tags: ['性价比高', '耐心指导'], likes: 76, timestamp: 1697616000000 },
  { id: 'd4', avatar: 'https://picsum.photos/id/1012/200/200', name: '孙先生 & 周女士', date: '2025年9月25日', rating: 5, pkg: '经典挚爱套餐', highlight: '中式婚纱照拍得超有韵味！', text: '对比了很多家最终选择了唯爱。服装非常精美，摄影师非常懂中式美学。', images: ['https://picsum.photos/id/1069/200/200', 'https://picsum.photos/id/1074/200/200'], tags: ['中式风格', '细节精美', '有故事感'], likes: 89, timestamp: 1695628800000 }
]

const userReviews = ref(loadUserReviews())

// 合并默认+用户评价
const allReviews = computed(() => {
  return [...userReviews.value, ...defaultReviews]
})

const overallRating = computed(() => {
  if (!allReviews.value.length) return '0.0'
  return (allReviews.value.reduce((s, r) => s + r.rating, 0) / allReviews.value.length).toFixed(1)
})

const ratingItems = [
  { value: '98%', label: '服务满意度' },
  { value: '99%', label: '拍摄效果' },
  { value: '97%', label: '化妆造型' },
  { value: '96%', label: '性价比' },
  { value: '98%', label: '后期修图' }
]

const filteredReviews = computed(() => {
  let result = [...allReviews.value]
  if (ratingFilter.value === '3') {
    result = result.filter(r => r.rating <= 3)
  } else if (ratingFilter.value !== 'all') {
    result = result.filter(r => String(r.rating) === ratingFilter.value)
  }
  if (sortBy.value === 'highest') {
    result.sort((a, b) => b.rating - a.rating)
  } else {
    result.sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
  }
  return result
})

// 已完成的订单（可以评价）
const completedOrders = computed(() => {
  return (userStore.orders || []).filter(o => o.status === '已完成')
})

// 判断订单是否已评价
function isOrderReviewed(orderId) {
  return userReviews.value.some(r => r.orderId === orderId)
}

const reviewForm = reactive({
  orderId: '',
  orderPkgLabel: '',
  rating: 5,
  text: '',
  images: []
})

function selectOrder(order) {
  if (isOrderReviewed(order.id)) {
    reviewForm.orderId = order.id
    return
  }
  reviewForm.orderId = order.id
  reviewForm.orderPkgLabel = order.pkgLabel || ''
  reviewForm.rating = 5
  reviewForm.text = ''
  reviewForm.images = []
}

function triggerImageUpload() {
  imageInputRef.value?.click()
}

function handleImageUpload(e) {
  const files = Array.from(e.target.files || [])
  if (!files.length) return

  const remaining = 9 - reviewForm.images.length
  const toProcess = files.slice(0, remaining)

  toProcess.forEach(file => {
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.warning(`图片 ${file.name} 超过5MB，已跳过`)
      return
    }
    const reader = new FileReader()
    reader.onload = (ev) => {
      reviewForm.images.push(ev.target.result)
    }
    reader.readAsDataURL(file)
  })

  // 重置 input
  e.target.value = ''
}

function removeImage(idx) {
  reviewForm.images.splice(idx, 1)
}

function likeReview(review) {
  review.likes = (review.likes || 0) + 1
}

function submitReview() {
  if (!reviewForm.text.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }
  if (reviewForm.text.trim().length < 10) {
    ElMessage.warning('评价内容至少10个字哦~')
    return
  }

  submitting.value = true
  setTimeout(() => {
    const newReview = {
      id: 'ur_' + Date.now().toString(36),
      orderId: reviewForm.orderId,
      avatar: userStore.user?.avatar || '',
      name: userStore.user?.nickname || '匿名用户',
      date: new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' }),
      rating: reviewForm.rating,
      pkg: reviewForm.orderPkgLabel,
      highlight: '',
      text: reviewForm.text,
      images: [...reviewForm.images],
      tags: [],
      likes: 0,
      timestamp: Date.now()
    }

    userReviews.value.unshift(newReview)
    saveUserReviews(userReviews.value)

    // 同步到后台评价管理
    syncToAdminReviews(newReview)

    submitting.value = false
    reviewForm.orderId = ''
    reviewForm.text = ''
    reviewForm.images = []
    reviewForm.rating = 5

    ElMessage.success('评价提交成功，感谢您的分享！')
  }, 800)
}

function syncToAdminReviews(review) {
  try {
    const ADMIN_REVIEWS_KEY = 'weiai_admin_reviews'
    const adminReviews = JSON.parse(localStorage.getItem(ADMIN_REVIEWS_KEY) || '[]')
    adminReviews.unshift({
      id: review.id,
      customerName: review.name,
      avatar: review.avatar,
      date: review.date,
      rating: review.rating,
      content: review.text,
      pkgName: review.pkg,
      images: review.images,
      reply: '',
      replied: false,
      featured: false
    })
    localStorage.setItem(ADMIN_REVIEWS_KEY, JSON.stringify(adminReviews))
  } catch {}
}

// 从路由参数自动选中订单
onMounted(() => {
  const orderId = route.query.orderId
  if (orderId && userStore.isLoggedIn) {
    const order = completedOrders.value.find(o => o.id === orderId)
    if (order) {
      selectOrder(order)
      setTimeout(() => {
        document.querySelector('.submit-review')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 300)
    }
  }
})

const caseList = [
  { badge: '三亚旅拍', img: 'https://picsum.photos/id/1011/800/600', title: '海边的承诺：张先生 & 李女士', story: '相识于大学校园，相恋8年，在三亚的海边完成了他们的婚纱照拍摄。' },
  { badge: '复古系列', img: 'https://picsum.photos/id/1015/800/600', title: '时光的印记：王先生 & 陈女士', story: '偏爱复古风格，选择了欧式宫廷风的婚纱照拍摄。' },
  { badge: '中式系列', img: 'https://picsum.photos/id/1018/800/600', title: '东方的浪漫：刘先生 & 赵女士', story: '热爱中国传统文化，选择了新中式风格的婚纱照。' }
]
</script>

<style scoped>
.rating-overview { background: linear-gradient(135deg, var(--primary-color) 0%, #fdf2f8 100%); border-radius: 20px; padding: 60px 40px; margin-bottom: 60px; text-align: center; }
.overall-rating { font-size: 4rem; font-weight: 700; color: var(--secondary-color); margin-bottom: 10px; }
.rating-count { color: #666; font-size: 1.1rem; margin: 20px 0; }
.rating-details { display: flex; justify-content: center; flex-wrap: wrap; gap: 30px; margin-top: 40px; }
.rating-item { text-align: center; min-width: 120px; }
.rating-item-value { font-size: 2rem; font-weight: 700; color: var(--dark-color); }
.rating-item-label { color: #666; font-size: 0.9rem; }

.review-filter { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px; margin-bottom: 40px; padding: 20px 30px; background: #fff; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }

.review-card { background: #fff; border-radius: 15px; padding: 40px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); margin-bottom: 30px; transition: all 0.3s ease; }
.review-card:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(241,174,181,0.1); }
.review-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 10px; }
.review-header-right { display: flex; align-items: center; gap: 10px; }
.reviewer-info { display: flex; align-items: center; gap: 15px; }
.reviewer-avatar { width: 70px; height: 70px; border-radius: 50%; overflow: hidden; border: 3px solid var(--primary-color); display: flex; align-items: center; justify-content: center; background: #f8f8f8; }
.reviewer-avatar img { width: 100%; height: 100%; object-fit: cover; }
.reviewer-name { font-weight: 700; font-size: 1.2rem; }
.review-date { color: #999; font-size: 0.9rem; }
.review-content { color: #666; line-height: 1.8; margin-bottom: 20px; }
.review-highlight { color: var(--secondary-color); font-weight: 600; }
.review-images { display: flex; gap: 10px; margin-bottom: 20px; overflow-x: auto; }
.review-image { width: 100px; height: 100px; border-radius: 8px; flex-shrink: 0; }
.review-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px; }
.review-actions { display: flex; gap: 20px; color: #999; font-size: 0.9rem; }
.review-actions span { display: flex; align-items: center; gap: 5px; cursor: pointer; transition: color 0.3s; }
.review-actions span:hover { color: var(--secondary-color); }

.submit-review { background: #fff; border-radius: 15px; padding: 50px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); margin-bottom: 60px; }
.submit-review h3 { text-align: center; font-weight: 700; margin-bottom: 10px; }
.submit-review > p { text-align: center; color: #666; margin-bottom: 40px; }

/* 登录提示 */
.login-hint-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 30px;
  background: #fdf2f8;
  border-radius: 12px;
  color: #666;
  font-size: 0.95rem;
}

.no-orders-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 40px;
  color: #999;
  font-size: 0.95rem;
}

/* 订单选择 */
.order-select-section {
  margin-bottom: 30px;
}

.field-label {
  display: block;
  font-weight: 700;
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 12px;
}

.order-select-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.order-select-card {
  border: 2px solid #eee;
  border-radius: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-select-card:hover {
  border-color: #f1aeb5;
}

.order-select-card.selected {
  border-color: var(--secondary-color);
  background: #fdf2f8;
}

.order-select-card.reviewed {
  opacity: 0.7;
}

.osc-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.osc-id {
  font-weight: 700;
  font-size: 0.88rem;
  color: #333;
}

.osc-body {
  display: flex;
  justify-content: space-between;
  font-size: 0.82rem;
  color: #888;
}

/* 已评价提示 */
.already-reviewed {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 30px;
  background: #f0f9eb;
  border-radius: 12px;
  color: #67C23A;
  font-weight: 600;
  font-size: 0.95rem;
}

/* 评价表单 */
.review-form-area {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 图片上传 */
.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.upload-preview {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-preview-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.upload-preview-remove:hover {
  background: #e74c3c;
}

.upload-btn {
  width: 100px;
  height: 100px;
  border: 2px dashed #ddd;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  color: #ccc;
  font-size: 0.78rem;
  transition: all 0.3s;
}

.upload-btn:hover {
  border-color: #e88a94;
  color: #e88a94;
}

.upload-hint {
  font-size: 0.7rem;
  color: #bbb;
}

.case-img { height: 250px; position: relative; overflow: hidden; }
.case-img img { width: 100%; height: 100%; object-fit: cover; }
.case-badge { position: absolute; top: 15px; left: 15px; }
</style>
