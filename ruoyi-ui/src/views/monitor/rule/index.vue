<template>
  <div class="app-container rule-page">
    <div class="page-head">
      <div>
        <div class="page-title">告警规则</div>
        <div class="page-subtitle">定义 CPU、内存、磁盘等基础阈值，监控数据上报后自动触发告警事件。</div>
      </div>
      <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['monitor:rule:add']">
        新增规则
      </el-button>
    </div>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="query-bar">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="输入规则名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="指标类型" prop="alarmType">
        <el-select v-model="queryParams.alarmType" placeholder="全部" clearable>
          <el-option v-for="dict in dict.type.alarm_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="全部" clearable>
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
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

    <el-table v-loading="loading" :data="ruleList" class="business-table">
      <el-table-column label="规则名称" align="left" prop="ruleName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="指标" align="center" prop="alarmType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_type" :value="scope.row.alarmType" />
        </template>
      </el-table-column>
      <el-table-column label="级别" align="center" prop="alarmLevel" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.alarmLevel" />
        </template>
      </el-table-column>
      <el-table-column label="条件" align="center" width="120">
        <template slot-scope="scope">
          <strong>{{ scope.row.compareOperator || '>=' }} {{ scope.row.thresholdValue }}%</strong>
        </template>
      </el-table-column>
      <el-table-column label="作用范围" align="left" min-width="160" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.scopeType === '1'">{{ scope.row.serverIp }} / {{ scope.row.hostname }}</span>
          <el-tag v-else size="mini" type="success">全部服务器</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="静默" align="center" width="90">
        <template slot-scope="scope">{{ scope.row.silentMinutes || 0 }} 分钟</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="enabled" width="90">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.enabled === '0' ? 'success' : 'info'">
            {{ scope.row.enabled === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="170" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:rule:edit']">
            编辑
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:rule:remove']">
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

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="例如：CPU 使用率过高" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="指标类型" prop="alarmType">
              <el-select v-model="form.alarmType" placeholder="请选择" style="width: 100%">
                <el-option v-for="dict in metricOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="告警级别" prop="alarmLevel">
              <el-select v-model="form.alarmLevel" placeholder="请选择" style="width: 100%">
                <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="启用状态" prop="enabled">
              <el-switch v-model="form.enabled" active-value="0" inactive-value="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="比较符" prop="compareOperator">
              <el-select v-model="form.compareOperator" style="width: 100%">
                <el-option label=">=" value=">=" />
                <el-option label=">" value=">" />
                <el-option label="<=" value="<=" />
                <el-option label="<" value="<" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="阈值" prop="thresholdValue">
              <el-input-number v-model="form.thresholdValue" :min="0" :max="100" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="静默分钟" prop="silentMinutes">
              <el-input-number v-model="form.silentMinutes" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="作用范围" prop="scopeType">
          <el-radio-group v-model="form.scopeType">
            <el-radio label="0">全部服务器</el-radio>
            <el-radio label="1">指定服务器</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.scopeType === '1'" label="服务器" prop="serverId">
          <el-select v-model="form.serverId" filterable placeholder="选择服务器" style="width: 100%">
            <el-option
              v-for="server in serverOptions"
              :key="server.id"
              :label="formatServerOption(server)"
              :value="server.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="补充说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRule, getRule, addRule, updateRule, delRule } from '@/api/monitor/rule'
import { listServer } from '@/api/monitor/server'

export default {
  name: 'MonitorRule',
  dicts: ['alarm_type', 'alarm_level'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      ruleList: [],
      serverOptions: [],
      open: false,
      title: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleName: null,
        alarmType: null,
        enabled: null
      },
      form: {},
      rules: {
        ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
        alarmType: [{ required: true, message: '指标类型不能为空', trigger: 'change' }],
        alarmLevel: [{ required: true, message: '告警级别不能为空', trigger: 'change' }],
        thresholdValue: [{ required: true, message: '阈值不能为空', trigger: 'blur' }],
        serverId: [{ required: true, message: '指定服务器不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    metricOptions() {
      return (this.dict.type.alarm_type || []).filter(item => ['1', '2', '3'].includes(item.value))
    }
  },
  created() {
    this.getList()
    this.getServerOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listRule(this.queryParams).then(response => {
        this.ruleList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getServerOptions() {
      listServer({ pageNum: 1, pageSize: 200 }).then(response => {
        this.serverOptions = response.rows || []
      })
    },
    formatServerOption(server) {
      return `${server.serverIp || '-'} / ${server.hostname || '-'}`
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    reset() {
      this.form = {
        id: null,
        ruleName: null,
        alarmType: '1',
        alarmLevel: '2',
        compareOperator: '>=',
        thresholdValue: 85,
        scopeType: '0',
        serverId: null,
        silentMinutes: 30,
        enabled: '0',
        remark: null
      }
      this.resetForm('form')
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增告警规则'
    },
    handleUpdate(row) {
      this.reset()
      getRule(row.id).then(response => {
        this.form = response.data || {}
        this.open = true
        this.title = '编辑告警规则'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const payload = { ...this.form }
        if (payload.scopeType !== '1') {
          payload.serverId = null
        }
        const action = payload.id ? updateRule(payload) : addRule(payload)
        action.then(() => {
          this.$modal.msgSuccess(payload.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除规则 "${row.ruleName}" 吗？`)
        .then(() => delRule(row.id))
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
</style>
