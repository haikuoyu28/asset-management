<template>
  <div class="ops-workbench">
    <section class="hero-panel">
      <div class="hero-copy">
        <div class="eyebrow">Operations Workbench</div>
        <h1>企业 IT 运维总览</h1>
        <p>资产、服务器、监控指标与告警事件统一汇聚，形成稳定可演进的运维底座。</p>
      </div>
      <div class="hero-status">
        <div class="status-item">
          <span class="status-dot online"></span>
          <span>平台运行正常</span>
        </div>
        <div class="status-time">{{ currentTime }}</div>
      </div>
    </section>

    <section class="metric-grid">
      <article v-for="item in metrics" :key="item.key" class="metric-card">
        <div :class="['metric-icon', item.key]">
          <svg-icon :icon-class="item.icon" />
        </div>
        <div class="metric-body">
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-label">{{ item.label }}</div>
        </div>
        <div :class="['metric-trend', item.trendType]">{{ item.trend }}</div>
      </article>
    </section>

    <section class="main-grid">
      <article class="panel resource-panel">
        <div class="panel-header">
          <div>
            <h2>资源趋势</h2>
            <span>最近采集窗口</span>
          </div>
          <button class="ghost-button" @click="go('/ops-monitor/data')">查看指标</button>
        </div>
        <div ref="resourceChart" class="resource-chart"></div>
      </article>

      <article class="panel health-panel">
        <div class="panel-header">
          <div>
            <h2>服务器健康分布</h2>
            <span>连接状态与监控状态</span>
          </div>
          <button class="ghost-button" @click="go('/ops-monitor/server')">服务器</button>
        </div>
        <div ref="healthChart" class="health-chart"></div>
        <div class="health-list">
          <div v-for="item in healthItems" :key="item.label" class="health-item">
            <span :class="['health-mark', item.type]"></span>
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </article>
    </section>

    <section class="bottom-grid">
      <article class="panel alert-panel">
        <div class="panel-header">
          <div>
            <h2>实时告警</h2>
            <span>待处理事件优先展示</span>
          </div>
          <button class="ghost-button danger" @click="go('/ops-monitor/alarm')">处理告警</button>
        </div>
        <div class="event-list">
          <div v-for="alarm in alarmEvents" :key="alarm.id" class="event-row">
            <div :class="['event-level', alarm.level]">{{ alarm.levelText }}</div>
            <div class="event-main">
              <strong>{{ alarm.title }}</strong>
              <span>{{ alarm.time }}</span>
            </div>
          </div>
        </div>
      </article>

      <article class="panel quick-panel">
        <div class="panel-header">
          <div>
            <h2>快捷入口</h2>
            <span>高频运维动作</span>
          </div>
        </div>
        <div class="quick-grid">
          <button v-for="item in quickActions" :key="item.path" class="quick-action" @click="go(item.path)">
            <svg-icon :icon-class="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </div>
      </article>

      <article class="panel flow-panel">
        <div class="panel-header">
          <div>
            <h2>最近资产变更</h2>
            <span>设备生命周期记录</span>
          </div>
          <button class="ghost-button" @click="go('/asset/flow')">变更记录</button>
        </div>
        <div class="timeline-list">
          <div v-for="flow in assetFlows" :key="flow.id" class="timeline-row">
            <span class="timeline-dot"></span>
            <div>
              <strong>{{ flow.title }}</strong>
              <span>{{ flow.time }}</span>
            </div>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listAssetInfo } from '@/api/asset/info'
import { listAssetFlow } from '@/api/asset/flow'
import { listServer } from '@/api/monitor/server'
import { getUnhandleCount, getUnhandleList } from '@/api/monitor/alarm'
import { listMonitorData } from '@/api/monitor/data'

