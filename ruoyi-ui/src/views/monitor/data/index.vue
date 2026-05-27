<template>
  <div class="app-container data-page">
    <div class="page-head">
      <div>
        <div class="page-title">监控数据</div>
        <div class="page-subtitle">查看服务器指标上报记录，验证采集链路和告警触发结果。</div>
      </div>
      <el-button type="primary" icon="el-icon-upload2" size="small" @click="handleReport" v-hasPermi="['monitor:data:add']">
        模拟上报
      </el-button>
    </div>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="query-bar">
      <el-form-item label="服务器" prop="serverId">
        <el-select v-model="queryParams.serverId" filterable clearable placeholder="全部服务器">
          <el-option
            v-for="server in serverOptions"
            :key="server.id"
            :label="formatServerOption(server)"
            :value="server.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="服务器IP" prop="serverIp">
        <el-input v-model="queryParams.serverIp" placeholder="输入服务器 IP" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12" class="metric-summary">
      <el-col :span="6">
        <div class="metric-card cpu">
          <span>CPU</span>
          <strong>{{ latestData.cpuUsage || 0 }}%</strong>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="metric-card memory">
          <span>内存</span>
          <strong>{{ latestData.memoryUsage || 0 }}%</strong>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="metric-card disk">
          <span>磁盘</span>
          <strong>{{ latestData.diskUsage || 0 }}%</strong>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="metric-card time">
          <span>最近采集</span>
          <strong>{{ latestData.collectTime ? parseTime(latestData.collectTime, '{HH}:{mm}:{ss}') : '-' }}</strong>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" class="business-table">
      <el-table-column label="服务器IP" align="center" prop="serverIp" width="140" />
      <el-table-column label="CPU" align="center" prop="cpuUsage" width="90">
        <template slot-scope="scope">{{ formatPercent(scope.row.cpuUsage) }}</template>
      </el-table-column>
      <el-table-column label="内存" align="center" prop="memoryUsage" width="90">
        <template slot-scope="scope">{{ formatPercent(scope.row.memoryUsage) }}</template>
      </el-table-column>
      <el-table-column label="内存用量" align="center" width="130">
        <template slot-scope="scope">{{ formatCapacity(scope.row.memoryUsedGb, scope.row.memoryTotalGb) }}</template>
      </el-table-column>
      <el-table-column label="磁盘" align="center" prop="diskUsage" width="90">
        <template slot-scope="scope">{{ formatPercent(scope.row.diskUsage) }}</template>
      </el-table-column>
      <el-table-column label="磁盘用量" align="center" width="130">
        <template slot-scope="scope">{{ formatCapacity(scope.row.diskUsedGb, scope.row.diskTotalGb) }}</template>
      </el-table-column>
      <el-table-column label="网络入/出" align="center" width="150">
        <template slot-scope="scope">{{ scope.row.networkIn || 0 }} / {{ scope.row.networkOut || 0 }} KB/s</template>
      </el-table-column>
      <el-table-column label="负载" align="center" prop="loadAverage" width="120" />
      <el-table-column label="进程数" align="center" prop="runningProcesses" width="90" />
      <el-table-column label="采集时间" align="center" prop="collectTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.collectTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="90" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:data:remove']">
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

    <el-dialog title="模拟上报监控数据" :visible.sync="reportOpen" width="620px" append-to-body>
      <el-form ref="reportForm" :model="reportForm" :rules="reportRules" label-width="110px">
        <el-form-item label="服务器" prop="serverId">
          <el-select v-model="reportForm.serverId" filterable placeholder="选择服务器" style="width: 100%" @change="handleReportServerChange">
            <el-option
              v-for="server in serverOptions"
              :key="server.id"
              :label="formatServerOption(server)"
              :value="server.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="CPU%" prop="cpuUsage">
              <el-input-number v-model="reportForm.cpuUsage" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="内存%" prop="memoryUsage">
              <el-input-number v-model="reportForm.memoryUsage" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="磁盘%" prop="diskUsage">
              <el-input-number v-model="reportForm.diskUsage" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert type="info" :closable="false" show-icon>
          <template slot="title">
            这里是无 Agent 阶段的验证入口；真实 Agent 后续会直接调用同一个上报接口。
          </template>
        </el-alert>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReport">确定上报</el-button>
        <el-button @click="reportOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMonitorData, getLatestData, reportMonitorData, delMonitorData } from '@/api/monitor/data'
