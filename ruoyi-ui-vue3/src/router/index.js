import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'
import WorkbenchIndex from '@/views/workbench/Index.vue'
import Login from '@/views/login/Login.vue'
import AssetInfo from '@/views/asset/AssetInfo.vue'
import AssetFlow from '@/views/asset/AssetFlow.vue'
import Server from '@/views/monitor/Server.vue'
import MonitorData from '@/views/monitor/MonitorData.vue'
import Alarm from '@/views/monitor/Alarm.vue'
import Rule from '@/views/monitor/Rule.vue'
import Profile from '@/views/user/Profile.vue'
import { hasToken } from '@/utils/auth'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'

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
        meta: { title: '首页' }
      },
      {
        path: 'user/profile',
        name: 'UserProfile',
        component: Profile,
        meta: { title: '个人中心' }
      },
      {
        path: 'asset/info',
        name: 'AssetInfoMigration',
        component: AssetInfo,
        meta: { title: '设备资产' }
      },
      {
        path: 'asset/flow',
        name: 'AssetFlowMigration',
        component: AssetFlow,
        meta: { title: '资产变更' }
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
      },
      {
        path: 'ops-monitor/rule',
        name: 'MonitorRuleMigration',
        component: Rule,
        meta: { title: '告警规则' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(async to => {
  if (to.path !== '/login' && !hasToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && hasToken()) {
    return { path: '/index' }
  }
  if (to.path !== '/login') {
    const user = useUserStore()
    const permission = usePermissionStore()
    if (!user.isLoaded) {
      await user.loadUserInfo().catch(() => {})
    }
    await permission.loadMenus()
  }
  return true
})

router.afterEach(to => {
  document.title = `${to.meta.title || '运维平台'} - ${import.meta.env.VITE_APP_TITLE || '企业 IT 资产与运维监控平台'}`
})

export default router
