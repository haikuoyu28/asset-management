<template>
  <div class="app-container asset-page">
    <div class="page-head">
      <div>
        <div class="page-title">设备资产</div>
        <div class="page-subtitle">维护设备台账，并将服务器类资产纳入监控中心。</div>
      </div>
      <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['asset:info:add']">
        新增资产
      </el-button>
    </div>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" class="query-bar">
      <el-form-item label="设备编号" prop="assetCode">
        <el-input v-model="queryParams.assetCode" placeholder="输入设备编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select v-model="queryParams.assetType" placeholder="全部类型" clearable>
          <el-option v-for="dict in dict.type.asset_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
          <el-option v-for="dict in dict.type.asset_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['asset:info:export']">
          导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="assetList" class="business-table">
      <el-table-column label="设备编号" align="center" prop="assetCode" width="130" />
      <el-table-column label="设备名称" align="left" prop="assetName" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_type" :value="scope.row.assetType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="责任人" align="center" prop="responsiblePerson" width="110" />
      <el-table-column label="位置" align="left" prop="location" min-width="130" :show-overflow-tooltip="true" />
      <el-table-column label="IP地址" align="center" prop="ipAddress" width="130" />
      <el-table-column label="型号" align="left" prop="model" min-width="130" :show-overflow-tooltip="true" />
      <el-table-column label="采购日期" align="center" prop="purchaseDate" width="110" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)" v-hasPermi="['asset:info:query']">
            详情
          </el-button>
          <el-button
            v-if="canEnrollMonitor(scope.row)"
            size="mini"
            type="text"
            icon="el-icon-connection"
            @click="handleEnrollMonitor(scope.row)"
            v-hasPermi="['monitor:server:add']"
          >
            纳入监控
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['asset:info:edit']">
            编辑
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['asset:info:remove']">
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

    <el-dialog :title="title" :visible.sync="open" width="860px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="例如 SRV-2026-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="例如 生产数据库服务器" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="form.assetType" placeholder="选择资产类型" style="width: 100%">
                <el-option v-for="dict in dict.type.asset_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.asset_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="设备型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="form.serialNumber" placeholder="设备序列号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="责任人" prop="responsiblePerson">
              <el-input v-model="form.responsiblePerson" placeholder="责任人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptName">
              <el-input v-model="form.deptName" placeholder="例如 运维部" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="form.location" placeholder="机房/楼层/机柜" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model="form.ipAddress" placeholder="例如 192.168.1.10" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="MAC地址" prop="macAddress">
              <el-input v-model="form.macAddress" placeholder="MAC地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker v-model="form.purchaseDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="form.specification" placeholder="规格描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配置" prop="configuration">
          <el-input v-model="form.configuration" type="textarea" :rows="2" placeholder="CPU、内存、磁盘、网络等配置" />
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

    <el-dialog title="纳入监控" :visible.sync="monitorOpen" width="620px" append-to-body>
      <el-form ref="monitorForm" :model="monitorForm" :rules="monitorRules" label-width="110px">
        <el-alert type="info" :closable="false" show-icon class="mb16">
          <template slot="title">
            将服务器资产创建为监控对象。当前版本使用 Agent/API 地址纳管，不保存 SSH 密码。
          </template>
        </el-alert>
        <el-form-item label="关联资产">
          <el-input :value="monitorAssetLabel" disabled />
        </el-form-item>
        <el-form-item label="服务器IP" prop="serverIp">
          <el-input v-model="monitorForm.serverIp" placeholder="服务器 IP" />
        </el-form-item>
        <el-form-item label="主机名" prop="hostname">
          <el-input v-model="monitorForm.hostname" placeholder="主机名" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="CPU核数" prop="cpuCores">
              <el-input-number v-model="monitorForm.cpuCores" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="内存GB" prop="memoryGb">
              <el-input-number v-model="monitorForm.memoryGb" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="磁盘GB" prop="diskGb">
              <el-input-number v-model="monitorForm.diskGb" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Agent地址" prop="agentPath">
          <el-input v-model="monitorForm.agentPath" placeholder="例如 http://192.168.1.10:9090/monitor/data" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="monitorForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMonitor">纳入监控</el-button>
        <el-button @click="monitorOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="设备资产详情" :visible.sync="detailOpen" width="860px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设备编号">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">
          <dict-tag :options="dict.type.asset_type" :value="detailData.assetType" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.asset_status" :value="detailData.status" />
        </el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailData.responsiblePerson }}</el-descriptions-item>
        <el-descriptions-item label="归属部门">{{ detailData.deptName }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detailData.model }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ detailData.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detailData.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ detailData.price }}</el-descriptions-item>
        <el-descriptions-item label="配置" :span="2">{{ detailData.configuration }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listAssetInfo, getAssetInfo, addAssetInfo, updateAssetInfo, delAssetInfo } from "@/api/asset/info"
