<template>
  <div class="reviews-manage-page">
    <!-- 统计栏 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(241,174,181,0.1);">
          <el-icon :size="22" color="#e88a94"><Star /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ avgRating }}</span>
          <span class="stat-label">平均评分</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(64,158,255,0.1);">
          <el-icon :size="22" color="#409EFF"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ reviews.length }}</span>
          <span class="stat-label">总评价数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(103,194,58,0.1);">
          <el-icon :size="22" color="#67C23A"><StarFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ reviews.filter(r => r.featured).length }}</span>
          <span class="stat-label">精选评价</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(230,162,60,0.1);">
          <el-icon :size="22" color="#E6A23C"><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ reviews.filter(r => !r.replied).length }}</span>
          <span class="stat-label">待回复</span>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input v-model="searchKey" placeholder="搜索客户名/评价内容" :prefix-icon="Search" clearable style="width: 260px;" />
      <el-select v-model="filterRating" placeholder="评分筛选" clearable style="width: 140px;">
        <el-option label="5星" :value="5" />
        <el-option label="4星" :value="4" />
        <el-option label="3星" :value="3" />
        <el-option label="2星及以下" :value="2" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 130px;">
        <el-option label="待回复" value="unreplied" />
        <el-option label="已回复" value="replied" />
        <el-option label="精选" value="featured" />
      </el-select>
    </div>

    <!-- 评价列表 -->
    <div class="reviews-list">
      <div v-for="review in filteredReviews" :key="review.id" class="review-card" :class="{ featured: review.featured }">
        <div class="review-header">
          <div class="reviewer-info">
            <el-avatar :size="40" :src="review.avatar || ''">
              <el-icon :size="20"><UserFilled /></el-icon>
            </el-avatar>
            <div>
              <span class="reviewer-name">{{ review.customerName }}</span>
              <span class="review-date">{{ review.date }}</span>
            </div>
          </div>
          <div class="review-header-right">
            <div class="star-rating">
              <el-icon v-for="i in 5" :key="i" :size="16" :color="i <= review.rating ? '#f7ba2a' : '#ddd'"><StarFilled /></el-icon>
            </div>
            <el-tag v-if="review.featured" type="warning" size="small" effect="plain">精选</el-tag>
            <el-tag v-if="review.pkgName" size="small" type="info" effect="plain">{{ review.pkgName }}</el-tag>
          </div>
        </div>

        <div class="review-content">
          <p>{{ review.content }}</p>
          <div v-if="review.images && review.images.length" class="review-images">
            <img v-for="(img, i) in review.images" :key="i" :src="img" @click="previewImage(img)" />
          </div>
        </div>

        <!-- 商家回复 -->
        <div v-if="review.reply" class="review-reply">
          <div class="reply-label">
            <el-icon><Shop /></el-icon> 商家回复
          </div>
          <p>{{ review.reply }}</p>
        </div>

        <div class="review-actions">
          <el-button size="small" text type="primary" @click="openReplyDialog(review)">
            <el-icon><ChatDotRound /></el-icon> {{ review.reply ? '修改回复' : '回复' }}
          </el-button>
          <el-button size="small" text :type="review.featured ? 'info' : 'warning'" @click="toggleFeatured(review)">
            <el-icon><Star /></el-icon> {{ review.featured ? '取消精选' : '设为精选' }}
          </el-button>
          <el-button size="small" text type="danger" @click="deleteReview(review)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>

      <el-empty v-if="filteredReviews.length === 0" description="暂无评价" />
    </div>

    <!-- 回复弹窗 -->
    <el-dialog v-model="replyDialogVisible" title="回复评价" width="500px">
      <div v-if="replyingReview" class="reply-dialog-content">
        <div class="reply-review-summary">
          <strong>{{ replyingReview.customerName }}</strong> 的评价：
          <p style="color: #666; margin-top: 6px;">{{ replyingReview.content }}</p>
        </div>
        <el-input v-model="replyText" type="textarea" :rows="4" placeholder="输入您的回复..." maxlength="500" show-word-limit />
      </div>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :disabled="!replyText.trim()">发送回复</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" width="auto" :show-close="true" class="preview-dialog">
      <img :src="previewSrc" style="max-width: 100%; max-height: 80vh;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search, Star, StarFilled, ChatDotRound, UserFilled, Warning, Delete, Shop } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const REVIEWS_KEY = 'weiai_admin_reviews'
function loadReviews() {
  try { return JSON.parse(localStorage.getItem(REVIEWS_KEY) || '[]') } catch { return [] }
}
function saveReviewsToStorage(data) {
  localStorage.setItem(REVIEWS_KEY, JSON.stringify(data))
}

const reviews = ref(loadReviews().length > 0 ? loadReviews() : getDefaults())
const searchKey = ref('')
const filterRating = ref('')
const filterStatus = ref('')
const replyDialogVisible = ref(false)
const replyingReview = ref(null)
const replyText = ref('')
const previewVisible = ref(false)
const previewSrc = ref('')