import { listServer } from '@/api/monitor/server'

export default {
  name: 'MonitorData',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      serverOptions: [],
      latestData: {},
      reportOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serverId: null,
        serverIp: null
      },
      reportForm: {},
      reportRules: {
        serverId: [{ required: true, message: '服务器不能为空', trigger: 'change' }],
        cpuUsage: [{ required: true, message: 'CPU 使用率不能为空', trigger: 'blur' }],
        memoryUsage: [{ required: true, message: '内存使用率不能为空', trigger: 'blur' }],
        diskUsage: [{ required: true, message: '磁盘使用率不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getServerOptions()
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMonitorData(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
        this.loadLatest()
      }).catch(() => {
        this.loading = false
      })
    },
    getServerOptions() {
      listServer({ pageNum: 1, pageSize: 200 }).then(response => {
        this.serverOptions = response.rows || []
      })
    },
    loadLatest() {
      if (!this.queryParams.serverId && this.dataList.length) {
        this.latestData = this.dataList[0]
        return
      }
      if (!this.queryParams.serverId) {
        this.latestData = {}
        return
      }
      getLatestData(this.queryParams.serverId).then(response => {
        this.latestData = response.data || {}
      }).catch(() => {
        this.latestData = {}
      })
    },
    formatServerOption(server) {
      return `${server.serverIp || '-'} / ${server.hostname || '-'}`
    },
    formatPercent(value) {
      return value === null || value === undefined ? '-' : `${value}%`
    },
    formatCapacity(used, total) {
      if (used === null || used === undefined || total === null || total === undefined) {
        return '-'
      }
      return `${used} / ${total} GB`
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleReport() {
      this.reportForm = {
        serverId: this.queryParams.serverId || null,
        serverIp: null,
        cpuUsage: 35,
        memoryUsage: 45,
        diskUsage: 55,
        memoryUsedGb: null,
        memoryTotalGb: null,
        diskUsedGb: null,
        diskTotalGb: null,
        networkIn: 128,
        networkOut: 96,
        loadAverage: '0.36, 0.42, 0.48',
        runningProcesses: 128
      }
      this.handleReportServerChange(this.reportForm.serverId)
      this.reportOpen = true
    },
    handleReportServerChange(serverId) {
      const server = this.serverOptions.find(item => String(item.id) === String(serverId))
      if (!server) {
        return
      }
      this.reportForm.serverIp = server.serverIp
      this.reportForm.memoryTotalGb = server.memoryGb || null
      this.reportForm.diskTotalGb = server.diskGb || null
      this.reportForm.memoryUsedGb = server.memoryGb ? Number((server.memoryGb * this.reportForm.memoryUsage / 100).toFixed(2)) : null
      this.reportForm.diskUsedGb = server.diskGb ? Number((server.diskGb * this.reportForm.diskUsage / 100).toFixed(2)) : null
    },
    submitReport() {
      this.$refs.reportForm.validate(valid => {
        if (!valid) {
          return
        }
        this.handleReportServerChange(this.reportForm.serverId)
        reportMonitorData(this.reportForm).then(() => {
          this.$modal.msgSuccess('上报成功，规则命中时会自动生成告警')
          this.reportOpen = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除 ${row.serverIp} 的这条采集记录吗？`)
        .then(() => delMonitorData(row.id))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        }).catch(() => {})
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

.metric-summary {
  margin-bottom: 14px;
}

.metric-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 72px;
  padding: 16px;
  border-radius: 6px;
  color: #fff;
}

.metric-card span {
  font-size: 13px;
  opacity: 0.9;
}

.metric-card strong {
  font-size: 24px;
}

.metric-card.cpu { background: #2563eb; }
.metric-card.memory { background: #059669; }
.metric-card.disk { background: #d97706; }
.metric-card.time { background: #475569; }
</style>
