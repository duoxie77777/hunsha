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
        <el-form-item label="套餐描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="简要描述套餐" maxlength="200" show-word-limit />
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
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, CircleCheck, SoldOut, Sell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminGetPackagesApi, adminCreatePackageApi, adminUpdatePackageApi, adminDeletePackageApi, adminTogglePackageStatusApi } from '../../api/package'
import { uploadImageApi } from '../../api/upload'

const packages = ref([])
const dialogVisible = ref(false)
const editingPkg = ref(null)
const formRef = ref(null)

const formData = reactive({
  name: '',
  price: 0,
  description: '',
  coverImage: '',
  features: [''],
  hot: false,
  online: true
})

const formRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

async function loadPackages() {
  try {
    const res = await adminGetPackagesApi({ page: 1, size: 100 })
    if (res.data?.code === 200) {
      packages.value = (res.data.data?.records || []).map(p => ({
        ...p,
        features: p.includes || [],
        hot: p.isHot === 1,
        online: p.status === 1,
        color: 'linear-gradient(135deg, #f1aeb5, #e88a94)'
      }))
    }
  } catch (e) { console.error(e) }
}

onMounted(() => loadPackages())

function openAddDialog() {
  editingPkg.value = null
  Object.assign(formData, { name: '', price: 0, description: '', coverImage: '', features: [''], hot: false, online: true })
  dialogVisible.value = true
}

function editPkg(pkg) {
  editingPkg.value = pkg
  Object.assign(formData, {
    name: pkg.name,
    price: Number(pkg.price),
    description: pkg.description || '',
    coverImage: pkg.coverImage || '',
    features: pkg.features?.length ? [...pkg.features] : [''],
    hot: pkg.hot,
    online: pkg.online
  })
  dialogVisible.value = true
}

function addFeature() {
  formData.features.push('')
}

function removeFeature(index) {
  formData.features.splice(index, 1)
}

async function savePkg() {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    const cleanFeatures = formData.features.filter(f => f.trim())
    if (cleanFeatures.length === 0) {
      ElMessage.warning('请至少填写一项套餐内容')
      return
    }
    const payload = {
      name: formData.name,
      price: formData.price,
      description: formData.description,
      coverImage: formData.coverImage,
      includes: cleanFeatures,
      isHot: formData.hot ? 1 : 0,
      status: formData.online ? 1 : 0
    }
    try {
      if (editingPkg.value) {
        const res = await adminUpdatePackageApi(editingPkg.value.id, payload)
        if (res.data?.code === 200) {
          ElMessage.success('套餐已更新')
          dialogVisible.value = false
          loadPackages()
        } else {
          ElMessage.error(res.data?.msg || '更新失败')
        }
      } else {
        const res = await adminCreatePackageApi(payload)
        if (res.data?.code === 200) {
          ElMessage.success('套餐已创建')
          dialogVisible.value = false
          loadPackages()
        } else {
          ElMessage.error(res.data?.msg || '创建失败')
        }
      }
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
}

async function toggleOnline(pkg) {
  try {
    const newStatus = pkg.online ? 0 : 1
    const res = await adminTogglePackageStatusApi(pkg.id, newStatus)
    if (res.data?.code === 200) {
      pkg.online = !pkg.online
      pkg.status = newStatus
      ElMessage.success(pkg.online ? '已上架' : '已下架')
    } else {
      ElMessage.error(res.data?.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function deletePkg(pkg) {
  try {
    await ElMessageBox.confirm(`确定删除套餐「${pkg.name}」吗？`, '删除确认', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
    const res = await adminDeletePackageApi(pkg.id)
    if (res.data?.code === 200) {
      ElMessage.success('已删除')
      loadPackages()
    } else {
      ElMessage.error(res.data?.msg || '删除失败')
    }
  } catch (e) {}
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
