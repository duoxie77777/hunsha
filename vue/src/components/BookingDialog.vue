<template>
  <el-dialog
    v-model="userStore.showBooking"
    width="560px"
    :show-close="true"
    :close-on-click-modal="true"
    class="booking-dialog"
    align-center
  >
    <div class="booking-wrapper">
      <!-- 成功状态 -->
      <div v-if="submitted" class="booking-success">
        <div class="success-icon">
          <el-icon :size="64" color="var(--secondary-color)"><CircleCheckFilled /></el-icon>
        </div>
        <h3>预约提交成功！</h3>
        <p>订单号：<strong>{{ lastOrderId }}</strong></p>
        <p style="color: #999; font-size: 0.9rem; margin-top: 4px;">点击下方按钮与客服确认预约详情</p>
        <div class="success-info">
          <div class="success-info-item">
            <span class="label">联系电话</span>
            <span class="value">{{ userStore.user?.phone }}</span>
          </div>
          <div class="success-info-item">
            <span class="label">预约套餐</span>
            <span class="value">{{ pkgLabel }}</span>
          </div>
          <div class="success-info-item">
            <span class="label">预约日期</span>
            <span class="value">{{ form.date ? formatDate(form.date) : '待定' }}</span>
          </div>
        </div>
        <el-button type="primary" class="close-btn" @click="openChatWithOrder" style="margin-bottom: 10px;">
          <el-icon style="margin-right: 6px;"><ChatDotRound /></el-icon>联系客服确认
        </el-button>
        <el-button class="close-btn secondary-btn" @click="handleClose">稍后再说</el-button>
      </div>

      <!-- 表单 -->
      <div v-else>
        <div class="booking-header">
          <div class="booking-icon">
            <el-icon :size="40" color="#fff"><Calendar /></el-icon>
          </div>
          <h3>在线预约</h3>
          <p>填写以下信息，我们将尽快为您安排拍摄档期</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <div class="form-row">
            <el-form-item label="您的姓名" prop="name" class="form-half">
              <el-input v-model="form.name" placeholder="请输入姓名" size="large" :prefix-icon="User" />
            </el-form-item>
            <el-form-item label="伴侣姓名" class="form-half">
              <el-input v-model="form.partnerName" placeholder="请输入伴侣姓名" size="large" :prefix-icon="User" />
            </el-form-item>
          </div>

          <el-form-item label="联系电话">
            <el-input :model-value="userStore.user?.phone" disabled size="large" :prefix-icon="Phone" />
          </el-form-item>

          <div class="form-row">
            <el-form-item label="预约日期" prop="date" class="form-half">
              <el-date-picker
                v-model="form.date"
                placeholder="选择日期"
                size="large"
                style="width:100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>
            <el-form-item label="选择套餐" prop="packageId" class="form-half">
              <el-select v-model="form.packageId" placeholder="请选择套餐" size="large" style="width:100%">
                <el-option v-for="pkg in packageList" :key="pkg.id" :label="`${pkg.name} (¥${pkg.price})`" :value="pkg.id" />
              </el-select>
            </el-form-item>
          </div>

          <el-form-item label="拍摄风格偏好">
            <el-checkbox-group v-model="form.styles">
              <el-checkbox label="韩式清新" />
              <el-checkbox label="欧式浪漫" />
              <el-checkbox label="中式古典" />
              <el-checkbox label="森系自然" />
              <el-checkbox label="海景旅拍" />
              <el-checkbox label="城市街拍" />
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="备注信息">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请填写您的特殊需求或疑问（选填）" />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            提交预约
          </el-button>
        </el-form>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Calendar, User, Phone, CircleCheckFilled, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userStore } from '../stores/user'
import { getPackagesApi } from '../api/package'

const formRef = ref(null)
const loading = ref(false)
const submitted = ref(false)
const lastOrderId = ref('')
const packageList = ref([])

onMounted(async () => {
  try {
    const res = await getPackagesApi()
    packageList.value = res.data || []
  } catch {}
})

const form = reactive({
  name: '',
  partnerName: '',
  date: '',
  packageId: '',
  styles: [],
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入您的姓名', trigger: 'blur' }],
  date: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  packageId: [{ required: true, message: '请选择套餐', trigger: 'change' }]
}

const selectedPkg = computed(() => packageList.value.find(p => p.id === form.packageId))
const pkgLabel = computed(() => selectedPkg.value?.name || '')

function disabledDate(time) {
  return time.getTime() < Date.now() - 86400000
}

function formatDate(d) {
  const date = new Date(d)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

async function handleSubmit() {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const order = await userStore.addOrder({
        packageId: form.packageId,
        shootDate: form.date ? formatDate(form.date) : null,
        shootTime: null,
        contactName: form.name,
        contactPhone: userStore.user?.phone,
        remark: form.remark
      })
      lastOrderId.value = order.orderNo || order.id
      submitted.value = true
      ElMessage.success('预约提交成功！')
    } catch (e) {
      // error handled by interceptor
    } finally {
      loading.value = false
    }
  })
}

function openChatWithOrder() {
  userStore.showBooking = false
  setTimeout(() => {
    userStore.openChat(userStore.currentOrder)
  }, 300)
  resetForm()
}

function handleClose() {
  userStore.showBooking = false
  resetForm()
}

function resetForm() {
  submitted.value = false
  lastOrderId.value = ''
  Object.assign(form, { name: '', partnerName: '', date: '', packageId: '', styles: [], remark: '' })
}
</script>

<style scoped>
.booking-wrapper {
  padding: 5px 5px 0;
}

.booking-header {
  text-align: center;
  margin-bottom: 30px;
}

.booking-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--secondary-color), #e88a94);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 8px 20px rgba(241, 174, 181, 0.4);
}

.booking-header h3 {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 6px;
}

.booking-header p {
  color: #999;
  font-size: 0.9rem;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-half {
  flex: 1;
}

.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--secondary-color), #e88a94) !important;
  border: none !important;
  font-weight: 600;
  font-size: 1rem;
  height: 46px;
  border-radius: 8px;
  margin-top: 5px;
}

.submit-btn:hover {
  opacity: 0.9;
}

/* 成功状态 */
.booking-success {
  text-align: center;
  padding: 20px 0 10px;
}

.success-icon {
  margin-bottom: 15px;
}

.booking-success h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 8px;
}

.booking-success > p {
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 25px;
}

.success-info {
  background: #fdf5f6;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
}

.success-info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid rgba(241, 174, 181, 0.2);
}

.success-info-item:last-child {
  border-bottom: none;
}

.success-info-item .label {
  color: #999;
  font-size: 0.9rem;
}

.success-info-item .value {
  font-weight: 600;
  color: var(--dark-color);
}

.close-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--secondary-color), #e88a94) !important;
  border: none !important;
  font-weight: 600;
  height: 46px;
  border-radius: 8px;
}

.secondary-btn {
  background: #fff !important;
  border: 1px solid #ddd !important;
  color: #666 !important;
}

.secondary-btn:hover {
  border-color: var(--secondary-color) !important;
  color: var(--secondary-color) !important;
}

:deep(.el-dialog__header) {
  display: none;
}

:deep(.el-dialog) {
  border-radius: 16px !important;
  overflow: hidden;
}

:deep(.el-dialog__body) {
  padding: 30px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--dark-color);
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
