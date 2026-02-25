<template>
  <div class="customers-page">
    <!-- 顶部统计 -->
    <div class="stats-row">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-icon" :style="{ background: s.bg }">
          <el-icon :size="22" :color="s.color"><component :is="s.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input v-model="searchKey" placeholder="搜索客户姓名/手机号" :prefix-icon="Search" clearable style="width: 280px;" />
      <el-select v-model="filterTag" placeholder="客户标签" clearable style="width: 160px;">
        <el-option label="VIP客户" value="VIP" />
        <el-option label="意向客户" value="意向" />
        <el-option label="已成交" value="已成交" />
        <el-option label="普通客户" value="普通" />
      </el-select>
      <div class="filter-right">
        <span class="total">共 <strong>{{ filteredCustomers.length }}</strong> 位客户</span>
      </div>
    </div>

    <!-- 客户列表 -->
    <el-table :data="filteredCustomers" stripe style="width: 100%;" class="customer-table" @row-click="openDetail">
      <el-table-column label="客户" min-width="200">
        <template #default="{ row }">
          <div class="customer-cell">
            <el-avatar :size="36" :src="row.avatar || ''">
              <el-icon :size="18"><UserFilled /></el-icon>
            </el-avatar>
            <div class="customer-name-wrap">
              <span class="customer-name">{{ row.nickname || row.name || '未设置昵称' }}</span>
              <span class="customer-phone">{{ row.phone }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="标签" width="120">
        <template #default="{ row }">
          <el-tag :type="tagType(row.tag)" size="small" effect="plain">{{ row.tag || '普通' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="订单数" width="100" align="center">
        <template #default="{ row }">
          <span class="order-count">{{ row.orderCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="消费金额" width="130" align="right">
        <template #default="{ row }">
          <span class="amount">¥{{ row.totalAmount.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最近联系" width="160" prop="lastContact" />
      <el-table-column label="注册时间" width="160" prop="registerTime" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click.stop="openChat(row)">
            <el-icon><ChatDotRound /></el-icon> 聊天
          </el-button>
          <el-button size="small" text type="info" @click.stop="editTag(row)">
            <el-icon><PriceTag /></el-icon> 标签
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 客户详情抽屉 -->
    <el-drawer v-model="detailVisible" :title="detailCustomer?.nickname || '客户详情'" size="480px" direction="rtl">
      <div v-if="detailCustomer" class="customer-detail">
        <div class="detail-header">
          <el-avatar :size="72" :src="detailCustomer.avatar || ''">
            <el-icon :size="36"><UserFilled /></el-icon>
          </el-avatar>
          <h3>{{ detailCustomer.nickname || detailCustomer.name || '未设置昵称' }}</h3>
          <p class="detail-phone">{{ detailCustomer.phone }}</p>
          <el-tag :type="tagType(detailCustomer.tag)" effect="plain" style="margin-top: 8px;">{{ detailCustomer.tag || '普通客户' }}</el-tag>
        </div>

        <el-divider />

        <div class="detail-info-grid">
          <div class="detail-info-item">
            <span class="label">年龄</span>
            <span class="value">{{ detailCustomer.age || '未填写' }}</span>
          </div>
          <div class="detail-info-item">
            <span class="label">个人简介</span>
            <span class="value">{{ detailCustomer.bio || '暂无' }}</span>
          </div>
          <div class="detail-info-item">
            <span class="label">注册时间</span>
            <span class="value">{{ detailCustomer.registerTime || detailCustomer.createdAt }}</span>
          </div>
          <div class="detail-info-item">
            <span class="label">最近联系</span>
            <span class="value">{{ detailCustomer.lastContact }}</span>
          </div>
          <div class="detail-info-item">
            <span class="label">订单数量</span>
            <span class="value highlight">{{ detailCustomer.orderCount }} 单</span>
          </div>
          <div class="detail-info-item">
            <span class="label">累计消费</span>
            <span class="value highlight">¥{{ detailCustomer.totalAmount.toLocaleString() }}</span>
          </div>
        </div>

        <el-divider content-position="left">订单记录</el-divider>

        <div v-if="detailCustomer.orders.length === 0" class="empty-orders">暂无订单</div>
        <div v-else class="detail-orders">
          <div class="detail-order-card" v-for="o in detailCustomer.orders" :key="o.id">
            <div class="order-card-top">
              <span class="order-id">{{ o.orderNo }}</span>
              <el-tag :type="orderStatusType(o.status)" size="small">{{ o.status }}</el-tag>
            </div>
            <div class="order-card-body">
              <span>{{ o.packageName }}</span>
              <span>{{ o.shootDate }}</span>
            </div>
          </div>
        </div>

        <div class="detail-actions">
          <el-button type="primary" @click="openChat(detailCustomer)">
            <el-icon><ChatDotRound /></el-icon> 发送消息
          </el-button>
          <el-button @click="editTag(detailCustomer)">
            <el-icon><PriceTag /></el-icon> 修改标签
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 标签编辑弹窗 -->
    <el-dialog v-model="tagDialogVisible" title="修改客户标签" width="400px">
      <el-radio-group v-model="editingTag" size="large">
        <el-radio-button label="VIP">VIP客户</el-radio-button>
        <el-radio-button label="意向">意向客户</el-radio-button>
        <el-radio-button label="已成交">已成交</el-radio-button>
        <el-radio-button label="普通">普通客户</el-radio-button>
      </el-radio-group>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTag">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, UserFilled, ChatDotRound, PriceTag, User as UserIcon, Ticket, Money, TrendCharts } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCustomersApi, getCustomerDetailApi, updateCustomerStatusApi } from '../../api/admin'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKey = ref('')
const filterTag = ref('')
const detailVisible = ref(false)
const detailCustomer = ref(null)
const tagDialogVisible = ref(false)
const editingTag = ref('普通')
const editingCustomer = ref(null)

const customers = ref([])
const totalCount = ref(0)

async function loadCustomers() {
  try {
    const params = { page: 1, size: 100 }
    if (searchKey.value.trim()) params.keyword = searchKey.value.trim()
    const res = await getCustomersApi(params)
    const records = res.data?.records || []
    customers.value = records.map(u => ({
      ...u,
      name: u.nickname || u.username || '未设置昵称',
      tag: u.status === 1 ? '普通' : '禁用',
      orderCount: 0,
      totalAmount: 0,
      lastContact: u.createdAt || '-',
      registerTime: u.createdAt || '-',
      orders: []
    }))
    totalCount.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

onMounted(() => loadCustomers())

let searchTimer = null
watch(searchKey, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadCustomers(), 300)
})

const filteredCustomers = computed(() => {
  let list = customers.value
  if (filterTag.value) {
    list = list.filter(c => c.tag === filterTag.value)
  }
  return list
})

const stats = computed(() => [
  { label: '总客户数', value: totalCount.value, icon: UserIcon, bg: 'rgba(64,158,255,0.1)', color: '#409EFF' },
  { label: '正常状态', value: customers.value.filter(c => c.status === 1).length, icon: Ticket, bg: 'rgba(230,162,60,0.1)', color: '#E6A23C' },
  { label: '已禁用', value: customers.value.filter(c => c.status === 0).length, icon: Money, bg: 'rgba(103,194,58,0.1)', color: '#67C23A' },
  { label: '总数量', value: totalCount.value, icon: TrendCharts, bg: 'rgba(241,174,181,0.1)', color: '#e88a94' }
])

function tagType(tag) {
  const map = { 'VIP': 'warning', '意向': '', '已成交': 'success', '普通': 'info', '禁用': 'danger' }
  return map[tag] || 'info'
}

function orderStatusType(status) {
  const map = { '待确认': 'warning', '已确认': '', '已完成': 'success', '已取消': 'info' }
  return map[status] || 'info'
}

async function openDetail(row) {
  try {
    const res = await getCustomerDetailApi(row.id)
    const data = res.data
    const user = data.user || row
    const orders = data.orders || []
    detailCustomer.value = {
      ...row,
      ...user,
      name: user.nickname || user.username || '未设置昵称',
      orders,
      orderCount: orders.length,
      totalAmount: orders.reduce((s, o) => s + (Number(o.amount) || 0), 0)
    }
  } catch (e) {
    detailCustomer.value = row
  }
  detailVisible.value = true
}

function openChat(customer) {
  detailVisible.value = false
  router.push({ path: '/admin/chat', query: { phone: customer.phone } })
}

function editTag(customer) {
  editingCustomer.value = customer
  editingTag.value = customer.tag || '普通'
  tagDialogVisible.value = true
}

async function saveTag() {
  if (editingCustomer.value) {
    try {
      const newStatus = editingTag.value === '禁用' ? 0 : 1
      await updateCustomerStatusApi(editingCustomer.value.id, newStatus)
      editingCustomer.value.status = newStatus
      editingCustomer.value.tag = editingTag.value
      ElMessage.success('状态已更新')
    } catch (e) {
      ElMessage.error('更新失败')
    }
  }
  tagDialogVisible.value = false
}
</script>

<style scoped>
.customers-page { }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
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

.filter-right {
  margin-left: auto;
  color: #999;
  font-size: 0.85rem;
}

.customer-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.customer-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-name-wrap {
  display: flex;
  flex-direction: column;
}

.customer-name {
  font-weight: 600;
  font-size: 0.9rem;
  color: #333;
}

.customer-phone {
  font-size: 0.78rem;
  color: #aaa;
  margin-top: 2px;
}

.order-count {
  font-weight: 700;
  color: #409EFF;
}

.amount {
  font-weight: 700;
  color: #e88a94;
}

/* 详情抽屉 */
.customer-detail {
  padding: 0 4px;
}

.detail-header {
  text-align: center;
  padding: 10px 0 0;
}

.detail-header h3 {
  margin-top: 12px;
  font-size: 1.2rem;
  color: #1a1a2e;
}

.detail-phone {
  color: #999;
  font-size: 0.9rem;
  margin-top: 4px;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.detail-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-info-item .label {
  font-size: 0.78rem;
  color: #aaa;
}

.detail-info-item .value {
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.detail-info-item .value.highlight {
  color: #e88a94;
}

.detail-orders {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 240px;
  overflow-y: auto;
}

.detail-order-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 12px 14px;
}

.order-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.order-id {
  font-weight: 700;
  font-size: 0.85rem;
  color: #333;
}

.order-card-body {
  display: flex;
  justify-content: space-between;
  font-size: 0.82rem;
  color: #888;
}

.empty-orders {
  text-align: center;
  padding: 20px;
  color: #ccc;
  font-size: 0.9rem;
}

.detail-actions {
  display: flex;
  gap: 10px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.detail-actions .el-button {
  flex: 1;
}

@media (max-width: 992px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .stats-row { grid-template-columns: 1fr; }
}
</style>
