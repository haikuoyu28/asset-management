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
          <strong>运维 AI 助手平台</strong>
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
          <button class="icon-button" type="button" title="通知">
            <Bell />
          </button>
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
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Bell,
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

const app = useAppStore()
const permission = usePermissionStore()
const user = useUserStore()
const route = useRoute()
const router = useRouter()
const openGroups = ref(['/asset', '/ops-monitor'])
const menus = computed(() => permission.menus)
const avatarText = computed(() => (user.name || 'A').slice(0, 1).toUpperCase())

user.loadUserInfo().catch(() => {})
permission.loadMenus()

function toggleGroup(path) {
  if (app.collapsed) {
    return
  }
  openGroups.value = openGroups.value.includes(path)
    ? openGroups.value.filter(item => item !== path)
    : openGroups.value.concat(path)
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
