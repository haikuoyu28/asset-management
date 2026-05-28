<template>
  <div class="app-shell" :class="{ collapsed: app.collapsed }">
    <aside class="app-sidebar">
      <div class="brand">
        <div class="brand-mark">
          <Monitor />
        </div>
        <div class="brand-copy">
          <strong>Asset Ops</strong>
          <span>AIOps Console</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
        <RouterLink to="/index" class="nav-item">
          <DataBoard />
          <span>首页</span>
        </RouterLink>

        <template v-for="item in menus" :key="item.path">
          <button v-if="item.children?.length" type="button" class="nav-item nav-parent" @click="toggleGroup(item.path)">
            <component :is="item.icon" />
            <span>{{ item.title }}</span>
            <ArrowDown class="nav-arrow" :class="{ open: openGroups.includes(item.path) }" />
          </button>
          <RouterLink v-else :to="item.path" class="nav-item">
            <component :is="item.icon" />
            <span>{{ item.title }}</span>
          </RouterLink>

          <div v-if="item.children?.length && openGroups.includes(item.path)" class="nav-children">
            <RouterLink
              v-for="child in item.children"
              :key="child.path"
              :to="child.disabled ? route.fullPath : child.path"
              class="nav-item child"
              :class="{ disabled: child.disabled }"
            >
              <component :is="child.icon" />
              <span>{{ child.title }}</span>
            </RouterLink>
          </div>
        </template>
      </nav>
    </aside>

    <main class="app-main">
      <header class="topbar">
        <button class="icon-button" type="button" title="折叠菜单" @click="app.toggleSidebar">
          <Fold v-if="!app.collapsed" />
          <Expand v-else />
        </button>
        <div class="topbar-title">
          <strong>IT设备管理平台</strong>
          <span>{{ route.meta.title || '运维平台' }}</span>
        </div>
        <div class="topbar-tools">
          <button class="icon-button" type="button" title="明亮模式" :class="{ active: app.theme === 'light' }" @click="app.setTheme('light')">
            <Sunny />
          </button>
          <button
            class="switch-button"
            type="button"
            :class="{ dark: app.theme === 'dark' }"
            :aria-label="app.theme === 'dark' ? '切换到明亮模式' : '切换到暗色模式'"
            @click="app.toggleTheme"
          ></button>
          <button class="icon-button" type="button" title="暗色模式" :class="{ active: app.theme === 'dark' }" @click="app.setTheme('dark')">
            <Moon />
          </button>
          <el-popover placement="bottom-end" trigger="click" width="320" popper-class="notification-popover">
            <template #reference>
              <button class="icon-button notice-button" type="button" title="通知">
                <Bell />
                <span v-if="notifications.length" class="notice-dot"></span>
              </button>
            </template>
            <div class="notification-panel">
              <div class="notification-head">
                <strong>通知</strong>
                <span>{{ notifications.length ? `${notifications.length} 条未读` : '无未读' }}</span>
              </div>
              <div v-if="notificationLoading" class="notification-empty">正在加载通知...</div>
              <div v-else-if="!notifications.length" class="notification-empty">暂无新通知</div>
              <div v-else class="notification-list">
                <article
                  v-for="item in notifications"
                  :key="item.id"
                  class="notification-row"
                >
                  <span :class="['notification-level', item.level]"></span>
                  <span>
                    <strong>{{ item.title }}</strong>
                    <small>{{ item.desc }}</small>
                  </span>
                </article>
              </div>
              <button class="notification-refresh" type="button" @click="loadNotifications">刷新通知</button>
            </div>
          </el-popover>
        </div>

        <el-dropdown trigger="click" @command="handleUserCommand">
          <button class="user-chip" type="button">
            <span>{{ avatarText }}</span>
            <strong>{{ user.name || 'admin' }}</strong>
            <ArrowDown />
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <UserFilled />
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <SwitchButton />
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </header>

      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Bell,
  DataBoard,
  Expand,
  Fold,
  Monitor,
  Moon,
  Sunny,
  SwitchButton,
  UserFilled
} from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'
import { listAlarm } from '@/api/monitor/alarm'

const app = useAppStore()
const permission = usePermissionStore()
const user = useUserStore()
const route = useRoute()
const router = useRouter()
const openGroups = ref(['/asset', '/ops-monitor'])
const menus = computed(() => permission.menus.filter(item => item.path !== '/index'))
const avatarText = computed(() => (user.name || 'A').slice(0, 1).toUpperCase())
const notifications = ref([])
const notificationLoading = ref(false)

user.loadUserInfo().catch(() => {})
permission.loadMenus()
onMounted(loadNotifications)

function toggleGroup(path) {
  if (app.collapsed) {
    return
  }
  openGroups.value = openGroups.value.includes(path)
    ? openGroups.value.filter(item => item !== path)
    : openGroups.value.concat(path)
}

async function loadNotifications() {
  notificationLoading.value = true
  try {
    const response = await listAlarm({ pageNum: 1, pageSize: 5, alarmStatus: '0' })
    notifications.value = (response.rows || []).map(normalizeAlarmNotification)
  } catch (error) {
    notifications.value = []
  } finally {
    notificationLoading.value = false
  }
}

function normalizeAlarmNotification(item, index) {
  const level = String(item.alarmLevel || '')
  return {
    id: item.id || index,
    level: level === '3' ? 'danger' : level === '2' ? 'warning' : 'info',
    title: item.alarmMessage || `${item.serverIp || item.hostname || '服务器'} 产生告警`,
    desc: `${item.serverIp || item.hostname || '未知对象'} · ${item.alarmTime || '刚刚'}`
  }
}

async function handleUserCommand(command) {
  if (command === 'profile') {
    router.push('/user/profile')
    return
  }
  if (command === 'logout') {
    await user.logout()
    window.location.href = '/login'
  }
}
</script>
