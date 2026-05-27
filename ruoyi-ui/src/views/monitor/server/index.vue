<template>
  <div class="app-container server-page">
    <div class="page-head">
      <div>
        <div class="page-title">服务器管理</div>
        <div class="page-subtitle">管理纳入监控的服务器，并关联设备资产台账。</div>
      </div>
      <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['monitor:server:add']">
        新增服务器
      </el-button>
    </div>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="query-bar">
      <el-form-item label="服务器IP" prop="serverIp">
        <el-input v-model="queryParams.serverIp" placeholder="输入服务器 IP" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="主机名" prop="hostname">
        <el-input v-model="queryParams.hostname" placeholder="输入主机名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="监控状态" prop="monitorStatus">
        <el-select v-model="queryParams.monitorStatus" placeholder="全部" clearable>
          <el-option v-for="dict in dict.type.monitor_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="连接状态" prop="connectionStatus">
        <el-select v-model="queryParams.connectionStatus" placeholder="全部" clearable>
          <el-option v-for="dict in dict.type.connection_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['monitor:server:export']">
          导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="serverList" class="business-table">
      <el-table-column label="服务器IP" align="center" prop="serverIp" width="140" />
      <el-table-column label="主机名" align="left" prop="hostname" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="关联资产" align="left" min-width="180" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.assetCode">{{ scope.row.assetCode }} / {{ scope.row.assetName }}</span>
          <el-tag v-else size="mini" type="info">未关联</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作系统" align="left" prop="osName" min-width="130" :show-overflow-tooltip="true" />
      <el-table-column label="资源" align="center" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.cpuCores || '-' }}C / {{ scope.row.memoryGb || '-' }}G / {{ scope.row.diskGb || '-' }}G</span>
        </template>
      </el-table-column>
      <el-table-column label="连接" align="center" prop="connectionStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.connection_status" :value="scope.row.connectionStatus" />
        </template>
      </el-table-column>
      <el-table-column label="监控" align="center" prop="monitorStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.monitor_status" :value="scope.row.monitorStatus" />
        </template>
      </el-table-column>
      <el-table-column label="最近采集" align="center" prop="lastCollectTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastCollectTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="340">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-data-line" @click="handleMonitor(scope.row)" v-hasPermi="['monitor:data:query']">
            监控
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-upload2" @click="handleMockReport(scope.row)" v-hasPermi="['monitor:data:add']">
            上报
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-connection" @click="handleAgent(scope.row)" v-hasPermi="['monitor:server:edit']">
            Agent
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:server:edit']">
            编辑
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:server:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="关联资产" prop="assetId">
          <el-select
            v-model="form.assetId"
            filterable
            clearable
            placeholder="选择服务器类资产"
            style="width: 100%"
            @change="handleAssetChange"
          >
            <el-option
              v-for="asset in assetOptions"
              :key="asset.id"
              :label="formatAssetOption(asset)"
              :value="asset.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="服务器IP" prop="serverIp">
              <el-input v-model="form.serverIp" placeholder="服务器 IP" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主机名" prop="hostname">
              <el-input v-model="form.hostname" placeholder="主机名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="操作系统" prop="osName">
              <el-input v-model="form.osName" placeholder="例如 CentOS 7.9" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Agent地址" prop="agentPath">
              <el-input v-model="form.agentPath" placeholder="Agent/API 地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="CPU核数" prop="cpuCores">
              <el-input-number v-model="form.cpuCores" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="内存GB" prop="memoryGb">
              <el-input-number v-model="form.memoryGb" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="磁盘GB" prop="diskGb">
              <el-input-number v-model="form.diskGb" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="连接状态" prop="connectionStatus">
              <el-radio-group v-model="form.connectionStatus">
                <el-radio label="0">在线</el-radio>
                <el-radio label="1">离线</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控状态" prop="monitorStatus">
              <el-radio-group v-model="form.monitorStatus">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">异常</el-radio>
                <el-radio label="2">未知</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert type="info" :closable="false" show-icon class="mb16">
          <template slot="title">
            当前版本不保存 SSH 密码。请优先使用 Agent/API 地址进行监控纳管。
          </template>
        </el-alert>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="补充说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="'Agent 配置 - ' + (agentServer.serverIp || '')" :visible.sync="agentOpen" width="720px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon class="mb16">
        <template slot="title">
          Token 只在重置后展示一次，请放入目标服务器的 Agent 配置文件，不要提交到 Git。
        </template>
      </el-alert>
      <el-descriptions :column="2" border class="mb16">
        <el-descriptions-item label="服务器ID">{{ agentServer.id }}</el-descriptions-item>
        <el-descriptions-item label="服务器IP">{{ agentServer.serverIp }}</el-descriptions-item>
        <el-descriptions-item label="主机名">{{ agentServer.hostname }}</el-descriptions-item>
        <el-descriptions-item label="Agent状态">
          <el-tag size="mini" :type="agentServer.agentEnabled === '0' ? 'success' : 'info'">
            {{ agentServer.agentEnabled === '0' ? '已启用' : '未启用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-input
        v-model="agentConfigText"
        type="textarea"
        :rows="12"
        readonly
        placeholder="点击重置 Token 后生成 Agent 配置"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" icon="el-icon-refresh" @click="handleResetAgentToken">重置 Token 并生成配置</el-button>
        <el-button @click="agentOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="'实时监控 - ' + (monitorServer.serverIp || '')" :visible.sync="monitorOpen" width="1080px" append-to-body @close="stopMonitorTimer">
      <el-row :gutter="16">
        <el-col :span="6">
          <div class="server-card">
            <div class="server-ip">{{ monitorServer.serverIp }}</div>
            <div class="server-name">{{ monitorServer.hostname }}</div>
            <div class="server-meta">资产：{{ monitorServer.assetCode || '未关联' }}</div>
            <div class="server-meta">系统：{{ monitorServer.osName || '-' }}</div>
            <div class="server-meta">资源：{{ monitorServer.cpuCores || '-' }}C / {{ monitorServer.memoryGb || '-' }}G / {{ monitorServer.diskGb || '-' }}G</div>
          </div>
        </el-col>
        <el-col :span="18">
          <el-row :gutter="12" class="metric-row">
            <el-col :span="8">
              <div class="metric-card cpu">
                <span>CPU使用率</span>
                <strong>{{ latestData.cpuUsage || 0 }}%</strong>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-card memory">
                <span>内存使用率</span>
                <strong>{{ latestData.memoryUsage || 0 }}%</strong>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-card disk">
                <span>磁盘使用率</span>
                <strong>{{ latestData.diskUsage || 0 }}%</strong>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12">
              <div id="cpuChart" class="chart-box"></div>
            </el-col>
            <el-col :span="12">
              <div id="memoryChart" class="chart-box"></div>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="monitorOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listServer, getServer, addServer, updateServer, delServer, resetAgentToken } from "@/api/monitor/server"
import { getRecentData, getLatestData, reportMonitorData } from "@/api/monitor/data"
import { listAssetInfo } from "@/api/asset/info"

export default {
  name: "MonitorServer",
  dicts: ['monitor_status', 'connection_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      serverList: [],
      assetOptions: [],
      title: "",
      open: false,
      monitorOpen: false,
      monitorServer: {},
      agentOpen: false,
      agentServer: {},
      agentConfigText: "",
      latestData: {},
      recentDataList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serverIp: null,
        hostname: null,
        monitorStatus: null,
        connectionStatus: null
      },
      form: {},
      rules: {
        serverIp: [{ required: true, message: "服务器IP不能为空", trigger: "blur" }],
        hostname: [{ required: true, message: "主机名不能为空", trigger: "blur" }]
      },
      cpuChart: null,
      memoryChart: null,
      monitorTimer: null
    }
  },
  created() {
    this.getList()
    this.getAssetOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listServer(this.queryParams).then(response => {
        this.serverList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getAssetOptions() {
      listAssetInfo({ pageNum: 1, pageSize: 200, assetType: '1' }).then(response => {
        this.assetOptions = response.rows || []
      })
    },
    formatAssetOption(asset) {
      return `${asset.assetCode || '-'} / ${asset.assetName || '-'} / ${asset.ipAddress || '无IP'}`
    },
    handleAssetChange(assetId) {
      const asset = this.assetOptions.find(item => String(item.id) === String(assetId))
      if (!asset) {
        return
      }
      this.form.serverIp = asset.ipAddress || this.form.serverIp
      this.form.hostname = asset.assetName || this.form.hostname
      this.form.osName = asset.model || this.form.osName
      this.form.agentPath = asset.ipAddress ? `http://${asset.ipAddress}:9090/monitor/data` : this.form.agentPath
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    reset() {
      this.form = {
        id: null,
        assetId: null,
        serverIp: null,
        hostname: null,
        osName: null,
        cpuCores: null,
        memoryGb: null,
        diskGb: null,
        monitorStatus: "2",
        connectionStatus: "1",
        sshPort: 22,
        sshUsername: null,
        sshPassword: null,
        agentPath: null,
        remark: null
      }
      this.resetForm("form")
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增服务器"
    },
    handleUpdate(row) {
      this.reset()
      getServer(row.id).then(response => {
        this.form = { ...(response.data || {}), sshPassword: null }
        this.open = true
        this.title = "编辑服务器"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return
        }
        const payload = { ...this.form, sshPassword: null }
        const action = payload.id ? updateServer(payload) : addServer(payload)
        action.then(() => {
          this.$modal.msgSuccess(payload.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleMonitor(row) {
      this.monitorServer = row
      this.monitorOpen = true
      this.loadMonitorData()
      this.startMonitorTimer()
    },
    handleAgent(row) {
      this.agentServer = { ...row, agentToken: null }
      this.agentConfigText = this.buildAgentConfig(this.agentServer)
      this.agentOpen = true
    },
    handleResetAgentToken() {
      resetAgentToken(this.agentServer.id).then(response => {
        this.agentServer = response.data || this.agentServer
        this.agentConfigText = this.buildAgentConfig(this.agentServer)
        this.$modal.msgSuccess('Agent Token 已重置')
        this.getList()
      })
    },
    buildAgentConfig(server) {
      return JSON.stringify({
        serverId: server.id || null,
        endpoint: 'http://localhost:8080/monitor/data/agent/report',
        token: server.agentToken || '<点击重置后生成>',
        intervalSeconds: 30,
        diskPath: '/'
      }, null, 2)
    },
    handleMockReport(row) {
      const cpuUsage = this.randomMetric(25, 92)
      const memoryUsage = this.randomMetric(35, 94)
      const diskUsage = this.randomMetric(45, 96)
      const payload = {
        serverId: row.id,
        serverIp: row.serverIp,
        cpuUsage,
        memoryUsage,
        diskUsage,
        memoryTotalGb: row.memoryGb || null,
        diskTotalGb: row.diskGb || null,
        memoryUsedGb: row.memoryGb ? Number((row.memoryGb * memoryUsage / 100).toFixed(2)) : null,
        diskUsedGb: row.diskGb ? Number((row.diskGb * diskUsage / 100).toFixed(2)) : null,
        networkIn: this.randomMetric(80, 280),
        networkOut: this.randomMetric(60, 220),
        loadAverage: '0.42, 0.48, 0.55',
        runningProcesses: Math.floor(this.randomMetric(90, 180))
      }
      reportMonitorData(payload).then(() => {
        this.$modal.msgSuccess('监控数据已上报')
        this.getList()
        if (this.monitorOpen && this.monitorServer.id === row.id) {
          this.loadMonitorData()
        }
      })
    },
    randomMetric(min, max) {
      return Number((Math.random() * (max - min) + min).toFixed(2))
    },
    loadMonitorData() {
      getLatestData(this.monitorServer.id).then(response => {
        this.latestData = response.data || {}
      }).catch(() => {
        this.latestData = {}
      })
      getRecentData(this.monitorServer.id, 20).then(response => {
        this.recentDataList = response.data || []
        this.initCharts()
      }).catch(() => {
        this.recentDataList = []
        this.initCharts()
      })
    },
    startMonitorTimer() {
      this.stopMonitorTimer()
      this.monitorTimer = setInterval(() => {
        this.loadMonitorData()
      }, 30000)
    },
    stopMonitorTimer() {
      if (this.monitorTimer) {
        clearInterval(this.monitorTimer)
        this.monitorTimer = null
      }
    },
    initCharts() {
      this.$nextTick(() => {
        const rows = this.recentDataList.length ? this.recentDataList.slice().reverse() : []
        const times = rows.map(item => this.parseTime(item.collectTime, '{HH}:{mm}'))
        const cpuData = rows.map(item => item.cpuUsage || 0)
        const memoryData = rows.map(item => item.memoryUsage || 0)
        if (!this.cpuChart) {
          this.cpuChart = echarts.init(document.getElementById('cpuChart'))
        }
        if (!this.memoryChart) {
          this.memoryChart = echarts.init(document.getElementById('memoryChart'))
        }
        this.cpuChart.setOption(this.buildLineOption('CPU使用率趋势', times, cpuData, '#3b82f6'))
        this.memoryChart.setOption(this.buildLineOption('内存使用率趋势', times, memoryData, '#10b981'))
      })
    },
    buildLineOption(title, times, data, color) {
      return {
        title: { text: title, left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        grid: { top: 48, left: 36, right: 16, bottom: 28 },
        xAxis: { type: 'category', data: times },
        yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
        series: [{ data, type: 'line', smooth: true, symbolSize: 6, lineStyle: { color }, itemStyle: { color }, areaStyle: { opacity: 0.12 } }]
      }
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除服务器 "${row.serverIp}" 吗？`)
        .then(() => delServer(row.id))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        }).catch(() => {})
    },
    handleExport() {
      this.download('/monitor/server/export', {
        ...this.queryParams
      }, `monitor_server_${new Date().getTime()}.xlsx`)
    }
  },
  beforeDestroy() {
    this.stopMonitorTimer()
    if (this.cpuChart) {
      this.cpuChart.dispose()
    }
    if (this.memoryChart) {
      this.memoryChart.dispose()
    }
  }
}
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2d3d;
}

.page-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #7b8794;
}

.query-bar {
  padding: 14px 16px 2px;
  margin-bottom: 12px;
  background: #f8fafc;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.server-card {
  min-height: 220px;
  padding: 18px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #f8fafc;
}

.server-ip {
  font-size: 22px;
  font-weight: 700;
  color: #1f2d3d;
}

.server-name {
  margin-top: 6px;
  color: #475569;
}

.server-meta {
  margin-top: 12px;
  color: #64748b;
  line-height: 1.5;
}

.metric-row {
  margin-bottom: 12px;
}

.metric-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 6px;
  color: #fff;
}

.metric-card strong {
  font-size: 24px;
}

.metric-card.cpu {
  background: #2563eb;
}

.metric-card.memory {
  background: #059669;
}

.metric-card.disk {
  background: #d97706;
}

.chart-box {
  height: 300px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.mb16 {
  margin-bottom: 16px;
}
</style>
