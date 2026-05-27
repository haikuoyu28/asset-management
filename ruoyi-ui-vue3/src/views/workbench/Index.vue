<template>
  <section class="workbench-page">
    <div class="hero">
      <div class="hero-copy">
        <span class="eyebrow">Operations Workbench</span>
        <h1>企业 IT 运维总览</h1>
        <p>把资产纳管、服务器健康、监控指标和告警处理压到一个入口里，先稳住运维闭环，再向 AIOps 演进。</p>
      </div>
      <div class="hero-status">
        <span class="status-dot"></span>
        <span>平台运行中</span>
        <strong>{{ currentTime }}</strong>
      </div>
    </div>

    <div class="metric-grid">
      <article v-for="metric in metrics" :key="metric.key" class="metric-card">
        <component :is="metric.icon" class="metric-icon" />
        <div>
          <strong>{{ metric.value }}</strong>
          <span>{{ metric.label }}</span>
        </div>
        <em :class="metric.tone">{{ metric.tip }}</em>
      </article>
    </div>

    <div class="content-grid">
      <article class="panel span-2">
        <PanelTitle title="资源趋势" subtitle="最近采集窗口 CPU、内存、磁盘使用率" action="查看监控数据" @action="go('/ops-monitor/data')" />
        <div ref="resourceChartRef" class="chart large"></div>
      </article>

      <article class="panel">
        <PanelTitle title="服务器健康" subtitle="连接状态与监控状态" action="服务器管理" @action="go('/ops-monitor/server')" />
        <div ref="healthChartRef" class="chart donut"></div>
        <div class="health-list">
          <div v-for="item in healthItems" :key="item.type" class="health-item">
            <span :class="['health-dot', item.type]"></span>
            <span>{{ healthLabel(item) }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </article>
    </div>

    <div class="content-grid risk-layout">
      <article class="panel span-2">
        <PanelTitle title="风险服务器" subtitle="按活跃告警、离线状态和监控异常排序" action="查看服务器" @action="go('/ops-monitor/server')" />
        <div v-if="riskServers.length" class="risk-list">
          <div v-for="server in riskServers" :key="server.id" class="risk-row">
            <div>
              <strong>{{ server.serverIp }}</strong>
              <span>{{ server.hostname || '未命名服务器' }}</span>
            </div>
            <div class="usage-pills">
              <span>CPU {{ server.cpuUsage }}</span>
              <span>内存 {{ server.memoryUsage }}</span>
              <span>磁盘 {{ server.diskUsage }}</span>
            </div>
            <div class="state-pills">
              <span :class="['state-pill', server.connectionStatus === '0' ? 'good' : 'bad']">
                {{ server.connectionStatusText }}
              </span>
              <span :class="['state-pill', server.monitorStatus === '0' ? 'good' : 'warn']">
                {{ server.monitorStatusText }}
              </span>
              <strong>{{ server.activeAlarmCount }} 告警</strong>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无风险服务器" :image-size="88" />
      </article>

      <article class="panel">
        <PanelTitle title="告警闭环" subtitle="当前事件处理分布" action="告警事件" danger @action="go('/ops-monitor/alarm')" />
        <div class="alarm-status-grid">
          <div v-for="item in alarmStatusCards" :key="item.key" :class="['alarm-status-card', item.tone]">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </article>
    </div>

    <div class="bottom-grid">
      <article class="panel">
        <PanelTitle title="实时告警" subtitle="未处理事件优先展示" action="处理告警" danger @action="go('/ops-monitor/alarm')" />
        <div v-if="alarmEvents.length" class="event-list">
          <div v-for="alarm in alarmEvents" :key="alarm.id" class="event-row">
            <span :class="['level', alarm.level]">{{ alarm.levelText }}</span>
            <div>
              <strong>{{ alarm.title }}</strong>
              <span>{{ alarm.time }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无待处理告警" :image-size="88" />
      </article>

      <article class="panel">
        <PanelTitle title="快捷入口" subtitle="高频运维动作" />
        <div class="quick-grid">
          <button v-for="item in quickActions" :key="item.path" type="button" class="quick-action" @click="go(item.path)">
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </div>
      </article>

      <article class="panel">
        <PanelTitle title="最近资产变更" subtitle="设备生命周期记录" action="查看资产" @action="go('/asset/info')" />
        <div v-if="assetFlows.length" class="flow-list">
          <div v-for="flow in assetFlows" :key="flow.id" class="flow-row">
            <span></span>
            <div>
              <strong>{{ flow.title }}</strong>
              <small>{{ flow.time || '暂无时间' }}</small>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无资产变更" :image-size="88" />
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Collection, Monitor, Warning, Operation, Odometer } from '@element-plus/icons-vue'
import { getWorkbenchSummary } from '@/api/monitor/workbench'
import PanelTitle from './PanelTitle.vue'

const router = useRouter()
const currentTime = ref('')
const resourceChartRef = ref(null)
const healthChartRef = ref(null)
let resourceChart
let healthChart
let timer

const summary = ref({
  assetTotal: 0,
  serverTotal: 0,
  onlineServers: 0,
  onlineRate: 0,
  alarmTotal: 0,
  flowTotal: 0
})
const resourceSeries = ref(defaultResourceSeries())
const healthItems = ref([
  { label: '在线', value: 0, type: 'good' },
  { label: '离线', value: 0, type: 'warn' },
  { label: '异常', value: 0, type: 'bad' }
])
const alarmEvents = ref([])
const assetFlows = ref([])
const riskServers = ref([])
const alarmStatusSummary = ref({
  unhandled: 0,
  processing: 0,
  handled: 0,
  ignored: 0
})

const metrics = computed(() => [
  { key: 'assets', label: '资产总数', value: summary.value.assetTotal, icon: Collection, tip: '统一纳管', tone: 'stable' },
  { key: 'servers', label: '服务器数', value: summary.value.serverTotal, icon: Monitor, tip: `${summary.value.onlineRate}% 在线`, tone: 'good' },
  { key: 'alarms', label: '待处理告警', value: summary.value.alarmTotal, icon: Warning, tip: summary.value.alarmTotal > 0 ? '需要关注' : '正常', tone: summary.value.alarmTotal > 0 ? 'danger' : 'good' },
  { key: 'flows', label: '资产变更', value: summary.value.flowTotal, icon: Operation, tip: '生命周期', tone: 'stable' }
])

const alarmStatusCards = computed(() => [
  { key: 'unhandled', label: '未处理', value: alarmStatusSummary.value.unhandled, tone: 'danger' },
  { key: 'processing', label: '处理中', value: alarmStatusSummary.value.processing, tone: 'warning' },
  { key: 'handled', label: '已处理', value: alarmStatusSummary.value.handled, tone: 'good' },
  { key: 'ignored', label: '已忽略', value: alarmStatusSummary.value.ignored, tone: 'stable' }
])

const quickActions = [
  { label: '设备资产', path: '/asset/info', icon: Collection },
  { label: '服务器管理', path: '/ops-monitor/server', icon: Monitor },
  { label: '告警事件', path: '/ops-monitor/alarm', icon: Warning },
  { label: '监控数据', path: '/ops-monitor/data', icon: Odometer }
]

onMounted(() => {
  tickTime()
  timer = window.setInterval(tickTime, 30000)
  nextTick(() => {
    resourceChart = echarts.init(resourceChartRef.value)
    healthChart = echarts.init(healthChartRef.value)
    refreshCharts()
    window.addEventListener('resize', resizeCharts)
  })
  loadWorkbench()
})

onBeforeUnmount(() => {
  window.clearInterval(timer)
  window.removeEventListener('resize', resizeCharts)
  resourceChart?.dispose()
  healthChart?.dispose()
})

function tickTime() {
  const now = new Date()
  const pad = value => String(value).padStart(2, '0')
  currentTime.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
}

async function loadWorkbench() {
  try {
    const response = await getWorkbenchSummary()
    const data = response.data || {}
    const metricsData = data.metrics || {}
    summary.value = {
      assetTotal: toNumber(metricsData.assetTotal),
      serverTotal: toNumber(metricsData.serverTotal),
      onlineServers: toNumber(metricsData.onlineServers),
      onlineRate: toNumber(metricsData.onlineRate),
      alarmTotal: toNumber(metricsData.alarmTotal),
      flowTotal: toNumber(metricsData.flowTotal)
    }
    healthItems.value = normalizeHealthItems(data.healthItems)
    resourceSeries.value = normalizeResourceSeries(data.resourceSeries)
    alarmEvents.value = (data.alarmEvents || []).map(normalizeAlarm)
    assetFlows.value = (data.assetFlows || []).map(normalizeFlow)
    riskServers.value = (data.riskServers || []).map(normalizeRiskServer)
    alarmStatusSummary.value = normalizeAlarmStatusSummary(data.alarmStatusSummary)
  } finally {
    refreshCharts()
  }
}

function normalizeHealthItems(items) {
  if (!items?.length) {
    return healthItems.value
  }
  return items.map((item, index) => {
    const type = item.type || ['good', 'warn', 'bad'][index] || 'good'
    return {
      label: readableHealthLabel(item.label, type),
      value: toNumber(item.value),
      type
    }
  })
}

function normalizeResourceSeries(series) {
  if (!series?.time?.length) {
    return defaultResourceSeries()
  }
  return {
    time: series.time,
    cpu: (series.cpu || []).map(toNumber),
    memory: (series.memory || []).map(toNumber),
    disk: (series.disk || []).map(toNumber)
  }
}

function normalizeAlarm(item, index) {
  const level = mapAlarmLevel(item.alarmLevel)
  return {
    id: item.id || index,
    level,
    levelText: mapAlarmLevelText(level),
    title: item.alarmMessage || `${item.hostname || item.serverIp || '服务器'} 指标异常`,
    time: item.alarmTime || '刚刚'
  }
}

function normalizeFlow(item, index) {
  return {
    id: item.id || index,
    title: `${mapFlowType(item.flowType)} ${item.assetName || item.assetCode || '资产'}`,
    time: item.operateTime || ''
  }
}

function normalizeRiskServer(item, index) {
  const connectionStatus = String(item.connectionStatus ?? '1')
  const monitorStatus = String(item.monitorStatus ?? '2')
  return {
    id: item.id || index,
    serverIp: item.serverIp || '-',
    hostname: item.hostname || '',
    connectionStatus,
    monitorStatus,
    connectionStatusText: connectionStatus === '0' ? '在线' : '离线',
    monitorStatusText: monitorStatus === '0' ? '正常' : monitorStatus === '1' ? '异常' : '未知',
    activeAlarmCount: toNumber(item.activeAlarmCount),
    cpuUsage: formatMetric(item.cpuUsage),
    memoryUsage: formatMetric(item.memoryUsage),
    diskUsage: formatMetric(item.diskUsage)
  }
}

function normalizeAlarmStatusSummary(data = {}) {
  return {
    unhandled: toNumber(data.unhandled),
    processing: toNumber(data.processing),
    handled: toNumber(data.handled),
    ignored: toNumber(data.ignored)
  }
}

function refreshCharts() {
  nextTick(() => {
    refreshResourceChart()
    refreshHealthChart()
  })
}

function refreshResourceChart() {
  if (!resourceChart) {
    return
  }
  resourceChart.setOption({
    color: ['#1aa37a', '#376df6', '#f59e0b'],
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, textStyle: { color: '#667085' }, data: ['CPU', '内存', '磁盘'] },
    grid: { left: 38, right: 20, top: 28, bottom: 48 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: resourceSeries.value.time,
      axisLabel: { color: '#667085' },
      axisLine: { lineStyle: { color: '#d0d5dd' } }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: { formatter: '{value}%', color: '#667085' },
      splitLine: { lineStyle: { color: '#eaecf0' } }
    },
    series: [
      lineSeries('CPU', resourceSeries.value.cpu),
      lineSeries('内存', resourceSeries.value.memory),
      lineSeries('磁盘', resourceSeries.value.disk)
    ]
  })
}

