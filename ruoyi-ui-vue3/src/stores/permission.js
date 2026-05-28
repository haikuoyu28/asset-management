import { defineStore } from 'pinia'
import {
  Collection,
  DataBoard,
  Monitor,
  Odometer,
  Operation,
  SetUp,
  Tickets,
  User,
  Warning
} from '@element-plus/icons-vue'
import { getRouters } from '@/api/menu'

const routeTitleMap = {
  '/index': '运维总览',
  '/asset/info': '设备资产',
  '/asset/flow': '资产变更',
  '/ops-monitor/server': '服务器管理',
  '/ops-monitor/data': '监控数据',
  '/ops-monitor/alarm': '告警事件',
  '/ops-monitor/rule': '告警规则'
}

const routeIconMap = {
  '/index': DataBoard,
  '/asset/info': Collection,
  '/asset/flow': Tickets,
  '/ops-monitor/server': Monitor,
  '/ops-monitor/data': Odometer,
  '/ops-monitor/alarm': Warning,
  '/ops-monitor/rule': SetUp
}

const fallbackMenus = [
  { title: '工作台', path: '/index', icon: DataBoard },
  {
    title: '资产中心',
    path: '/asset',
    icon: Collection,
    children: [
      { title: '设备资产', path: '/asset/info', icon: Collection },
      { title: '资产变更', path: '/asset/flow', icon: Tickets }
    ]
  },
  {
    title: '监控中心',
    path: '/ops-monitor',
    icon: Monitor,
    children: [
      { title: '服务器管理', path: '/ops-monitor/server', icon: Monitor },
      { title: '监控数据', path: '/ops-monitor/data', icon: Odometer },
      { title: '告警事件', path: '/ops-monitor/alarm', icon: Warning },
      { title: '告警规则', path: '/ops-monitor/rule', icon: SetUp }
    ]
  },
  {
    title: '平台管理',
    path: '/platform',
    icon: User,
    children: [
      { title: '用户管理', path: '/system/user', icon: User, disabled: true },
      { title: '角色权限', path: '/system/role', icon: Operation, disabled: true },
      { title: '菜单配置', path: '/system/menu', icon: SetUp, disabled: true }
    ]
  }
]

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    loaded: false,
    menus: fallbackMenus
  }),
  actions: {
    async loadMenus() {
      if (this.loaded) {
        return this.menus
      }
      try {
        const response = await getRouters()
        const normalized = normalizeMenus(response.data || [])
        this.menus = normalized.length ? normalized : fallbackMenus
      } catch (error) {
        this.menus = fallbackMenus
      } finally {
        this.loaded = true
      }
      return this.menus
    }
  }
})

function normalizeMenus(routes, parentPath = '') {
  return routes
    .filter(route => !route.hidden)
    .map(route => {
      const fullPath = normalizePath(route.path, parentPath)
      const children = normalizeMenus(route.children || [], fullPath)
      const knownPath = routeTitleMap[fullPath] ? fullPath : ''

      if (!knownPath && !children.length) {
        return null
      }

      return {
        title: route.meta?.title || routeTitleMap[fullPath] || route.name || '菜单',
        path: knownPath || fullPath,
        icon: routeIconMap[knownPath || fullPath] || routeIconMap[children[0]?.path] || DataBoard,
        children
      }
    })
    .filter(Boolean)
}

function normalizePath(path = '', parentPath = '') {
  if (!path || path === '/') {
    return parentPath || '/'
  }
  if (path.startsWith('http')) {
    return path
  }
  if (path.startsWith('/')) {
    return path
  }
  const base = parentPath && parentPath !== '/' ? parentPath : ''
  return `${base}/${path}`.replace(/\/+/g, '/')
}