export default {
  name: 'Index',
  data() {
    return {
      nowTimer: null,
      currentTime: '',
      resourceChart: null,
      healthChart: null,
      assetTotal: 128,
      serverTotal: 24,
      onlineServers: 21,
      alarmTotal: 3,
      flowTotal: 15,
      resourceSeries: this.defaultResourceSeries(),
      healthItems: [
        { label: '在线', value: 21, type: 'good' },
        { label: '离线', value: 2, type: 'warn' },
        { label: '异常', value: 1, type: 'bad' }
      ],
      alarmEvents: [
        { id: 'fallback-1', level: 'critical', levelText: '严重', title: '核心服务器 CPU 持续高位', time: '10 分钟前' },
        { id: 'fallback-2', level: 'warning', levelText: '警告', title: '磁盘空间超过阈值', time: '28 分钟前' },
        { id: 'fallback-3', level: 'info', levelText: '提示', title: '监控采集任务已恢复', time: '1 小时前' }
      ],
      assetFlows: [
        { id: 'flow-1', title: '新增服务器资产 SRV-024', time: '今天 09:30' },
        { id: 'flow-2', title: '资产责任人完成变更', time: '昨天 16:12' },
        { id: 'flow-3', title: '网络设备进入维护状态', time: '昨天 11:08' }
      ],
      quickActions: [
        { label: '设备资产', path: '/asset/info', icon: 'list' },
        { label: '服务器管理', path: '/ops-monitor/server', icon: 'server' },
        { label: '告警事件', path: '/ops-monitor/alarm', icon: 'bug' },
        { label: '监控数据', path: '/ops-monitor/data', icon: 'chart' }
      ]
    }
  },
  computed: {
    metrics() {
      return [
        { key: 'assets', label: '资产总数', value: this.assetTotal, icon: 'list', trend: '统一纳管', trendType: 'stable' },
        { key: 'servers', label: '服务器数', value: this.serverTotal, icon: 'server', trend: `${this.onlineRate}% 在线`, trendType: 'good' },
        { key: 'alarms', label: '待处理告警', value: this.alarmTotal, icon: 'bug', trend: this.alarmTotal > 0 ? '需关注' : '正常', trendType: this.alarmTotal > 0 ? 'danger' : 'good' },
        { key: 'flows', label: '资产变更', value: this.flowTotal, icon: 'time', trend: '生命周期', trendType: 'stable' }
      ]
    },
    onlineRate() {
      if (!this.serverTotal) {
        return 0
      }
      return Math.round((this.onlineServers / this.serverTotal) * 100)
    }
  },
  mounted() {
    this.tickTime()
    this.nowTimer = window.setInterval(this.tickTime, 30000)
    this.loadWorkbench()
    this.$nextTick(() => {
      this.initResourceChart()
      this.initHealthChart()
      window.addEventListener('resize', this.resizeCharts)
    })
  },
  beforeDestroy() {
    window.clearInterval(this.nowTimer)
    window.removeEventListener('resize', this.resizeCharts)
    if (this.resourceChart) {
      this.resourceChart.dispose()
    }
    if (this.healthChart) {
      this.healthChart.dispose()
    }
  },
  methods: {
    tickTime() {
      const now = new Date()
      const pad = value => String(value).padStart(2, '0')
      this.currentTime = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
    },
    async loadWorkbench() {
      await Promise.all([
        this.loadAssetSummary(),
        this.loadServerSummary(),
        this.loadAlarmSummary(),
        this.loadFlowSummary(),
        this.loadMonitorSummary()
      ])
      this.refreshCharts()
    },
    async loadAssetSummary() {
      try {
        const res = await listAssetInfo({ pageNum: 1, pageSize: 1 })
        this.assetTotal = this.pickTotal(res, this.assetTotal)
      } catch (e) {}
    },
    async loadServerSummary() {
      try {
        const res = await listServer({ pageNum: 1, pageSize: 100 })
        const rows = res.rows || []
        this.serverTotal = this.pickTotal(res, rows.length || this.serverTotal)
        const online = rows.filter(item => item.connectionStatus === '0' || item.monitorStatus === '0').length
        this.onlineServers = rows.length ? online : Math.min(this.onlineServers, this.serverTotal)
        const offline = Math.max(this.serverTotal - this.onlineServers, 0)
        this.healthItems = [
          { label: '在线', value: this.onlineServers, type: 'good' },
          { label: '离线', value: offline, type: 'warn' },
          { label: '异常', value: this.alarmTotal, type: 'bad' }
        ]
      } catch (e) {}
    },
    async loadAlarmSummary() {
      try {
        const countRes = await getUnhandleCount()
        this.alarmTotal = Number(countRes.data || countRes.msg || this.alarmTotal) || 0
      } catch (e) {}
      try {
        const listRes = await getUnhandleList()
        const rows = listRes.rows || listRes.data || []
        if (rows.length) {
          this.alarmEvents = rows.slice(0, 4).map((item, index) => ({
            id: item.id || index,
            level: this.mapAlarmLevel(item.alarmLevel),
            levelText: this.mapAlarmLevelText(item.alarmLevel),
            title: item.alarmMessage || `${item.hostname || item.serverIp || '服务器'} 指标异常`,
            time: item.alarmTime || '刚刚'
          }))
        }
      } catch (e) {}
    },
    async loadFlowSummary() {
      try {
        const res = await listAssetFlow({ pageNum: 1, pageSize: 4 })
        const rows = res.rows || []
        this.flowTotal = this.pickTotal(res, this.flowTotal)
        if (rows.length) {
          this.assetFlows = rows.map((item, index) => ({
            id: item.id || index,
            title: `${this.mapFlowType(item.flowType)} ${item.assetName || item.assetCode || '资产'}`,
            time: item.operateTime || item.createTime || ''
          }))
        }
      } catch (e) {}
    },
    async loadMonitorSummary() {
      try {
        const res = await listMonitorData({ pageNum: 1, pageSize: 6 })
        const rows = (res.rows || []).slice().reverse()
        if (rows.length) {
          this.resourceSeries = {
            time: rows.map(item => this.formatTimeLabel(item.collectTime)),
            cpu: rows.map(item => this.toNumber(item.cpuUsage)),
            memory: rows.map(item => this.toNumber(item.memoryUsage)),
            disk: rows.map(item => this.toNumber(item.diskUsage))
          }
        }
      } catch (e) {}
    },
    pickTotal(res, fallback) {
      return typeof res.total === 'number' ? res.total : fallback
    },
    toNumber(value) {
      const parsed = Number(value)
      return Number.isFinite(parsed) ? parsed : 0
    },
    formatTimeLabel(value) {
      if (!value) {
        return '--:--'
      }
      const text = String(value)
      return text.length >= 16 ? text.slice(11, 16) : text
    },
    mapAlarmLevel(level) {
      const value = String(level || '').toLowerCase()
      if (value === '1' || value.includes('critical') || value.includes('严重')) {
        return 'critical'
      }
      if (value === '2' || value.includes('warn') || value.includes('警告')) {
        return 'warning'
      }
      return 'info'
    },
    mapAlarmLevelText(level) {
      const mapped = this.mapAlarmLevel(level)
      return mapped === 'critical' ? '严重' : mapped === 'warning' ? '警告' : '提示'
    },
    mapFlowType(type) {
      const map = {
        '1': '新增',
        '2': '领用',
        '3': '归还',
        '4': '维修',
        '5': '报废'
      }
      return map[String(type)] || '变更'
    },
    defaultResourceSeries() {
      return {
        time: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
        cpu: [42, 48, 64, 72, 59, 53],
        memory: [58, 62, 66, 73, 69, 64],
        disk: [35, 38, 41, 45, 47, 50]
      }
    },
    initResourceChart() {
      this.resourceChart = echarts.init(this.$refs.resourceChart)
      this.refreshResourceChart()
    },
    initHealthChart() {
      this.healthChart = echarts.init(this.$refs.healthChart)
      this.refreshHealthChart()
    },
    refreshCharts() {
      this.$nextTick(() => {
        this.refreshResourceChart()
        this.refreshHealthChart()
      })
    },
    refreshResourceChart() {
      if (!this.resourceChart) {
        return
      }
      this.resourceChart.setOption({
        color: ['#48d6a8', '#59a7ff', '#f4c45a'],
        tooltip: { trigger: 'axis' },
        legend: {
          bottom: 0,
          textStyle: { color: '#8ca0b3' },
          data: ['CPU', '内存', '磁盘']
        },
        grid: { left: 36, right: 20, top: 28, bottom: 48 },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.resourceSeries.time,
          axisLine: { lineStyle: { color: '#28384a' } },
          axisLabel: { color: '#8ca0b3' }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: { formatter: '{value}%', color: '#8ca0b3' },
          splitLine: { lineStyle: { color: 'rgba(140,160,179,.16)' } }
        },
        series: [
          this.lineSeries('CPU', this.resourceSeries.cpu),
          this.lineSeries('内存', this.resourceSeries.memory),
          this.lineSeries('磁盘', this.resourceSeries.disk)
        ]
      })
    },
    refreshHealthChart() {
      if (!this.healthChart) {
        return
      }
      this.healthChart.setOption({
        color: ['#48d6a8', '#f4c45a', '#ff6b6b'],
        tooltip: { trigger: 'item' },
        series: [
          {
            type: 'pie',
            radius: ['58%', '78%'],
            center: ['50%', '50%'],
            label: { show: false },
            data: this.healthItems.map(item => ({ name: item.label, value: item.value }))
          }
        ]
      })
    },
    lineSeries(name, data) {
      return {
        name,
        type: 'line',
        smooth: true,
        symbolSize: 6,
        data,
        areaStyle: { opacity: 0.12 },
        lineStyle: { width: 3 }
      }
    },
    resizeCharts() {
      if (this.resourceChart) {
        this.resourceChart.resize()
      }
      if (this.healthChart) {
        this.healthChart.resize()
      }
    },
    go(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.ops-workbench {
  min-height: calc(100vh - 84px);
  padding: 18px;
  color: #d8e4ef;
  background:
    linear-gradient(180deg, #101827 0%, #111827 38%, #f4f7fb 38%, #f4f7fb 100%);
}

.hero-panel {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  min-height: 156px;
  padding: 26px 28px;
  border: 1px solid rgba(128, 152, 177, 0.16);
  border-radius: 8px;
  background: linear-gradient(135deg, #111b2b 0%, #16263a 55%, #102a3a 100%);
  box-shadow: 0 18px 42px rgba(14, 24, 39, 0.18);
}

.hero-copy {
  max-width: 760px;

  .eyebrow {
    margin-bottom: 10px;
    color: #48d6a8;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 0;
  }

  h1 {
    margin: 0;
    color: #ffffff;
    font-size: 30px;
    font-weight: 700;
    letter-spacing: 0;
  }

  p {
    margin: 14px 0 0;
    color: #9fb1c3;
    font-size: 15px;
    line-height: 1.7;
  }
}

.hero-status {
  min-width: 178px;
  padding: 12px 14px;
  border: 1px solid rgba(128, 152, 177, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
  color: #b9c7d5;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #48d6a8;
  box-shadow: 0 0 0 4px rgba(72, 214, 168, 0.16);
}

.status-time {
  margin-top: 12px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: -34px;
  padding: 0 16px;
}

.metric-card,
.panel {
  border: 1px solid #e4eaf1;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 12px 28px rgba(31, 45, 61, 0.08);
}

.metric-card {
  display: flex;
  align-items: center;
  min-height: 108px;
  padding: 20px;
  color: #1f2d3d;
}

.metric-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-right: 16px;
  border-radius: 8px;
  color: #ffffff;
  font-size: 22px;

  &.assets { background: #4169e1; }
  &.servers { background: #13b98d; }
  &.alarms { background: #f05252; }
  &.flows { background: #7c5ce0; }
}

.metric-body {
  flex: 1;
}

.metric-value {
  color: #172033;
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
}

.metric-label {
  margin-top: 8px;
  color: #7b8794;
  font-size: 14px;
}

.metric-trend {
  align-self: flex-start;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;

  &.good {
    color: #0f8f68;
    background: #e8f8f2;
  }

  &.danger {
    color: #d64242;
    background: #fff0f0;
  }

  &.stable {
    color: #506174;
    background: #edf2f7;
  }
}

.main-grid,
.bottom-grid {
  display: grid;
  gap: 18px;
  margin-top: 18px;
}

.main-grid {
  grid-template-columns: minmax(0, 2fr) minmax(320px, 0.8fr);
}

.bottom-grid {
  grid-template-columns: 1.1fr 0.8fr 1.1fr;
}

.panel {
  padding: 22px;
  color: #172033;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;

  h2 {
    margin: 0;
    color: #172033;
    font-size: 18px;
    font-weight: 700;
  }

  span {
    display: block;
    margin-top: 6px;
    color: #8a96a3;
    font-size: 13px;
  }
}

.ghost-button {
  height: 32px;
  padding: 0 12px;
  border: 1px solid #d9e2ec;
  border-radius: 6px;
  background: #ffffff;
  color: #506174;
  cursor: pointer;

  &:hover {
    border-color: #4169e1;
    color: #4169e1;
  }

  &.danger:hover {
    border-color: #f05252;
    color: #f05252;
  }
}

.resource-chart {
  height: 330px;
  border-radius: 8px;
  background: #101827;
}

.health-chart {
  height: 210px;
}

.health-list {
  display: grid;
  gap: 10px;
}

.health-item {
  display: grid;
  grid-template-columns: 14px 1fr auto;
  align-items: center;
  color: #506174;
  font-size: 14px;

  strong {
    color: #172033;
    font-size: 16px;
  }
}

.health-mark {
  width: 8px;
  height: 8px;
  border-radius: 50%;

  &.good { background: #48d6a8; }
  &.warn { background: #f4c45a; }
  &.bad { background: #ff6b6b; }
}

.event-list,
.timeline-list {
  display: grid;
  gap: 12px;
}

.event-row {
  display: grid;
  grid-template-columns: 52px 1fr;
  gap: 12px;
  align-items: center;
  min-height: 54px;
  padding: 12px;
  border-radius: 8px;
  background: #f7f9fc;
}

.event-level {
  height: 26px;
  border-radius: 6px;
  text-align: center;
  line-height: 26px;
  font-size: 12px;
  font-weight: 700;

  &.critical {
    color: #ffffff;
    background: #f05252;
  }

  &.warning {
    color: #7a4f00;
    background: #ffe4a3;
  }

  &.info {
    color: #265c99;
    background: #dcecff;
  }
}

.event-main,
.timeline-row div {
  min-width: 0;

  strong {
    display: block;
    overflow: hidden;
    color: #172033;
    font-size: 14px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  span {
    display: block;
    margin-top: 6px;
    color: #8a96a3;
    font-size: 12px;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.quick-action {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 76px;
  border: 1px solid #e4eaf1;
  border-radius: 8px;
  background: #f7f9fc;
  color: #172033;
  cursor: pointer;
  font-weight: 700;

  .svg-icon {
    margin-right: 8px;
    color: #4169e1;
    font-size: 20px;
  }

  &:hover {
    border-color: #4169e1;
    background: #eef4ff;
  }
}

.timeline-row {
  display: grid;
  grid-template-columns: 14px 1fr;
  gap: 10px;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #eef2f6;

  &:last-child {
    border-bottom: 0;
  }
}

.timeline-dot {
  width: 9px;
  height: 9px;
  margin-top: 4px;
  border-radius: 50%;
  background: #4169e1;
  box-shadow: 0 0 0 4px #e7edff;
}

@media (max-width: 1280px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ops-workbench {
    padding: 12px;
  }

  .hero-panel {
    display: block;
    padding: 22px;
  }

  .hero-status {
    margin-top: 18px;
  }

  .metric-grid {
    grid-template-columns: 1fr;
    margin-top: 14px;
    padding: 0;
  }

  .metric-card {
    min-height: 96px;
  }

  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
