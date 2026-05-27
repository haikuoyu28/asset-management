import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'
import WorkbenchIndex from '@/views/workbench/Index.vue'
import MigrationPlaceholder from '@/views/workbench/MigrationPlaceholder.vue'

const routes = [
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
        component: MigrationPlaceholder,
        meta: { title: '设备资产', legacyPath: '/asset/info' }
      },
      {
        path: 'ops-monitor/server',
        name: 'MonitorServerMigration',
        component: MigrationPlaceholder,
        meta: { title: '服务器管理', legacyPath: '/ops-monitor/server' }
      },
      {
        path: 'ops-monitor/data',
        name: 'MonitorDataMigration',
        component: MigrationPlaceholder,
        meta: { title: '监控数据', legacyPath: '/ops-monitor/data' }
      },
      {
        path: 'ops-monitor/alarm',
        name: 'MonitorAlarmMigration',
        component: MigrationPlaceholder,
        meta: { title: '告警事件', legacyPath: '/ops-monitor/alarm' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.afterEach(to => {
  document.title = `${to.meta.title || '运维平台'} - ${import.meta.env.VITE_APP_TITLE || '企业IT资产与运维监控平台'}`
})

export default router
