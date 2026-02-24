<template>
  <div class="packages-page">
    <!-- 顶部 -->
    <div class="top-bar">
      <div class="pkg-stats">
        <span>共 <strong>{{ packages.length }}</strong> 个套餐</span>
        <span class="divider">|</span>
        <span>上架中 <strong>{{ packages.filter(p => p.online).length }}</strong></span>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增套餐
      </el-button>
    </div>

    <!-- 套餐卡片列表 -->
    <div class="pkg-grid">
      <div class="pkg-card" v-for="pkg in packages" :key="pkg.id" :class="{ offline: !pkg.online }">
        <div class="pkg-header" :style="{ background: pkg.color }">
          <div class="pkg-price">
            <span class="currency">¥</span>
            <span class="price-num">{{ pkg.price.toLocaleString() }}</span>
          </div>
          <span class="pkg-name">{{ pkg.name }}</span>
          <el-tag v-if="pkg.hot" type="danger" size="small" effect="dark" class="hot-tag">热门</el-tag>
          <span v-if="!pkg.online" class="offline-tag">已下架</span>
        </div>
        <div class="pkg-body">
          <ul class="pkg-features">
            <li v-for="(f, i) in pkg.features" :key="i">
              <el-icon color="#67C23A"><CircleCheck /></el-icon>
              <span>{{ f }}</span>
            </li>
          </ul>
        </div>
        <div class="pkg-footer">
          <el-button size="small" text type="primary" @click="editPkg(pkg)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button size="small" text :type="pkg.online ? 'warning' : 'success'" @click="toggleOnline(pkg)">
            <el-icon><component :is="pkg.online ? 'SoldOut' : 'Sell'" /></el-icon>
            {{ pkg.online ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" text type="danger" @click="deletePkg(pkg)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingPkg ? '编辑套餐' : '新增套餐'" width="580px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="90px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="formData.name" placeholder="例：尊享婚纱套餐" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="套餐价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :step="100" controls-position="right" style="width: 200px;" />
          <span style="margin-left: 8px; color: #999;">元</span>
        </el-form-item>
        <el-form-item label="主题色">
          <el-color-picker v-model="formData.color" />
          <span style="margin-left: 12px; color: #999; font-size: 0.85rem;">{{ formData.color }}</span>
        </el-form-item>
        <el-form-item label="套餐内容" prop="features">
          <div class="features-edit">
            <div class="feature-row" v-for="(f, i) in formData.features" :key="i">
              <el-input v-model="formData.features[i]" placeholder="例：含3套服装" />
              <el-button text type="danger" @click="removeFeature(i)" :disabled="formData.features.length <= 1">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button text type="primary" @click="addFeature" :disabled="formData.features.length >= 10">
              <el-icon><Plus /></el-icon> 添加一项
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="标记热门">
          <el-switch v-model="formData.hot" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item label="上架状态">
          <el-switch v-model="formData.online" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePkg">{{ editingPkg ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus, Edit, Delete, CircleCheck, SoldOut, Sell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const PKG_KEY = 'weiai_packages'
function loadPkgs() {
  try { return JSON.parse(localStorage.getItem(PKG_KEY) || '[]') } catch { return [] }
}
function savePkgsToStorage(data) {
  localStorage.setItem(PKG_KEY, JSON.stringify(data))
}

const packages = ref(loadPkgs().length > 0 ? loadPkgs() : getDefaults())
const dialogVisible = ref(false)
const editingPkg = ref(null)
const formRef = ref(null)

const formData = reactive({
  name: '',
  price: 0,
  color: 'linear-gradient(135deg, #f1aeb5, #e88a94)',
  features: [''],
  hot: false,
  online: true
})

const formRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

function getDefaults() {
  const list = [
    { id: 'basic', name: '轻享套餐', price: 3999, color: 'linear-gradient(135deg, #a8c0ff, #3f7dff)', features: ['1套婚纱+1套礼服', '20张精修照片', '室内1个场景', '拍摄2小时', '赠送10寸相册1本'], hot: false, online: true },
    { id: 'classic', name: '经典套餐', price: 7999, color: 'linear-gradient(135deg, #f1aeb5, #e88a94)', features: ['2套婚纱+1套礼服+1套便装', '40张精修照片', '室内+室外各2个场景', '拍摄半天', '赠送12寸相册1本+摆台2个', '免费化妆造型'], hot: true, online: true },
    { id: 'luxury', name: '奢华套餐', price: 15999, color: 'linear-gradient(135deg, #c9a87c, #8B6914)', features: ['4套婚纱+2套礼服+2套便装', '80张精修照片', '4个室内+2个室外场景', '全天拍摄', '赠送18寸相册+24寸挂画', '高级化妆师+助理全程跟随', '赠送精修短视频1条'], hot: true, online: true },
    { id: 'supreme', name: '至尊套餐', price: 29999, color: 'linear-gradient(135deg, #1a1a2e, #16213e)', features: ['不限服装数量', '120张精修照片', '不限场景', '两天拍摄', '赠送全套相册+挂画+摆台', '明星化妆师团队', '精修短视频3条', '包含外景交通住宿', 'VIP专属1对1服务'], hot: false, online: true }
  ]
  savePkgsToStorage(list)
  return list
}

function openAddDialog() {
  editingPkg.value = null
  Object.assign(formData, { name: '', price: 0, color: 'linear-gradient(135deg, #f1aeb5, #e88a94)', features: [''], hot: false, online: true })
  dialogVisible.value = true
}

function editPkg(pkg) {
  editingPkg.value = pkg
  Object.assign(formData, { name: pkg.name, price: pkg.price, color: pkg.color, features: [...pkg.features], hot: pkg.hot, online: pkg.online })
  dialogVisible.value = true
}

function addFeature() {
  formData.features.push('')
}

function removeFeature(index) {
  formData.features.splice(index, 1)
}

function savePkg() {
  formRef.value?.validate((valid) => {
    if (!valid) return
    const cleanFeatures = formData.features.filter(f => f.trim())
    if (cleanFeatures.length === 0) {
      ElMessage.warning('请至少填写一项套餐内容')
      return
    }
    if (editingPkg.value) {
      Object.assign(editingPkg.value, { name: formData.name, price: formData.price, color: formData.color, features: cleanFeatures, hot: formData.hot, online: formData.online })
    } else {
      packages.value.push({
        id: Date.now().toString(36),
        name: formData.name,
        price: formData.price,
        color: formData.color,
        features: cleanFeatures,
        hot: formData.hot,
        online: formData.online
      })
    }
    savePkgsToStorage(packages.value)
    dialogVisible.value = false
    ElMessage.success(editingPkg.value ? '套餐已更新' : '套餐已创建')
  })
}

function toggleOnline(pkg) {
  pkg.online = !pkg.online
  savePkgsToStorage(packages.value)
  ElMessage.success(pkg.online ? '已上架' : '已下架')
}

function deletePkg(pkg) {
  ElMessageBox.confirm(`确定删除套餐「${pkg.name}」吗？`, '删除确认', {
    confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    packages.value = packages.value.filter(p => p.id !== pkg.id)
    savePkgsToStorage(packages.value)
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

.pkg-stats {
  color: #666;
  font-size: 0.9rem;
}

.pkg-stats strong { color: #1a1a2e; }

.divider {
  margin: 0 10px;
  color: #ddd;
}

.pkg-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.pkg-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.pkg-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.pkg-card.offline {
  opacity: 0.6;
}

.pkg-header {
  padding: 24px 20px;
  color: #fff;
  text-align: center;
  position: relative;
}

.pkg-price {
  margin-bottom: 6px;
}

.currency {
  font-size: 1.1rem;
  font-weight: 600;
  vertical-align: top;
  margin-right: 2px;
}

.price-num {
  font-size: 2.2rem;
  font-weight: 800;
  letter-spacing: -1px;
}

.pkg-name {
  display: block;
  font-size: 1rem;
  font-weight: 600;
  opacity: 0.9;
}

.hot-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.offline-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0,0,0,0.4);
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 0.75rem;
}

.pkg-body {
  padding: 20px;
}

.pkg-features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pkg-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.88rem;
  color: #555;
}

.pkg-footer {
  display: flex;
  justify-content: center;
  gap: 4px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

/* 编辑特性列表 */
.features-edit {
  width: 100%;
}

.feature-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.feature-row .el-input {
  flex: 1;
}

@media (max-width: 768px) {
  .pkg-grid {
    grid-template-columns: 1fr;
  }
}
</style>
