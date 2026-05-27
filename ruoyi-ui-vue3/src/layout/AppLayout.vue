<template>
  <div class="app-shell" :class="{ collapsed: app.collapsed }">
    <aside class="app-sidebar">
      <div class="brand">
        <div class="brand-mark">Ops</div>
        <div class="brand-copy">
          <strong>资产运维平台</strong>
          <span>AIOps Foundation</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
        <RouterLink v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item">
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>

    <main class="app-main">
      <header class="topbar">
        <button class="icon-button" type="button" title="折叠菜单" @click="app.toggleSidebar">
          <Fold v-if="!app.collapsed" />
          <Expand v-else />
        </button>
        <div>
          <strong>{{ route.meta.title || '运维平台' }}</strong>
          <span>Vue3 迁移预览版</span>
        </div>
        <a class="legacy-link" :href="legacyHome">返回现有系统</a>
      </header>

      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataBoard, Monitor, Odometer, Warning, Collection, Fold, Expand } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'

const app = useAppStore()
const route = useRoute()
const legacyBase = import.meta.env.VITE_APP_LEGACY_BASE || '/'
const legacyHome = computed(() => `${legacyBase.replace(/\/$/, '')}/index`)

const navItems = [
  { label: '运维总览', path: '/index', icon: DataBoard },
  { label: '设备资产', path: '/asset/info', icon: Collection },
  { label: '服务器管理', path: '/ops-monitor/server', icon: Monitor },
  { label: '监控数据', path: '/ops-monitor/data', icon: Odometer },
  { label: '告警事件', path: '/ops-monitor/alarm', icon: Warning }
]
</script>
