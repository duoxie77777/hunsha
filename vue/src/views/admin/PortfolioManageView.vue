<template>
  <div class="portfolio-manage-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="top-left">
        <el-select v-model="filterCategory" placeholder="全部分类" clearable style="width: 150px;">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-input v-model="searchKey" placeholder="搜索作品标题" :prefix-icon="Search" clearable style="width: 240px;" />
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 上传作品
      </el-button>
    </div>

    <!-- 作品网格 -->
    <div v-if="filteredWorks.length > 0" class="works-grid">
      <div class="work-card" v-for="work in filteredWorks" :key="work.id" :class="{ featured: work.featured }">
        <div class="work-cover">
          <img :src="work.cover" :alt="work.title" />
          <div class="work-overlay">
            <el-button circle size="small" type="primary" @click="toggleFeatured(work)" :title="work.featured ? '取消推荐' : '设为推荐'">
              <el-icon><Star /></el-icon>
            </el-button>
            <el-button circle size="small" type="info" @click="editWork(work)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button circle size="small" type="danger" @click="deleteWork(work)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <span v-if="work.featured" class="featured-badge">
            <el-icon><Star /></el-icon> 推荐
          </span>
        </div>
        <div class="work-info">
          <h4>{{ work.title }}</h4>
          <div class="work-meta">
            <el-tag size="small" type="info" effect="plain">{{ work.category }}</el-tag>
            <span class="work-date">{{ work.date }}</span>
          </div>
          <p class="work-desc">{{ work.description }}</p>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无作品，点击上方按钮上传" />

    <!-- 上传/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingWork ? '编辑作品' : '上传新作品'" width="560px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="封面图" prop="cover">
          <div class="cover-upload" @click="triggerCoverUpload">
            <img v-if="formData.cover" :src="formData.cover" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon :size="32"><Plus /></el-icon>
              <span>点击上传封面</span>
            </div>
            <input ref="coverInputRef" type="file" accept="image/*" @change="handleCoverUpload" style="display:none;" />
          </div>
        </el-form-item>
        <el-form-item label="作品标题" prop="title">
          <el-input v-model="formData.title" placeholder="例：海边夕阳婚纱照" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="选择分类" style="width: 100%;">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="拍摄日期">
          <el-date-picker v-model="formData.date" type="date" placeholder="选择日期" style="width: 100%;" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="作品描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="简要描述这组作品" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="推荐展示">
          <el-switch v-model="formData.featured" active-text="是" inactive-text="否" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveWork" :loading="saving">{{ editingWork ? '保存修改' : '上传作品' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { Search, Plus, Star, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const WORKS_KEY = 'weiai_works'
function loadWorks() {
  try { return JSON.parse(localStorage.getItem(WORKS_KEY) || '[]') } catch { return [] }
}
function saveWorksToStorage(data) {
  localStorage.setItem(WORKS_KEY, JSON.stringify(data))
}

const categories = ['韩式婚纱', '中式婚纱', '森系清新', '海景婚纱', '城市街拍', '复古胶片', '轻奢简约', '旅拍纪实']

const works = ref(loadWorks().length > 0 ? loadWorks() : getDefaultWorks())
const searchKey = ref('')
const filterCategory = ref('')
const dialogVisible = ref(false)
const editingWork = ref(null)
const saving = ref(false)
const formRef = ref(null)
const coverInputRef = ref(null)

const formData = reactive({
  cover: '',
  title: '',
  category: '',
  date: '',
  description: '',
  featured: false
})

const rules = {
  cover: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const filteredWorks = computed(() => {
  let list = works.value
  if (filterCategory.value) list = list.filter(w => w.category === filterCategory.value)
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    list = list.filter(w => w.title.toLowerCase().includes(key))
  }
  return list
})

function getDefaultWorks() {
  const defaults = [
    { id: '1', cover: 'https://picsum.photos/seed/wed1/400/300', title: '浪漫海岸线', category: '海景婚纱', date: '2026-01-15', description: '三亚海棠湾日落时分拍摄，金色光线与碧蓝海水完美搭配', featured: true },
    { id: '2', cover: 'https://picsum.photos/seed/wed2/400/300', title: '古镇晨曦', category: '中式婚纱', date: '2026-01-20', description: '乌镇清晨拍摄，青石板路上的中式嫁衣别有韵味', featured: true },
    { id: '3', cover: 'https://picsum.photos/seed/wed3/400/300', title: '首尔恋曲', category: '韩式婚纱', date: '2025-12-28', description: '韩式唯美风格，粉色樱花树下的浪漫', featured: false },
    { id: '4', cover: 'https://picsum.photos/seed/wed4/400/300', title: '森林之约', category: '森系清新', date: '2025-12-10', description: '自然光线下的森系婚纱照，清新脱俗', featured: false },
    { id: '5', cover: 'https://picsum.photos/seed/wed5/400/300', title: '都市光影', category: '城市街拍', date: '2026-02-01', description: '上海外滩城市街拍，现代与经典的碰撞', featured: true },
    { id: '6', cover: 'https://picsum.photos/seed/wed6/400/300', title: '胶片时光', category: '复古胶片', date: '2025-11-22', description: '复古胶片质感，记录最真实的幸福瞬间', featured: false }
  ]
  saveWorksToStorage(defaults)
  return defaults
}

function openAddDialog() {
  editingWork.value = null
  Object.assign(formData, { cover: '', title: '', category: '', date: '', description: '', featured: false })
  dialogVisible.value = true
}

function editWork(work) {
  editingWork.value = work
  Object.assign(formData, { cover: work.cover, title: work.title, category: work.category, date: work.date, description: work.description, featured: work.featured })
  dialogVisible.value = true
}

function triggerCoverUpload() {
  coverInputRef.value?.click()
}

function handleCoverUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = (ev) => {
    formData.cover = ev.target.result
  }
  reader.readAsDataURL(file)
}

function saveWork() {
  formRef.value?.validate((valid) => {
    if (!valid) return
    saving.value = true
    setTimeout(() => {
      if (editingWork.value) {
        Object.assign(editingWork.value, { ...formData })
      } else {
        works.value.unshift({
          id: Date.now().toString(36),
          ...formData
        })
      }
      saveWorksToStorage(works.value)
      saving.value = false
      dialogVisible.value = false
      ElMessage.success(editingWork.value ? '作品已更新' : '作品已上传')
    }, 500)
  })
}

function toggleFeatured(work) {
  work.featured = !work.featured
  saveWorksToStorage(works.value)
  ElMessage.success(work.featured ? '已设为推荐' : '已取消推荐')
}

function deleteWork(work) {
  ElMessageBox.confirm(`确定删除作品「${work.title}」吗？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    works.value = works.value.filter(w => w.id !== work.id)
    saveWorksToStorage(works.value)
    ElMessage.success('已删除')
  }).catch(() => {})
}
</script>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  background: #fff;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.top-left {
  display: flex;
  gap: 12px;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.work-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.work-card.featured {
  border: 2px solid #e88a94;
}

.work-cover {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.work-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.work-card:hover .work-cover img {
  transform: scale(1.05);
}

.work-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.work-card:hover .work-overlay {
  opacity: 1;
}

.featured-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #e88a94, #f1aeb5);
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
}

.work-info {
  padding: 14px 16px 16px;
}

.work-info h4 {
  font-size: 1rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.work-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.work-date {
  font-size: 0.78rem;
  color: #aaa;
}

.work-desc {
  font-size: 0.82rem;
  color: #888;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 封面上传 */
.cover-upload {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-upload:hover {
  border-color: #e88a94;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #ccc;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 14px;
  }
}
</style>
