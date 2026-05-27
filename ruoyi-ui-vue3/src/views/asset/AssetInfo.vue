<template>
  <section class="module-page">
    <div class="module-hero asset">
      <div>
        <span class="eyebrow">Asset Center</span>
        <h1>设备资产</h1>
        <p>先把服务器、网络、安全和终端资产统一入库，再把服务器类资产纳入监控链路。</p>
      </div>
      <el-button type="primary" @click="openForm()">新增资产</el-button>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.assetName" clearable placeholder="资产名称" />
        <el-input v-model="query.assetCode" clearable placeholder="资产编号" />
        <el-input v-model="query.ipAddress" clearable placeholder="IP 地址" />
        <el-select v-model="query.assetType" clearable placeholder="资产类型">
          <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="资产编号" min-width="130" prop="assetCode" />
        <el-table-column label="资产名称" min-width="160" prop="assetName" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">{{ optionLabel(assetTypeOptions, row.assetType) }}</template>
        </el-table-column>
        <el-table-column label="IP 地址" min-width="130" prop="ipAddress" />
        <el-table-column label="负责人" min-width="120" prop="responsiblePerson" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="optionType(assetStatusOptions, row.status)">{{ optionLabel(assetStatusOptions, row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="位置" min-width="150" prop="location" />
        <el-table-column fixed="right" label="操作" width="250">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button :disabled="row.assetType !== '1'" link type="success" @click="includeMonitor(row)">纳入监控</el-button>
            <el-button link type="danger" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>

    <el-dialog v-model="formVisible" :title="form.id ? '编辑资产' : '新增资产'" width="720px">
      <el-form :model="form" label-width="96px">
        <div class="form-grid">
          <el-form-item label="资产编号"><el-input v-model="form.assetCode" /></el-form-item>
          <el-form-item label="资产名称"><el-input v-model="form.assetName" /></el-form-item>
          <el-form-item label="资产类型">
            <el-select v-model="form.assetType">
              <el-option v-for="item in assetTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option v-for="item in assetStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="IP 地址"><el-input v-model="form.ipAddress" /></el-form-item>
          <el-form-item label="负责人"><el-input v-model="form.responsiblePerson" /></el-form-item>
          <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
          <el-form-item label="位置"><el-input v-model="form.location" /></el-form-item>
        </div>
        <el-form-item label="配置说明"><el-input v-model="form.configuration" :rows="3" type="textarea" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" :rows="3" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAssetInfo, addAssetInfo, updateAssetInfo, delAssetInfo } from '@/api/asset/info'
import { addServer } from '@/api/monitor/server'
import { assetStatusOptions, assetTypeOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const formVisible = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, assetName: '', assetCode: '', ipAddress: '', assetType: '' })
const form = reactive(defaultForm())

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listAssetInfo(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, assetName: '', assetCode: '', ipAddress: '', assetType: '' })
  loadList()
}

function openForm(row) {
  Object.assign(form, defaultForm(), row || {})
  formVisible.value = true
}

async function submitForm() {
  if (form.id) {
    await updateAssetInfo(form)
  } else {
    await addAssetInfo(form)
  }
  ElMessage.success('资产已保存')
  formVisible.value = false
  loadList()
}

async function includeMonitor(row) {
  await ElMessageBox.confirm(`确认将 ${row.assetName || row.assetCode} 纳入监控？`, '纳入监控')
  await addServer({
    assetId: row.id,
    serverIp: row.ipAddress,
    hostname: row.assetName,
    osName: row.model,
    monitorStatus: '2',
    connectionStatus: '1',
    agentEnabled: '1',
    agentPath: '/opt/asset-agent',
    remark: `由资产 ${row.assetCode || row.id} 纳入监控`
  })
  ElMessage.success('已创建服务器监控对象')
}

async function removeRow(row) {
  await ElMessageBox.confirm(`确认删除资产 ${row.assetName || row.assetCode}？`, '删除确认', { type: 'warning' })
  await delAssetInfo(row.id)
  ElMessage.success('资产已删除')
  loadList()
}

function defaultForm() {
  return {
    id: undefined,
    assetCode: '',
    assetName: '',
    assetType: '1',
    status: '0',
    ipAddress: '',
    responsiblePerson: '',
    model: '',
    location: '',
    configuration: '',
    remark: ''
  }
}
</script>
