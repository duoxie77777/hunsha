<template>
  <div class="orders-page">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input v-model="search" placeholder="搜索订单号/客户名" clearable size="large" style="width:260px" :prefix-icon="Search" />
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable size="large" style="width:140px">
          <el-option label="全部" value="" />
          <el-option label="待确认" value="待确认" />
          <el-option label="已确认" value="已确认" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </div>
      <span class="order-count">共 {{ totalCount }} 条</span>
    </div>

    <!-- 订单表格 -->
    <div class="table-card">
      <el-table :data="orderList" stripe style="width:100%" empty-text="暂无订单" row-class-name="order-row">
        <el-table-column prop="orderNo" label="订单号" width="170">
          <template #default="{ row }">
            <span class="id-cell">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="客户" width="100" />
        <el-table-column prop="contactPhone" label="电话" width="130" />
        <el-table-column prop="packageName" label="套餐" min-width="130" />
        <el-table-column prop="shootDate" label="预约日期" width="110" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            <span style="color:#e88a94;font-weight:700">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small" round>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === '待确认'" type="primary" size="small" plain @click="confirmOrder(row)">确认</el-button>
            <el-button v-if="row.status === '已确认'" type="success" size="small" plain @click="completeOrder(row)">完成</el-button>
            <el-button size="small" plain @click="chatWithUser(row)">
              <el-icon style="margin-right:4px"><ChatDotRound /></el-icon>聊天
            </el-button>
            <el-button v-if="row.status === '待确认'" type="danger" size="small" plain @click="rejectOrder(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" width="520px" title="订单详情" align-center>
      <div v-if="selectedOrder" class="detail-grid">
        <div class="detail-item"><span class="label">订单号</span><span class="value mono">{{ selectedOrder.orderNo }}</span></div>
        <div class="detail-item"><span class="label">客户姓名</span><span class="value">{{ selectedOrder.contactName }}</span></div>
        <div class="detail-item"><span class="label">联系电话</span><span class="value">{{ selectedOrder.contactPhone }}</span></div>
        <div class="detail-item"><span class="label">套餐</span><span class="value">{{ selectedOrder.packageName }}</span></div>
        <div class="detail-item"><span class="label">金额</span><span class="value" style="color:#e88a94">¥{{ selectedOrder.amount }}</span></div>
        <div class="detail-item"><span class="label">预约日期</span><span class="value">{{ selectedOrder.shootDate }}</span></div>
        <div class="detail-item"><span class="label">拍摄时间</span><span class="value">{{ selectedOrder.shootTime || '未指定' }}</span></div>
        <div class="detail-item"><span class="label">状态</span><el-tag :type="statusType(selectedOrder.status)" size="small" round>{{ selectedOrder.status }}</el-tag></div>
        <div v-if="selectedOrder.remark" class="detail-item full"><span class="label">备注</span><span class="value">{{ selectedOrder.remark }}</span></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Search, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminGetOrdersApi, adminUpdateOrderStatusApi } from '../../api/order'
import { useRouter } from 'vue-router'

const router = useRouter()
const search = ref('')
const statusFilter = ref('')
const detailVisible = ref(false)
const selectedOrder = ref(null)
const orderList = ref([])
const totalCount = ref(0)

async function loadOrders() {
  try {
    const params = { page: 1, size: 100 }
    if (statusFilter.value) params.status = statusFilter.value
    if (search.value.trim()) params.contactName = search.value.trim()
    const res = await adminGetOrdersApi(params)
    orderList.value = res.data?.records || []
    totalCount.value = res.data?.total || 0
  } catch (e) { console.error(e) }
}

onMounted(() => loadOrders())

let searchTimer = null
watch([search, statusFilter], () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadOrders(), 300)
})

function statusType(s) {
  return { '待确认': 'warning', '已确认': 'success', '已完成': 'info', '已取消': 'danger' }[s] || 'info'
}

async function confirmOrder(order) {
  try {
    await ElMessageBox.confirm(`确认接受订单 ${order.orderNo}？`, '确认订单', {
      confirmButtonText: '确认', cancelButtonText: '取消', type: 'success'
    })
    await adminUpdateOrderStatusApi(order.id, '已确认')
    ElMessage.success('订单已确认')
    loadOrders()
  } catch (e) {}
}

async function completeOrder(order) {
  try {
    await ElMessageBox.confirm(`标记订单 ${order.orderNo} 为已完成？`, '完成订单', {
      confirmButtonText: '确认', cancelButtonText: '取消', type: 'success'
    })
    await adminUpdateOrderStatusApi(order.id, '已完成')
    ElMessage.success('订单已完成')
    loadOrders()
  } catch (e) {}
}

async function rejectOrder(order) {
  try {
    await ElMessageBox.confirm(`确认拒绝订单 ${order.orderNo}？此操作不可撤销。`, '拒绝订单', {
      confirmButtonText: '确认拒绝', cancelButtonText: '取消', type: 'warning'
    })
    await adminUpdateOrderStatusApi(order.id, '已取消')
    ElMessage.success('订单已拒绝')
    loadOrders()
  } catch (e) {}
}

function chatWithUser(order) {
  if (order.userId) {
    router.push({ path: '/admin/chat', query: { user: order.userId } })
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-left {
  display: flex;
  gap: 12px;
}

.order-count {
  font-size: 0.85rem;
  color: #999;
}

.table-card {
  background: #fff;
  border-radius: 14px;
  padding: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.id-cell {
  font-family: monospace;
  font-weight: 700;
  color: #667eea;
  cursor: pointer;
}

.style-text {
  font-size: 0.85rem;
  color: #666;
}

:deep(.el-table th.el-table__cell) {
  background: #fafafa;
  font-weight: 700;
  color: #666;
  font-size: 0.82rem;
}

:deep(.order-row td) {
  font-size: 0.88rem;
}

/* 详情 */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item.full {
  grid-column: 1 / -1;
}

.detail-item .label {
  font-size: 0.78rem;
  color: #999;
}

.detail-item .value {
  font-weight: 600;
  color: #333;
}

.detail-item .value.mono {
  font-family: monospace;
  color: #667eea;
}
</style>
