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
      <el-form-item label="告警类型" prop="alarmType">
        <el-select v-model="queryParams.alarmType" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.alarm_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="告警级别" prop="alarmLevel">
        <el-select v-model="queryParams.alarmLevel" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.alarm_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="告警状态" prop="alarmStatus">
        <el-select v-model="queryParams.alarmStatus" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.alarm_status"
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
          type="danger"
          plain
          icon="el-icon-warning"
          size="mini"
          :disabled="single"
          @click="handleBatchHandle"
          v-hasPermi="['monitor:alarm:edit']"
        >批量处理</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="服务器IP" align="center" prop="serverIp" width="130" />
      <el-table-column label="主机名" align="center" prop="hostname" :show-overflow-tooltip="true" />
      <el-table-column label="告警类型" align="center" prop="alarmType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_type" :value="scope.row.alarmType" />
        </template>
      </el-table-column>
      <el-table-column label="告警级别" align="center" prop="alarmLevel" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.alarmLevel" />
        </template>
      </el-table-column>
      <el-table-column label="告警状态" align="center" prop="alarmStatus" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_status" :value="scope.row.alarmStatus" />
        </template>
      </el-table-column>
      <el-table-column label="告警值" align="center" prop="alarmValue" width="80">
        <template slot-scope="scope">
          <span :class="getValueClass(scope.row)">{{ scope.row.alarmValue }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="告警消息" align="center" prop="alarmMessage" :show-overflow-tooltip="true" />
      <el-table-column label="告警时间" align="center" prop="alarmTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handlePerson" width="100" />
      <el-table-column label="处理时间" align="center" prop="handleTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.handleTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <template v-if="scope.row.alarmStatus === '0' || scope.row.alarmStatus === '1'">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleProcess(scope.row)"
              v-hasPermi="['monitor:alarm:edit']"
            >处理</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-close"
              @click="handleIgnore(scope.row)"
              v-hasPermi="['monitor:alarm:edit']"
            >忽略</el-button>
          </template>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['monitor:alarm:query']"
          >详情</el-button>
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

    <!-- 处理对话框 -->
    <el-dialog title="处理告警" :visible.sync="handleOpen" width="600px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="服务器IP">
          {{ handleForm.serverIp }}
        </el-form-item>
        <el-form-item label="告警类型">
          <dict-tag :options="dict.type.alarm_type" :value="handleForm.alarmType" />
        </el-form-item>
        <el-form-item label="告警消息">
          {{ handleForm.alarmMessage }}
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitHandle">确 定</el-button>
        <el-button @click="handleOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="告警详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务器IP">{{ detailData.serverIp }}</el-descriptions-item>
        <el-descriptions-item label="主机名">{{ detailData.hostname }}</el-descriptions-item>
        <el-descriptions-item label="告警类型">
          <dict-tag :options="dict.type.alarm_type" :value="detailData.alarmType" />
        </el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <dict-tag :options="dict.type.alarm_level" :value="detailData.alarmLevel" />
        </el-descriptions-item>
        <el-descriptions-item label="告警状态">
          <dict-tag :options="dict.type.alarm_status" :value="detailData.alarmStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="告警阈值">{{ detailData.thresholdValue }}%</el-descriptions-item>
        <el-descriptions-item label="告警值">{{ detailData.alarmValue }}%</el-descriptions-item>
        <el-descriptions-item label="告警时间">{{ parseTime(detailData.alarmTime) }}</el-descriptions-item>
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
      single: true,
      multiple: true,
      alarmList: [],
      ids: [],
      title: "",
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
        this.alarmList = response.rows
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    getValueClass(row) {
      if (row.alarmLevel === '3') return 'text-danger'
      if (row.alarmLevel === '2') return 'text-warning'
      return 'text-info'
    },
    handleProcess(row) {
      this.handleForm = {
        id: row.id,
        serverIp: row.serverIp,
        alarmType: row.alarmType,
        alarmMessage: row.alarmMessage,
        handleResult: ''
      }
      this.handleOpen = true
    },
    submitHandle() {
      this.$refs["handleForm"].validate(valid => {
        if (valid) {
          handleAlarm(this.handleForm.id, this.handleForm).then(response => {
            this.$modal.msgSuccess("处理成功")
            this.handleOpen = false
            this.getList()
          })
        }
      })
    },
    handleIgnore(row) {
      this.$modal.confirm('确认忽略此告警？')
        .then(() => {
          return ignoreAlarm(row.id)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess("忽略成功")
        }).catch(() => {})
    },
    handleBatchHandle() {
      this.$modal.confirm('确认批量处理选中的告警？')
        .then(() => {
          const promises = this.ids.map(id => {
            return ignoreAlarm(id)
          })
          return Promise.all(promises)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess("批量处理成功")
        }).catch(() => {})
    },
    handleDetail(row) {
      getAlarm(row.id).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    }
  }
}
</script>

<style scoped>
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
.text-warning {
  color: #e6a23c;
  font-weight: bold;
}
.text-info {
  color: #909399;
}
</style>