function getDefaults() {
  const list = [
    { id: '1', customerName: '李小姐', avatar: '', date: '2026-02-20', rating: 5, content: '非常满意！摄影师非常专业，化妆师也很棒，拍了一整天都没有觉得累。成片效果超出预期，每一张都很喜欢！强烈推荐给准新娘们！', pkgName: '经典套餐', images: ['https://picsum.photos/seed/r1/200/200', 'https://picsum.photos/seed/r2/200/200'], reply: '感谢您的认可！能拍出您满意的照片是我们最大的动力，祝您新婚快乐！❤️', replied: true, featured: true },
    { id: '2', customerName: '张先生', avatar: '', date: '2026-02-18', rating: 5, content: '从预约到拍摄到出片，整个流程都很顺畅。客服很耐心，摄影团队也很有经验。照片质量很高，已经推荐给身边的朋友了。', pkgName: '奢华套餐', images: [], reply: '感谢张先生的好评和推荐！我们会继续努力，期待您朋友的光临！', replied: true, featured: true },
    { id: '3', customerName: '王女士', avatar: '', date: '2026-02-15', rating: 4, content: '整体不错，照片质量很好。但是当天场地有些拥挤，等待时间比较长，希望能改善一下档期安排。', pkgName: '轻享套餐', images: ['https://picsum.photos/seed/r3/200/200'], reply: '', replied: false, featured: false },
    { id: '4', customerName: '赵先生 & 陈小姐', avatar: '', date: '2026-02-10', rating: 5, content: '至尊套餐真的物超所值！两天的拍摄体验非常棒，VIP服务很贴心，每个细节都照顾到了。照片简直是大片级别，已经在朋友圈收获了无数赞！', pkgName: '至尊套餐', images: ['https://picsum.photos/seed/r4/200/200', 'https://picsum.photos/seed/r5/200/200', 'https://picsum.photos/seed/r6/200/200'], reply: '感谢二位的认可！至尊套餐是我们的匠心之作，很高兴能给您完美的体验！', replied: true, featured: true },
    { id: '5', customerName: '刘女士', avatar: '', date: '2026-02-05', rating: 3, content: '照片质量还可以，但感觉精修的数量有点少，有些照片的色调也不太满意。希望后期能多沟通一下风格偏好。', pkgName: '轻享套餐', images: [], reply: '', replied: false, featured: false },
    { id: '6', customerName: '周先生', avatar: '', date: '2026-01-28', rating: 5, content: '海景婚纱照拍得太美了！摄影师很会捕捉瞬间，夕阳下的照片简直像电影画面。化妆师的造型也很时尚，妻子非常开心！', pkgName: '经典套餐', images: ['https://picsum.photos/seed/r7/200/200'], reply: '谢谢周先生的好评！海景拍摄确实是我们的特色项目，祝您和太太百年好合！', replied: true, featured: false }
  ]
  saveReviewsToStorage(list)
  return list
}

const avgRating = computed(() => {
  if (!reviews.value.length) return '0.0'
  return (reviews.value.reduce((s, r) => s + r.rating, 0) / reviews.value.length).toFixed(1)
})

const filteredReviews = computed(() => {
  let list = reviews.value
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    list = list.filter(r => r.customerName.toLowerCase().includes(key) || r.content.toLowerCase().includes(key))
  }
  if (filterRating.value) {
    if (filterRating.value === 2) {
      list = list.filter(r => r.rating <= 2)
    } else {
      list = list.filter(r => r.rating === filterRating.value)
    }
  }
  if (filterStatus.value === 'unreplied') list = list.filter(r => !r.replied)
  if (filterStatus.value === 'replied') list = list.filter(r => r.replied)
  if (filterStatus.value === 'featured') list = list.filter(r => r.featured)
  return list
})

function openReplyDialog(review) {
  replyingReview.value = review
  replyText.value = review.reply || ''
  replyDialogVisible.value = true
}

function submitReply() {
  if (!replyText.value.trim() || !replyingReview.value) return
  replyingReview.value.reply = replyText.value.trim()
  replyingReview.value.replied = true
  saveReviewsToStorage(reviews.value)
  replyDialogVisible.value = false
  ElMessage.success('回复成功')
}

function toggleFeatured(review) {
  review.featured = !review.featured
  saveReviewsToStorage(reviews.value)
  ElMessage.success(review.featured ? '已设为精选' : '已取消精选')
}

function deleteReview(review) {
  ElMessageBox.confirm(`确定删除「${review.customerName}」的评价吗？`, '删除确认', {
    confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    reviews.value = reviews.value.filter(r => r.id !== review.id)
    saveReviewsToStorage(reviews.value)
    ElMessage.success('已删除')
  }).catch(() => {})
}

function previewImage(src) {
  previewSrc.value = src
  previewVisible.value = true
}
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  display: block;
  font-size: 1.4rem;
  font-weight: 700;
  color: #1a1a2e;
}

.stat-label {
  font-size: 0.82rem;
  color: #999;
  margin-top: 2px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  background: #fff;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s;
}

.review-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.review-card.featured {
  border-left: 3px solid #f7ba2a;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-name {
  display: block;
  font-weight: 700;
  font-size: 0.95rem;
  color: #333;
}

.review-date {
  display: block;
  font-size: 0.78rem;
  color: #aaa;
  margin-top: 2px;
}

.review-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.star-rating {
  display: flex;
  gap: 2px;
}

.review-content p {
  color: #555;
  line-height: 1.7;
  font-size: 0.92rem;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.review-images img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.review-images img:hover {
  transform: scale(1.1);
}

.review-reply {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 12px 16px;
  margin-top: 12px;
}

.reply-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.82rem;
  font-weight: 700;
  color: #e88a94;
  margin-bottom: 6px;
}

.review-reply p {
  color: #666;
  font-size: 0.88rem;
  line-height: 1.6;
}

.review-actions {
  display: flex;
  gap: 4px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.reply-review-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 16px;
  font-size: 0.9rem;
}

@media (max-width: 992px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .stats-row { grid-template-columns: 1fr; }
}
</style>
