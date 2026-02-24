<template>
  <el-dialog
    v-model="userStore.showLogin"
    width="420px"
    :show-close="true"
    :close-on-click-modal="true"
    class="login-dialog"
    align-center
  >
    <div class="login-wrapper">
      <div class="login-header">
        <div class="login-logo">唯<span>爱</span></div>
        <h3>欢迎来到唯爱婚纱摄影</h3>
        <p>{{ isRegister ? '注册账号，开启美好旅程' : '登录后即可预约拍摄和发表评价' }}</p>
      </div>

      <!-- 登录表单 -->
      <el-form v-if="!isRegister" ref="loginRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-else ref="registerRef" :model="registerForm" :rules="registerRules" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            size="large"
            maxlength="11"
            :prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="loading"
          @click="handleRegister"
        >
          注 册
        </el-button>
      </el-form>

      <div class="switch-mode">
        <span v-if="!isRegister">
          还没有账号？<a @click="switchToRegister">立即注册</a>
        </span>
        <span v-else>
          已有账号？<a @click="switchToLogin">返回登录</a>
        </span>
      </div>

      <div class="login-footer">
        <p>登录即代表同意 <a href="#">《用户协议》</a> 和 <a href="#">《隐私政策》</a></p>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userStore } from '../stores/user'

const loginRef = ref(null)
const registerRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单
const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function switchToRegister() {
  isRegister.value = true
}

function switchToLogin() {
  isRegister.value = false
}

async function handleLogin() {
  const valid = await loginRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm.username, loginForm.password)
    userStore.onLoginSuccess()
    ElMessage.success('登录成功，欢迎您！')
    loginForm.username = ''
    loginForm.password = ''
  } catch {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const valid = await registerRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.register({
      username: registerForm.username,
      phone: registerForm.phone,
      password: registerForm.password
    })
    ElMessage.success('注册成功，请登录！')

    // 注册成功后切换到登录，并自动填入用户名
    loginForm.username = registerForm.username
    loginForm.password = ''
    registerForm.username = ''
    registerForm.phone = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    isRegister.value = false
  } catch {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  padding: 10px 10px 0;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo {
  font-size: 2.2rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 10px;
}

.login-logo span {
  color: var(--secondary-color);
}

.login-header h3 {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 6px;
}

.login-header p {
  color: #999;
  font-size: 0.9rem;
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

.switch-mode {
  text-align: center;
  margin-top: 18px;
  font-size: 0.9rem;
  color: #999;
}

.switch-mode a {
  color: var(--secondary-color);
  cursor: pointer;
  font-weight: 600;
}

.switch-mode a:hover {
  text-decoration: underline;
}

.login-footer {
  text-align: center;
  margin-top: 18px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.login-footer p {
  font-size: 0.8rem;
  color: #bbb;
}

.login-footer a {
  color: var(--secondary-color);
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
</style>
