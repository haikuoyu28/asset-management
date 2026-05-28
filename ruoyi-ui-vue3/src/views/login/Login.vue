<template>
  <main class="login-page">
    <section class="login-visual">
      <div class="login-brand">
        <span>IT</span>
        <strong>IT设备管理平台</strong>
      </div>
    </section>

    <section class="login-card">
      <h2>登录平台</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" autocomplete="username" placeholder="请输入账号" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" autocomplete="current-password" placeholder="请输入密码" show-password size="large" type="password" />
        </el-form-item>
        <el-form-item v-if="captchaOnOff" label="验证码" prop="code">
          <div class="captcha-row">
            <el-input v-model="form.code" placeholder="验证码" size="large" />
            <button class="captcha-image" type="button" @click="loadCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <span v-else>刷新</span>
            </button>
          </div>
        </el-form-item>
        <el-button :loading="loading" class="login-button" size="large" type="primary" @click="handleLogin">
          登录
        </el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCodeImg, login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const user = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaImage = ref('')
const captchaOnOff = ref(true)

const form = reactive({
  username: '',
  password: '',
  code: '',
  uuid: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

onMounted(loadCaptcha)

async function loadCaptcha() {
  const response = await getCodeImg()
  captchaOnOff.value = response.captchaOnOff !== false
  form.uuid = response.uuid || ''
  captchaImage.value = response.img ? `data:image/gif;base64,${response.img}` : ''
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const response = await login({ ...form })
    user.setLoginToken(response.token)
    await user.loadUserInfo().catch(() => {})
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/index')
  } catch (error) {
    if (captchaOnOff.value) {
      loadCaptcha()
    }
  } finally {
    loading.value = false
  }
}
</script>