function refreshHealthChart() {
  if (!healthChart) {
    return
  }
  healthChart.setOption({
    color: ['#12b76a', '#f79009', '#f04438'],
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['58%', '78%'],
        center: ['50%', '50%'],
        label: { show: false },
        data: healthItems.value.map(item => ({ name: healthLabel(item), value: item.value }))
      }
    ]
  })
}

function lineSeries(name, data) {
  return {
    name,
    type: 'line',
    smooth: true,
    symbolSize: 6,
    data,
    areaStyle: { opacity: 0.12 },
    lineStyle: { width: 3 }
  }
}

function resizeCharts() {
  resourceChart?.resize()
  healthChart?.resize()
}

function go(path) {
  router.push(path)
}

function defaultResourceSeries() {
  return {
    time: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
    cpu: [0, 0, 0, 0, 0, 0],
    memory: [0, 0, 0, 0, 0, 0],
    disk: [0, 0, 0, 0, 0, 0]
  }
}

function readableHealthLabel(label, type) {
  if (label && !/[�]/.test(label)) {
    return label
  }
  return type === 'good' ? '在线' : type === 'warn' ? '离线' : '异常'
}

function healthLabel(item) {
  return readableHealthLabel(item.label, item.type)
}

function toNumber(value) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatMetric(value) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  return `${toNumber(value)}%`
}

function mapAlarmLevel(level) {
  const value = String(level || '').toLowerCase()
  if (value === '3' || value.includes('critical') || value.includes('严重')) {
    return 'critical'
  }
  if (value === '2' || value.includes('warn') || value.includes('警告')) {
    return 'warning'
  }
  return 'info'
}

function mapAlarmLevelText(level) {
  return level === 'critical' ? '严重' : level === 'warning' ? '警告' : '提示'
}

function mapFlowType(type) {
  const map = {
    1: '领用',
    2: '归还',
    3: '报修',
    4: '维修',
    5: '巡检',
    6: '报废'
  }
  return map[String(type)] || '变更'
}
</script>
