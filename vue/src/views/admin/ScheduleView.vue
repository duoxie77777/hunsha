<template>
  <div class="schedule-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="top-left">
        <el-button-group>
          <el-button :type="viewMode === 'month' ? 'primary' : ''" @click="viewMode = 'month'">月视图</el-button>
          <el-button :type="viewMode === 'week' ? 'primary' : ''" @click="viewMode = 'week'">周视图</el-button>
        </el-button-group>
        <div class="month-nav">
          <el-button text @click="prevMonth"><el-icon><ArrowLeft /></el-icon></el-button>
          <span class="month-title">{{ currentYear }}年{{ currentMonth + 1 }}月</span>
          <el-button text @click="nextMonth"><el-icon><ArrowRight /></el-icon></el-button>
          <el-button size="small" @click="goToday" style="margin-left: 8px;">今天</el-button>
        </div>
      </div>
      <el-button type="primary" @click="openAddEvent">
        <el-icon><Plus /></el-icon> 新增排期
      </el-button>
    </div>

    <!-- 月视图日历 -->
    <div v-if="viewMode === 'month'" class="calendar-grid">
      <div class="weekday-header">
        <div class="weekday-cell" v-for="d in weekdays" :key="d">{{ d }}</div>
      </div>
      <div class="day-grid">
        <div
          v-for="(day, idx) in calendarDays"
          :key="idx"
          class="day-cell"
          :class="{
            'other-month': !day.currentMonth,
            today: day.isToday,
            'has-events': day.events.length > 0
          }"
          @click="selectDay(day)"
        >
          <span class="day-num">{{ day.date }}</span>
          <div class="day-events">
            <div
              v-for="(ev, i) in day.events.slice(0, 3)"
              :key="i"
              class="event-dot"
              :class="getStatusClass(ev.status)"
              :title="ev.customerName + ' - ' + ev.pkgName"
            >
              <span class="event-text">{{ ev.customerName }}</span>
            </div>
            <span v-if="day.events.length > 3" class="more-events">+{{ day.events.length - 3 }}更多</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 周视图 -->
    <div v-else class="week-view">
      <div class="week-header">
        <div class="week-day-col" v-for="d in weekViewDays" :key="d.key" :class="{ today: d.isToday }">
          <span class="week-day-name">{{ d.weekday }}</span>
          <span class="week-day-num">{{ d.date }}</span>
        </div>
      </div>
      <div class="week-body">
        <div class="week-day-col" v-for="d in weekViewDays" :key="d.key" :class="{ today: d.isToday }">
          <div v-if="d.events.length === 0" class="no-event">无排期</div>
          <div
            v-for="ev in d.events"
            :key="ev.id"
            class="week-event-card"
            :class="getStatusClass(ev.status)"
            @click="editEvent(ev)"
          >
            <div class="ev-time">{{ ev.time || '全天' }}</div>
            <div class="ev-name">{{ ev.customerName }}</div>
            <div class="ev-pkg">{{ ev.pkgName }}</div>
            <el-tag :type="statusTagType(ev.status)" size="small">{{ statusLabel(ev.status) }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 日期详情抽屉 -->
    <el-drawer v-model="dayDrawerVisible" :title="selectedDay ? `${selectedDay.fullDate} 排期详情` : '排期详情'" size="420px" direction="rtl">
      <div v-if="selectedDay">
        <div v-if="selectedDay.events.length === 0" style="text-align: center; color: #ccc; padding: 40px;">
          当日无排期
        </div>
        <div v-else class="day-event-list">
          <div v-for="ev in selectedDay.events" :key="ev.id" class="day-event-item">
            <div class="ev-header">
              <span class="ev-customer">{{ ev.customerName }}</span>
              <el-tag :type="statusTagType(ev.status)" size="small">{{ statusLabel(ev.status) }}</el-tag>
            </div>
            <div class="ev-detail">
              <span><el-icon><Tickets /></el-icon> {{ ev.pkgName }}</span>
              <span><el-icon><Clock /></el-icon> {{ ev.time || '全天' }}</span>
              <span v-if="ev.location"><el-icon><Location /></el-icon> {{ ev.location }}</span>
              <span v-if="ev.remark"><el-icon><Document /></el-icon> {{ ev.remark }}</span>
            </div>
            <div class="ev-actions">
              <el-button size="small" text type="primary" @click="editEvent(ev)">编辑</el-button>
              <el-button size="small" text type="success" v-if="ev.status === '待确认'" @click="confirmEvent(ev)">确认</el-button>
              <el-button size="small" text type="info" v-if="ev.status === '已确认'" @click="completeEvent(ev)">完成</el-button>
              <el-button size="small" text type="danger" @click="deleteEvent(ev)">删除</el-button>
            </div>
          </div>
        </div>
        <el-button type="primary" style="width: 100%; margin-top: 16px;" @click="openAddEventOnDay(selectedDay.fullDate)">
          <el-icon><Plus /></el-icon> 在当天新增排期
        </el-button>
      </div>
    </el-drawer>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="eventDialogVisible" :title="editingEvent ? '编辑排期' : '新增排期'" width="500px" :close-on-click-modal="false">
      <el-form :model="eventForm" :rules="eventRules" ref="eventFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="eventForm.title" placeholder="例：李小姐 - 经典套餐拍摄" />
        </el-form-item>
        <el-form-item label="拍摄日期" prop="shootDate">
          <el-date-picker v-model="eventForm.shootDate" type="date" placeholder="选择日期" style="width: 100%;" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间段">
          <el-input v-model="eventForm.timeSlot" placeholder="例：09:00-12:00" />
        </el-form-item>
        <el-form-item label="拍摄地点">
          <el-input v-model="eventForm.location" placeholder="例：三亚海棠湾" />
        </el-form-item>
        <el-form-item label="摄影师">
          <el-input v-model="eventForm.photographer" placeholder="摄影师姓名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="eventForm.status">
            <el-radio label="待确认">待确认</el-radio>
            <el-radio label="已确认">已确认</el-radio>
            <el-radio label="进行中">进行中</el-radio>
            <el-radio label="已完成">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="eventForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="eventDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEvent">{{ editingEvent ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, ArrowLeft, ArrowRight, Tickets, Clock, Location, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSchedulesApi, createScheduleApi, updateScheduleApi, deleteScheduleApi } from '../../api/admin'

const events = ref([])
const viewMode = ref('month')
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth())

