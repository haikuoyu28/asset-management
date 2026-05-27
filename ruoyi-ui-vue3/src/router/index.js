import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'
import WorkbenchIndex from '@/views/workbench/Index.vue'
import Login from '@/views/login/Login.vue'
import AssetInfo from '@/views/asset/AssetInfo.vue'
import Server from '@/views/monitor/Server.vue'
import MonitorData from '@/views/monitor/MonitorData.vue'
import Alarm from '@/views/monitor/Alarm.vue'
import { hasToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: AppLayout,
    redirect: '/index',
    children: [
      {
        path: 'index',
        name: 'Workbench',
        component: WorkbenchIndex,
        meta: { title: '运维总览' }
      },
      {
        path: 'asset/info',
        name: 'AssetInfoMigration',
        component: AssetInfo,
        meta: { title: '设备资产' }
      },
      {
        path: 'ops-monitor/server',
        name: 'MonitorServerMigration',
        component: Server,
        meta: { title: '服务器管理' }
      },
      {
        path: 'ops-monitor/data',
        name: 'MonitorDataMigration',
        component: MonitorData,
        meta: { title: '监控数据' }
      },
      {
        path: 'ops-monitor/alarm',
        name: 'MonitorAlarmMigration',
        component: Alarm,
        meta: { title: '告警事件' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(to => {
  if (to.path !== '/login' && !hasToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && hasToken()) {
    return { path: '/index' }
  }
  return true
})

router.afterEach(to => {
  document.title = `${to.meta.title || '运维平台'} - ${import.meta.env.VITE_APP_TITLE || '企业IT资产与运维监控平台'}`
})

export default router
