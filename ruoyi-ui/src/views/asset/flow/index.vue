<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="资产编号" prop="assetCode">
        <el-input
          v-model="queryParams.assetCode"
          placeholder="请输入资产编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程类型" prop="flowType">
        <el-select v-model="queryParams.flowType" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.asset_flow_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人" prop="operator">
        <el-input
          v-model="queryParams.operator"
          placeholder="请输入操作人"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['asset:flow:add']"
        >新增流程</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="flowList">
      <el-table-column label="资产编号" align="center" prop="assetCode" width="120" />
      <el-table-column label="资产名称" align="center" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="流程类型" align="center" prop="flowType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_flow_type" :value="scope.row.flowType" />
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="operator" width="100" />
      <el-table-column label="操作时间" align="center" prop="operateTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.operateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变更前状态" align="center" prop="beforeStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_status" :value="scope.row.beforeStatus" />
        </template>
      </el-table-column>
      <el-table-column label="变更后状态" align="center" prop="afterStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_status" :value="scope.row.afterStatus" />
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['asset:flow:query']"
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

    <!-- 添加流程对话框 -->
    <el-dialog title="新增资产流程" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="请选择资产" filterable @change="onAssetChange">
            <el-option
              v-for="asset in assetOptions"
              :key="asset.id"
              :label="asset.assetCode + ' - ' + asset.assetName"
              :value="asset.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="流程类型" prop="flowType">
          <el-select v-model="form.flowType" placeholder="请选择流程类型">
            <el-option
              v-for="dict in dict.type.asset_flow_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="变更后状态" prop="afterStatus">
          <el-select v-model="form.afterStatus" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.asset_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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

    <!-- 详情对话框 -->
    <el-dialog title="流程详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编号">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="流程类型">
          <dict-tag :options="dict.type.asset_flow_type" :value="detailData.flowType" />
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ parseTime(detailData.operateTime) }}</el-descriptions-item>
        <el-descriptions-item label="变更前状态">
          <dict-tag :options="dict.type.asset_status" :value="detailData.beforeStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="变更后状态">
          <dict-tag :options="dict.type.asset_status" :value="detailData.afterStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listAssetFlow, addAssetFlow, getAssetFlow } from "@/api/asset/flow"
import { listAssetInfo } from "@/api/asset/info"

export default {
  name: "AssetFlow",
  dicts: ['asset_flow_type', 'asset_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      flowList: [],
      assetOptions: [],
      title: "",
      open: false,
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        assetCode: null,
        flowType: null,
        operator: null
      },
      form: {},
      rules: {
        assetId: [{ required: true, message: "请选择资产", trigger: "change" }],
        flowType: [{ required: true, message: "请选择流程类型", trigger: "change" }]
      }
    }
  },
  created() {
    this.getList()
    this.getAssetList()
  },
  methods: {
    getList() {
      this.loading = true
      listAssetFlow(this.queryParams).then(response => {
        this.flowList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getAssetList() {
      listAssetInfo({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.assetOptions = response.rows
      })
    },
    onAssetChange(assetId) {
      const asset = this.assetOptions.find(a => a.id === assetId)
      if (asset) {
        this.form.afterStatus = asset.status
      }
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
        assetId: null,
        flowType: null,
        afterStatus: null,
        description: null,
        remark: null
      }
      this.resetForm("form")
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增资产流程"
    },
    handleDetail(row) {
      this.detailData = row
      this.detailOpen = true
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addAssetFlow(this.form).then(response => {
            this.$modal.msgSuccess("新增成功")
            this.open = false
            this.getList()
          })
        }
      })
    }
  }
}
</script>