const dayDrawerVisible = ref(false)
const selectedDay = ref(null)
const eventDialogVisible = ref(false)
const editingEvent = ref(null)
const eventFormRef = ref(null)

const eventForm = reactive({
  title: '',
  shootDate: '',
  timeSlot: '',
  location: '',
  photographer: '',
  status: '待确认',
  remark: '',
  orderId: null
})

const eventRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  shootDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

async function loadSchedules() {
  try {
    const res = await getSchedulesApi({ page: 1, size: 200 })
    if (res.data?.code === 200) {
      events.value = (res.data.data?.records || []).map(s => ({
        ...s,
        date: s.shootDate || '',
        time: s.timeSlot || '',
        customerName: s.title || '',
        pkgName: s.photographer || ''
      }))
    }
  } catch (e) { console.error(e) }
}

onMounted(() => loadSchedules())

const calendarDays = computed(() => {
  const y = currentYear.value
  const m = currentMonth.value
  const firstDay = new Date(y, m, 1).getDay()
  const daysInMonth = new Date(y, m + 1, 0).getDate()
  const prevMonthDays = new Date(y, m, 0).getDate()

  const days = []
  const todayStr = formatDateStr(new Date())

  for (let i = firstDay - 1; i >= 0; i--) {
    const date = prevMonthDays - i
    const pm = m === 0 ? 11 : m - 1
    const py = m === 0 ? y - 1 : y
    const fullDate = `${py}-${String(pm + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}`
    days.push({ date, currentMonth: false, isToday: false, fullDate, events: getEventsForDate(fullDate) })
  }

  for (let d = 1; d <= daysInMonth; d++) {
    const fullDate = `${y}-${String(m + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({ date: d, currentMonth: true, isToday: fullDate === todayStr, fullDate, events: getEventsForDate(fullDate) })
  }

  const remain = 42 - days.length
  for (let d = 1; d <= remain; d++) {
    const nm = m === 11 ? 0 : m + 1
    const ny = m === 11 ? y + 1 : y
    const fullDate = `${ny}-${String(nm + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({ date: d, currentMonth: false, isToday: false, fullDate, events: getEventsForDate(fullDate) })
  }

  return days
})

const weekViewDays = computed(() => {
  const today = new Date()
  const dayOfWeek = today.getDay()
  const weekStart = new Date(today)
  weekStart.setDate(today.getDate() - dayOfWeek)

  const todayStr = formatDateStr(today)
  const days = []

  for (let i = 0; i < 7; i++) {
    const d = new Date(weekStart)
    d.setDate(weekStart.getDate() + i)
    const fullDate = formatDateStr(d)
    days.push({
      key: fullDate,
      weekday: weekdays[i],
      date: `${d.getMonth() + 1}/${d.getDate()}`,
      fullDate,
      isToday: fullDate === todayStr,
      events: getEventsForDate(fullDate)
    })
  }

  return days
})

function formatDateStr(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function getEventsForDate(dateStr) {
  return events.value.filter(e => e.date === dateStr)
}

function prevMonth() {
  if (currentMonth.value === 0) { currentMonth.value = 11; currentYear.value-- }
  else currentMonth.value--
}

function nextMonth() {
  if (currentMonth.value === 11) { currentMonth.value = 0; currentYear.value++ }
  else currentMonth.value++
}

function goToday() {
  const t = new Date()
  currentYear.value = t.getFullYear()
  currentMonth.value = t.getMonth()
}

function selectDay(day) {
  selectedDay.value = day
  dayDrawerVisible.value = true
}

function statusTagType(status) {
  return { '待确认': 'warning', '已确认': '', '已完成': 'success', '进行中': 'primary' }[status] || 'info'
}

function statusLabel(status) {
  return status || '未知'
}

function getStatusClass(status) {
  const map = { '待确认': 'status-pending', '已确认': 'status-confirmed', '已完成': 'status-completed', '进行中': 'status-confirmed' }
  return map[status] || 'status-pending'
}

function openAddEvent() {
  editingEvent.value = null
  Object.assign(eventForm, { title: '', shootDate: '', timeSlot: '', location: '', photographer: '', status: '待确认', remark: '', orderId: null })
  eventDialogVisible.value = true
}

function openAddEventOnDay(dateStr) {
  openAddEvent()
  eventForm.shootDate = dateStr
  dayDrawerVisible.value = false
}

function editEvent(ev) {
  editingEvent.value = ev
  Object.assign(eventForm, {
    title: ev.title || ev.customerName || '',
    shootDate: ev.shootDate || ev.date || '',
    timeSlot: ev.timeSlot || ev.time || '',
    location: ev.location || '',
    photographer: ev.photographer || '',
    status: ev.status || '待确认',
    remark: ev.remark || '',
    orderId: ev.orderId || null
  })
  eventDialogVisible.value = true
  dayDrawerVisible.value = false
}

async function saveEvent() {
  eventFormRef.value?.validate(async (valid) => {
    if (!valid) return
    const payload = {
      title: eventForm.title,
      shootDate: eventForm.shootDate,
      timeSlot: eventForm.timeSlot,
      location: eventForm.location,
      photographer: eventForm.photographer,
      status: eventForm.status,
      remark: eventForm.remark,
      orderId: eventForm.orderId
    }
    try {
      if (editingEvent.value) {
        const res = await updateScheduleApi(editingEvent.value.id, payload)
        if (res.data?.code === 200) {
          ElMessage.success('排期已更新')
          eventDialogVisible.value = false
          loadSchedules()
        } else {
          ElMessage.error(res.data?.msg || '更新失败')
        }
      } else {
        const res = await createScheduleApi(payload)
        if (res.data?.code === 200) {
          ElMessage.success('排期已创建')
          eventDialogVisible.value = false
          loadSchedules()
        } else {
          ElMessage.error(res.data?.msg || '创建失败')
        }
      }
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
}

async function confirmEvent(ev) {
  try {
    const res = await updateScheduleApi(ev.id, { ...ev, status: '已确认' })
    if (res.data?.code === 200) {
      ElMessage.success('已确认')
      loadSchedules()
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function completeEvent(ev) {
  try {
    const res = await updateScheduleApi(ev.id, { ...ev, status: '已完成' })
    if (res.data?.code === 200) {
      ElMessage.success('已标记完成')
      loadSchedules()
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function deleteEvent(ev) {
  try {
    await ElMessageBox.confirm(`确定删除「${ev.customerName || ev.title}」的排期吗？`, '删除确认', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    })
    const res = await deleteScheduleApi(ev.id)
    if (res.data?.code === 200) {
      ElMessage.success('已删除')
      loadSchedules()
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
  margin-bottom: 20px;
  background: #fff;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.top-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.month-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.month-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1a1a2e;
  min-width: 120px;
  text-align: center;
}

/* 月视图 */
.calendar-grid {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.weekday-cell {
  text-align: center;
  padding: 12px;
  font-weight: 700;
  font-size: 0.85rem;
  color: #888;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.day-cell {
  min-height: 100px;
  padding: 8px;
  border-right: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.day-cell:hover { background: #fafafa; }
.day-cell:nth-child(7n) { border-right: none; }
.day-cell.other-month { opacity: 0.35; }

.day-cell.today {
  background: rgba(241, 174, 181, 0.06);
}

.day-cell.today .day-num {
  background: #e88a94;
  color: #fff;
  border-radius: 50%;
  width: 26px;
  height: 26px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.day-num {
  font-size: 0.85rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  display: inline-block;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.event-dot {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.72rem;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.event-dot.status-pending { background: #E6A23C; }
.event-dot.status-confirmed { background: #409EFF; }
.event-dot.status-completed { background: #67C23A; }

.more-events {
  font-size: 0.7rem;
  color: #999;
  padding-left: 4px;
}

/* 周视图 */
.week-view {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.week-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid #eee;
}

.week-header .week-day-col {
  text-align: center;
  padding: 14px 8px;
  background: #f8f9fa;
}

.week-header .week-day-col.today {
  background: rgba(241, 174, 181, 0.1);
}

.week-day-name {
  display: block;
  font-size: 0.8rem;
  color: #999;
}

.week-day-num {
  display: block;
  font-size: 1rem;
  font-weight: 700;
  color: #333;
  margin-top: 2px;
}

.week-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  min-height: 400px;
}

.week-body .week-day-col {
  padding: 10px 6px;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.week-body .week-day-col:last-child { border-right: none; }

.week-body .week-day-col.today {
  background: rgba(241, 174, 181, 0.03);
}

.no-event {
  text-align: center;
  color: #ddd;
  font-size: 0.8rem;
  padding: 20px 0;
}

.week-event-card {
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  border-left: 3px solid;
  background: #f8f9fa;
  transition: transform 0.2s;
}

.week-event-card:hover { transform: scale(1.02); }

.week-event-card.status-pending { border-color: #E6A23C; }
.week-event-card.status-confirmed { border-color: #409EFF; }
.week-event-card.status-completed { border-color: #67C23A; }

.ev-time {
  font-size: 0.75rem;
  color: #999;
  margin-bottom: 2px;
}

.ev-name {
  font-weight: 700;
  font-size: 0.82rem;
  color: #333;
}

.ev-pkg {
  font-size: 0.75rem;
  color: #888;
  margin: 2px 0 4px;
}

/* 日期详情 */
.day-event-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.day-event-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 14px 16px;
}

.ev-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.ev-customer {
  font-weight: 700;
  font-size: 1rem;
  color: #1a1a2e;
}

.ev-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 0.85rem;
  color: #666;
}

.ev-detail span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ev-actions {
  display: flex;
  gap: 4px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

@media (max-width: 992px) {
  .day-cell { min-height: 70px; }
  .event-text { display: none; }
  .event-dot { width: 8px; height: 8px; padding: 0; border-radius: 50%; }
}
</style>