import { addServer } from "@/api/monitor/server"

export default {
  name: "AssetInfo",
  dicts: ['asset_type', 'asset_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      assetList: [],
      title: "",
      open: false,
      detailOpen: false,
      monitorOpen: false,
      detailData: {},
      selectedAsset: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        assetCode: null,
        assetName: null,
        assetType: null,
        status: null
      },
      form: {},
      monitorForm: {},
      rules: {
        assetCode: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
        assetName: [{ required: true, message: "设备名称不能为空", trigger: "blur" }],
        assetType: [{ required: true, message: "资产类型不能为空", trigger: "change" }],
        status: [{ required: true, message: "状态不能为空", trigger: "change" }]
      },
      monitorRules: {
        serverIp: [{ required: true, message: "服务器IP不能为空", trigger: "blur" }],
        hostname: [{ required: true, message: "主机名不能为空", trigger: "blur" }]
      }
    }
  },
  computed: {
    monitorAssetLabel() {
      if (!this.selectedAsset.id) {
        return ""
      }
      return `${this.selectedAsset.assetCode || "-"} / ${this.selectedAsset.assetName || "-"}`
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAssetInfo(this.queryParams).then(response => {
        this.assetList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    canEnrollMonitor(row) {
      return row.assetType === '1' && row.status !== '3'
    },
    reset() {
      this.form = {
        id: null,
        assetCode: null,
        assetName: null,
        model: null,
        specification: null,
        configuration: null,
        serialNumber: null,
        assetType: null,
        status: "0",
        deptId: null,
        deptName: null,
        responsiblePerson: null,
        purchaseDate: null,
        price: null,
        imageUrl: null,
        location: null,
        ipAddress: null,
        macAddress: null,
        remark: null
      }
      this.resetForm("form")
    },
    resetMonitorForm(asset) {
      this.selectedAsset = asset || {}
      this.monitorForm = {
        assetId: asset.id,
        serverIp: asset.ipAddress,
        hostname: asset.assetName,
        osName: asset.model,
        cpuCores: null,
        memoryGb: null,
        diskGb: null,
        monitorStatus: "2",
        connectionStatus: "1",
        sshPort: 22,
        sshUsername: null,
        sshPassword: null,
        agentPath: asset.ipAddress ? `http://${asset.ipAddress}:9090/monitor/data` : null,
        remark: asset.location ? `资产位置：${asset.location}` : null
      }
      this.resetForm("monitorForm")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增设备资产"
    },
    handleUpdate(row) {
      this.reset()
      getAssetInfo(row.id).then(response => {
        this.form = response.data || {}
        this.open = true
        this.title = "编辑设备资产"
      })
    },
    handleDetail(row) {
      getAssetInfo(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    handleEnrollMonitor(row) {
      if (!row.ipAddress) {
        this.$modal.msgWarning("该服务器资产缺少 IP 地址，补充后再纳入监控")
        return
      }
      this.resetMonitorForm(row)
      this.monitorOpen = true
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return
        }
        const action = this.form.id ? updateAssetInfo(this.form) : addAssetInfo(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.form.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    submitMonitor() {
      this.$refs["monitorForm"].validate(valid => {
        if (!valid) {
          return
        }
        const payload = { ...this.monitorForm, sshPassword: null }
        addServer(payload).then(() => {
          this.$modal.msgSuccess("已纳入监控中心")
          this.monitorOpen = false
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除设备资产 "${row.assetName}" 吗？`)
        .then(() => delAssetInfo(row.id))
        .then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        }).catch(() => {})
    },
    handleExport() {
      this.download('/asset/info/export', {
        ...this.queryParams
      }, `asset_info_${new Date().getTime()}.xlsx`)
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

.business-table {
  border-radius: 6px;
}

.mb16 {
  margin-bottom: 16px;
}
</style>
