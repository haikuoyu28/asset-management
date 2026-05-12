<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="设备编号" prop="assetCode">
        <el-input
          v-model="queryParams.assetCode"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="assetName">
        <el-input
          v-model="queryParams.assetName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select v-model="queryParams.assetType" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.asset_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option
            v-for="dict in dict.type.asset_status"
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
          v-hasPermi="['asset:info:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['asset:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="assetList">
      <el-table-column label="设备编号" align="center" prop="assetCode" width="120" />
      <el-table-column label="设备名称" align="center" prop="assetName" :show-overflow-tooltip="true" />
      <el-table-column label="型号" align="center" prop="model" :show-overflow-tooltip="true" />
      <el-table-column label="资产类型" align="center" prop="assetType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_type" :value="scope.row.assetType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.asset_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="归属部门" align="center" prop="deptName" width="120" />
      <el-table-column label="责任人" align="center" prop="responsiblePerson" width="100" />
      <el-table-column label="存放位置" align="center" prop="location" :show-overflow-tooltip="true" />
      <el-table-column label="IP地址" align="center" prop="ipAddress" width="120" />
      <el-table-column label="采购日期" align="center" prop="purchaseDate" width="110" />
      <el-table-column label="价格" align="center" prop="price" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.price">¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['asset:info:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['asset:info:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['asset:info:remove']"
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

    <!-- 添加或修改设备资产对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备编号" prop="assetCode">
              <el-input v-model="form.assetCode" placeholder="请输入设备编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="form.specification" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="form.serialNumber" placeholder="请输入序列号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="form.assetType" placeholder="请选择">
                <el-option
                  v-for="dict in dict.type.asset_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :normalizer="normalizer" placeholder="选择部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人" prop="responsiblePerson">
              <el-input v-model="form.responsiblePerson" placeholder="请输入责任人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="存放位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model="form.ipAddress" placeholder="请输入IP地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择日期"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :precision="2" :min="0" placeholder="请输入价格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="MAC地址" prop="macAddress">
              <el-input v-model="form.macAddress" placeholder="请输入MAC地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.asset_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配置信息" prop="configuration">
          <el-input v-model="form.configuration" type="textarea" :rows="2" placeholder="请输入配置信息" />
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

    <!-- 设备资产详情对话框 -->
    <el-dialog title="设备资产详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设备编号">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detailData.model }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ detailData.specification }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ detailData.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">
          <dict-tag :options="dict.type.asset_type" :value="detailData.assetType" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.asset_status" :value="detailData.status" />
        </el-descriptions-item>
        <el-descriptions-item label="归属部门">{{ detailData.deptName }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailData.responsiblePerson }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="MAC地址">{{ detailData.macAddress }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detailData.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ detailData.price }}</el-descriptions-item>
        <el-descriptions-item label="配置信息" :span="2">{{ detailData.configuration }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailData.createBy }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listAssetInfo, getAssetInfo, addAssetInfo, updateAssetInfo, delAssetInfo } from "@/api/asset/info"
import { listDept } from "@/api/system/dept"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "AssetInfo",
  dicts: ['asset_type', 'asset_status'],
  components: { Treeselect },
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      assetList: [],
      title: "",
      open: false,
      detailOpen: false,
      deptOptions: [],
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        assetCode: null,
        assetName: null,
        assetType: null,
        status: null
      },
      form: {},
      rules: {
        assetCode: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
        assetName: [{ required: true, message: "设备名称不能为空", trigger: "blur" }],
        assetType: [{ required: true, message: "资产类型不能为空", trigger: "change" }],
        status: [{ required: true, message: "状态不能为空", trigger: "change" }]
      }
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
  },
  methods: {
    getList() {
      this.loading = true
      listAssetInfo(this.queryParams).then(response => {
        this.assetList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getDeptTree() {
      listDept().then(response => {
        this.deptOptions = this.handleTree(response.data, "deptId")
      })
    },
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      }
    },
    cancel() {
      this.open = false
      this.reset()
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
      this.title = "添加设备资产"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAssetInfo(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改设备资产"
      })
    },
    handleDetail(row) {
      getAssetInfo(row.id).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.deptId) {
            const dept = this.findDeptName(this.deptOptions, this.form.deptId)
            this.form.deptName = dept
          }
          if (this.form.id) {
            updateAssetInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAssetInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    findDeptName(nodes, id) {
      for (let node of nodes) {
        if (node.deptId === id) {
          return node.deptName
        }
        if (node.children) {
          const found = this.findDeptName(node.children, id)
          if (found) return found
        }
      }
      return null
    },
    handleDelete(row) {
      this.$modal.confirm('是否确认删除设备名称为"' + row.assetName + '"的数据项？')
        .then(() => {
          return delAssetInfo(row.id)
        }).then(() => {
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
