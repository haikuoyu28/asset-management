<template>
  <div class="app-container alarm-page">
    <div class="page-head">
      <div>
        <div class="page-title">告警事件</div>
        <div class="page-subtitle">跟踪服务器告警状态，记录处理动作和结果。</div>
      </div>
      <el-button icon="el-icon-refresh" size="small" @click="getList">刷新</el-button>
    </div>

    <el-tabs v-model="statusTab" class="status-tabs" @tab-click="handleStatusTab">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="未处理" name="0" />
      <el-tab-pane label="处理中" name="1" />
      <el-tab-pane label="已处理" name="2" />
      <el-tab-pane label="已忽略" name="3" />
    </el-tabs>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="query-bar">
      <el-form-item label="服务器IP" prop="serverIp">
        <el-input v-model="queryParams.serverIp" placeholder="输入服务器 IP" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="告警类型" prop="alarmType">
        <el-select v-model="queryParams.alarmType" placeholder="全部类型" clearable>
          <el-option v-for="dict in dict.type.alarm_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="告警级别" prop="alarmLevel">
        <el-select v-model="queryParams.alarmLevel" placeholder="全部级别" clearable>
          <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmList" class="business-table">
      <el-table-column label="服务器IP" align="center" prop="serverIp" width="130" />
      <el-table-column label="主机名" align="left" prop="hostname" min-width="130" :show-overflow-tooltip="true" />
      <el-table-column label="类型" align="center" prop="alarmType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_type" :value="scope.row.alarmType" />
        </template>
      </el-table-column>
      <el-table-column label="级别" align="center" prop="alarmLevel" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.alarmLevel" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="alarmStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_status" :value="scope.row.alarmStatus" />
        </template>
      </el-table-column>
      <el-table-column label="告警值" align="center" prop="alarmValue" width="100">
        <template slot-scope="scope">
          <span :class="getValueClass(scope.row)">{{ scope.row.alarmValue }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="告警消息" align="left" prop="alarmMessage" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="告警时间" align="center" prop="alarmTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handlePerson" width="100" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="190">
        <template slot-scope="scope">
          <template v-if="canHandle(scope.row)">
            <el-button size="mini" type="text" icon="el-icon-check" @click="handleProcess(scope.row)" v-hasPermi="['monitor:alarm:edit']">
              处理
            </el-button>
            <el-button size="mini" type="text" icon="el-icon-close" @click="handleIgnore(scope.row)" v-hasPermi="['monitor:alarm:edit']">
              忽略
            </el-button>
          </template>
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)" v-hasPermi="['monitor:alarm:query']">
            详情
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

    <el-dialog title="处理告警" :visible.sync="handleOpen" width="620px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-descriptions :column="2" border class="mb16">
          <el-descriptions-item label="服务器IP">{{ handleForm.serverIp }}</el-descriptions-item>
          <el-descriptions-item label="主机名">{{ handleForm.hostname }}</el-descriptions-item>
          <el-descriptions-item label="类型">
            <dict-tag :options="dict.type.alarm_type" :value="handleForm.alarmType" />
          </el-descriptions-item>
          <el-descriptions-item label="级别">
            <dict-tag :options="dict.type.alarm_level" :value="handleForm.alarmLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="告警值">{{ handleForm.alarmValue }}%</el-descriptions-item>
          <el-descriptions-item label="阈值">{{ handleForm.thresholdValue }}%</el-descriptions-item>
          <el-descriptions-item label="告警消息" :span="2">{{ handleForm.alarmMessage }}</el-descriptions-item>
        </el-descriptions>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="4" placeholder="记录定位过程、处置动作和结论" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitHandle">确定处理</el-button>
        <el-button @click="handleOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="告警详情" :visible.sync="detailOpen" width="760px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务器IP">{{ detailData.serverIp }}</el-descriptions-item>
        <el-descriptions-item label="主机名">{{ detailData.hostname }}</el-descriptions-item>
        <el-descriptions-item label="告警类型">
          <dict-tag :options="dict.type.alarm_type" :value="detailData.alarmType" />
        </el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <dict-tag :options="dict.type.alarm_level" :value="detailData.alarmLevel" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.alarm_status" :value="detailData.alarmStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="告警时间">{{ parseTime(detailData.alarmTime) }}</el-descriptions-item>
        <el-descriptions-item label="阈值">{{ detailData.thresholdValue }}%</el-descriptions-item>
        <el-descriptions-item label="告警值">{{ detailData.alarmValue }}%</el-descriptions-item>
        <el-descriptions-item label="告警消息" :span="2">{{ detailData.alarmMessage }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlePerson }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ parseTime(detailData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ detailData.handleResult }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarm, handleAlarm, ignoreAlarm, getAlarm } from "@/api/monitor/alarm"

export default {
  name: "MonitorAlarm",
  dicts: ['alarm_type', 'alarm_level', 'alarm_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      statusTab: "all",
      alarmList: [],
      handleOpen: false,
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serverIp: null,
        alarmType: null,
        alarmLevel: null,
        alarmStatus: null
      },
      handleForm: {},
      handleRules: {
        handleResult: [{ required: true, message: "处理结果不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAlarm(this.queryParams).then(response => {
        this.alarmList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleStatusTab() {
      this.queryParams.alarmStatus = this.statusTab === "all" ? null : this.statusTab
      this.handleQuery()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      const alarmStatus = this.queryParams.alarmStatus
      this.resetForm("queryForm")
      this.queryParams.alarmStatus = alarmStatus
      this.getList()
    },
    canHandle(row) {
      return row.alarmStatus === '0' || row.alarmStatus === '1'
    },
    getValueClass(row) {
      if (row.alarmLevel === '3') {
        return 'value-danger'
      }
      if (row.alarmLevel === '2') {
        return 'value-warning'
      }
      return 'value-info'
    },
    handleProcess(row) {
      this.handleForm = { ...row, handleResult: "" }
      this.handleOpen = true
    },
    submitHandle() {
      this.$refs["handleForm"].validate(valid => {
        if (!valid) {
          return
        }
        handleAlarm(this.handleForm.id, { handleResult: this.handleForm.handleResult }).then(() => {
          this.$modal.msgSuccess("告警已处理")
          this.handleOpen = false
          this.getList()
        })
      })
    },
    handleIgnore(row) {
      this.$modal.confirm(`确认忽略 ${row.serverIp} 的这条告警吗？`)
        .then(() => ignoreAlarm(row.id))
        .then(() => {
          this.$modal.msgSuccess("告警已忽略")
          this.getList()
        }).catch(() => {})
    },
    handleDetail(row) {
      getAlarm(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    }
  }
}
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
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

.status-tabs {
  margin-bottom: 8px;
}

.query-bar {
  padding: 14px 16px 2px;
  margin-bottom: 12px;
  background: #f8fafc;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.value-danger {
  color: #f56c6c;
  font-weight: 700;
}

.value-warning {
  color: #e6a23c;
  font-weight: 700;
}

.value-info {
  color: #409eff;
  font-weight: 700;
}

.mb16 {
  margin-bottom: 16px;
}
</style>
