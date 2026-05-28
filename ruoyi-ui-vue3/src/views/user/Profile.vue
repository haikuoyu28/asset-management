<template>
  <section class="profile-page">
    <div class="module-hero profile">
      <div>
        <p class="eyebrow">User Center</p>
        <h1>个人中心</h1>
        <p>管理当前登录账号的基础资料和登录密码。这里复用后端用户接口，保持和平台权限体系一致。</p>
      </div>
    </div>

    <div class="profile-grid">
      <aside class="profile-card">
        <div class="profile-avatar">{{ avatarText }}</div>
        <h2>{{ profile.nickName || profile.userName || '运维用户' }}</h2>
        <p>{{ profile.userName || '-' }}</p>
        <dl>
          <div>
            <dt>手机号码</dt>
            <dd>{{ profile.phonenumber || '-' }}</dd>
          </div>
          <div>
            <dt>邮箱</dt>
            <dd>{{ profile.email || '-' }}</dd>
          </div>
          <div>
            <dt>所属部门</dt>
            <dd>{{ profile.dept?.deptName || '-' }}</dd>
          </div>
          <div>
            <dt>所属角色</dt>
            <dd>{{ roleGroup || '-' }}</dd>
          </div>
          <div>
            <dt>创建时间</dt>
            <dd>{{ profile.createTime || '-' }}</dd>
          </div>
        </dl>
      </aside>

      <article class="profile-panel">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基础资料" name="info">
            <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="88px" class="profile-form">
              <el-form-item label="用户昵称" prop="nickName">
                <el-input v-model="infoForm.nickName" maxlength="30" placeholder="请输入用户昵称" />
              </el-form-item>
              <el-form-item label="手机号码" prop="phonenumber">
                <el-input v-model="infoForm.phonenumber" maxlength="11" placeholder="请输入手机号码" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="infoForm.email" maxlength="50" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="infoForm.sex">
                  <el-radio label="0">男</el-radio>
                  <el-radio label="1">女</el-radio>
                  <el-radio label="2">未知</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingInfo" @click="submitInfo">保存资料</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="88px" class="profile-form">
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingPwd" @click="submitPassword">更新密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, updateUserPwd } from '@/api/system/user'
import { useUserStore } from '@/stores/user'

const user = useUserStore()
const activeTab = ref('info')
const infoFormRef = ref()
const pwdFormRef = ref()
const savingInfo = ref(false)
const savingPwd = ref(false)
const profile = ref({})
const roleGroup = ref('')

const infoForm = reactive({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: '2'
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const avatarText = computed(() => (profile.value.nickName || profile.value.userName || user.name || 'A').slice(0, 1).toUpperCase())

const infoRules = {
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { required: true, message: '手机号码不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在 6 到 20 位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

onMounted(loadProfile)

async function loadProfile() {
  const response = await getUserProfile()
  profile.value = response.data || {}
  roleGroup.value = response.roleGroup || ''
  Object.assign(infoForm, {
    nickName: profile.value.nickName || '',
    phonenumber: profile.value.phonenumber || '',
    email: profile.value.email || '',
    sex: profile.value.sex || '2'
  })
}

async function submitInfo() {
  await infoFormRef.value.validate()
  savingInfo.value = true
  try {
    await updateUserProfile({ ...infoForm })
    ElMessage.success('个人资料已更新')
    await loadProfile()
    await user.loadUserInfo().catch(() => {})
  } finally {
    savingInfo.value = false
  }
}

async function submitPassword() {
  await pwdFormRef.value.validate()
  savingPwd.value = true
  try {
    await updateUserPwd(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('密码已更新')
    Object.assign(pwdForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    pwdFormRef.value.clearValidate()
  } finally {
    savingPwd.value = false
  }
}
</script>
