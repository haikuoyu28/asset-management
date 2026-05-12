<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="服务器IP" prop="serverIp">
        <el-input
          v-model="queryParams.serverIp"
          placeholder="请输入服务器IP"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主机名" prop="hostname">
        <el-input
          v-model="queryParams.hostname"
          placeholder="请输入主机名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监控状态" prop="monitorStatus">
        <el-select v-model="queryParams.monitorStatus" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.monitor_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="连接状态" prop="connectionStatus">
        <el-select v-model="queryParams.connectionStatus" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.connection_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['monitor:server:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['monitor:server:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="serverList">
      <el-table-column label="服务器IP" align="center" prop="serverIp" width="140" />
      <el-table-column label="主机名" align="center" prop="hostname" :show-overflow-tooltip="true" />
      <el-table-column label="操作系统" align="center" prop="osName" :show-overflow-tooltip="true" />
      <el-table-column label="CPU核心" align="center" prop="cpuCores" width="80" />
      <el-table-column label="内存(GB)" align="center" prop="memoryGb" width="90" />
      <el-table-column label="磁盘(GB)" align="center" prop="diskGb" width="90" />
      <el-table-column label="连接状态" align="center" prop="connectionStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.connection_status" :value="scope.row.connectionStatus" />
        </template>
      </el-table-column>
      <el-table-column label="监控状态" align="center" prop="monitorStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.monitor_status" :value="scope.row.monitorStatus" />
        </template>
      </el-table-column>
      <el-table-column label="上次采集" align="center" prop="lastCollectTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastCollectTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleMonitor(scope.row)"
            v-hasPermi="['monitor:data:query']"
          >监控</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['monitor:server:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:server:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改服务器对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="服务器IP" prop="serverIp">
              <el-input v-model="form.serverIp" placeholder="请输入服务器IP" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主机名" prop="hostname">
              <el-input v-model="form.hostname" placeholder="请输入主机名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作系统" prop="osName">
              <el-input v-model="form.osName" placeholder="如：CentOS 7.9" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SSH端口" prop="sshPort">
              <el-input-number v-model="form.sshPort" :min="1" :max="65535" placeholder="22" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="CPU核心数" prop="cpuCores">
              <el-input-number v-model="form.cpuCores" :min="1" placeholder="如：8" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内存(GB)" prop="memoryGb">
              <el-input-number v-model="form.memoryGb" :min="1" placeholder="如：16" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="磁盘(GB)" prop="diskGb">
              <el-input-number v-model="form.diskGb" :min="1" placeholder="如：500" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="连接状态" prop="connectionStatus">
              <el-radio-group v-model="form.connectionStatus">
                <el-radio label="0">在线</el-radio>
                <el-radio label="1">离线</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="SSH用户名" prop="sshUsername">
              <el-input v-model="form.sshUsername" placeholder="请输入SSH用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SSH密码" prop="sshPassword">
              <el-input type="password" v-model="form.sshPassword" placeholder="请输入SSH密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Agent路径" prop="agentPath">
          <el-input v-model="form.agentPath" placeholder="如：http://192.168.1.100:9090/monitor/data" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 监控图表对话框 -->
    <el-dialog :title="'实时监控 - ' + monitorServer.serverIp" :visible.sync="monitorOpen" width="1200px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <div slot="header">服务器信息</div>
            <p><strong>主机名：</strong>{{ monitorServer.hostname }}</p>
            <p><strong>操作系统：</strong>{{ monitorServer.osName }}</p>
            <p><strong>CPU：</strong>{{ monitorServer.cpuCores }} 核</p>
            <p><strong>内存：</strong>{{ monitorServer.memoryGb }} GB</p>
            <p><strong>磁盘：</strong>{{ monitorServer.diskGb }} GB</p>
          </el-card>
        </el-col>
        <el-col :span="18">
          <el-row :gutter="10" style="margin-bottom: 10px;">
            <el-col :span="8">
              <div class="stat-card cpu">
                <div class="stat-label">CPU使用率</div>
                <div class="stat-value">{{ latestData.cpuUsage || 0 }}%</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card memory">
                <div class="stat-label">内存使用率</div>
                <div class="stat-value">{{ latestData.memoryUsage || 0 }}%</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-card disk">
                <div class="stat-label">磁盘使用率</div>
                <div class="stat-value">{{ latestData.diskUsage || 0 }}%</div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="12">
              <div id="cpuChart" style="width: 100%; height: 300px;"></div>
            </el-col>
            <el-col :span="12">
              <div id="memoryChart" style="width: 100%; height: 300px;"></div>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="monitorOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listServer, getServer, addServer, updateServer, delServer } from "@/api/monitor/server"
import { getRecentData, getLatestData } from "@/api/monitor/data"

export default {
  name: "MonitorServer",
  dicts: ['monitor_status', 'connection_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      serverList: [],
      title: "",
      open: false,
      monitorOpen: false,
      monitorServer: {},
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
  },
  methods: {
    getList() {
      this.loading = true
      listServer(this.queryParams).then(response => {
        this.serverList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        serverIp: null,
        hostname: null,
        osName: null,
        cpuCores: null,
        memoryGb: null,
        diskGb: null,
        monitorStatus: "2",
        connectionStatus: "0",
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
      this.title = "添加服务器"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getServer(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改服务器"
      })
    },
    handleMonitor(row) {
      this.monitorServer = row
      this.monitorOpen = true
      this.loadMonitorData()
      this.startMonitorTimer()
    },
    loadMonitorData() {
      getLatestData(this.monitorServer.id).then(response => {
        if (response.data) {
          this.latestData = response.data
        }
      })
      getRecentData(this.monitorServer.id, 20).then(response => {
        this.recentDataList = response.data || []
        this.initCharts()
      })
    },
    startMonitorTimer() {
      if (this.monitorTimer) {
        clearInterval(this.monitorTimer)
      }
      this.monitorTimer = setInterval(() => {
        this.loadMonitorData()
      }, 30000)
    },
    initCharts() {
      this.$nextTick(() => {
        if (this.recentDataList.length > 0) {
          const times = this.recentDataList.map(item => {
            return this.parseTime(item.collectTime, '{HH}:{mm}')
          }).reverse()
          const cpuData = this.recentDataList.map(item => item.cpuUsage || 0).reverse()
          const memoryData = this.recentDataList.map(item => item.memoryUsage || 0).reverse()

          if (!this.cpuChart) {
            this.cpuChart = echarts.init(document.getElementById('cpuChart'))
          }
          this.cpuChart.setOption({
            title: { text: 'CPU使用率趋势', left: 'center' },
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: times },
            yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
            series: [{ data: cpuData, type: 'line', smooth: true, areaStyle: { opacity: 0.3 } }]
          })

          if (!this.memoryChart) {
            this.memoryChart = echarts.init(document.getElementById('memoryChart'))
          }
          this.memoryChart.setOption({
            title: { text: '内存使用率趋势', left: 'center' },
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: times },
            yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
            series: [{ data: memoryData, type: 'line', smooth: true, areaStyle: { opacity: 0.3 } }]
          })
        }
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id) {
            updateServer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addServer(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除服务器IP为"' + row.serverIp + '"的数据项？')
        .then(() => {
          return delServer(row.id)
        }).then(() => {
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
    if (this.monitorTimer) {
      clearInterval(this.monitorTimer)
    }
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
.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  color: white;
  text-align: center;
}
.stat-card.cpu {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.stat-card.memory {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}
.stat-card.disk {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}
.stat-label {
  font-size: 14px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
</style>
