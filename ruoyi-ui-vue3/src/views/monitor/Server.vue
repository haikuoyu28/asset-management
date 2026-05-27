<template>
  <section class="module-page">
    <div class="module-hero server">
      <div>
        <span class="eyebrow">Monitor Center</span>
        <h1>服务器管理</h1>
        <p>维护资产关联、Agent 接入状态、连接状态和最近采集时间，为后续智能诊断提供可信对象。</p>
      </div>
      <el-button type="primary" @click="openForm()">新增服务器</el-button>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.serverIp" clearable placeholder="服务器 IP" />
        <el-input v-model="query.hostname" clearable placeholder="主机名" />
        <el-select v-model="query.connectionStatus" clearable placeholder="连接状态">
          <el-option v-for="item in connectionStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="query.monitorStatus" clearable placeholder="监控状态">
          <el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="服务器 IP" min-width="140" prop="serverIp" />
        <el-table-column label="主机名" min-width="150" prop="hostname" />
        <el-table-column label="关联资产" min-width="180">
          <template #default="{ row }">{{ row.assetCode ? `${row.assetCode} / ${row.assetName || '-'}` : '未关联' }}</template>
        </el-table-column>
        <el-table-column label="规格" min-width="150">
          <template #default="{ row }">{{ row.cpuCores || 0 }}C / {{ row.memoryGb || 0 }}GB / {{ row.diskGb || 0 }}GB</template>
        </el-table-column>
        <el-table-column label="连接" width="96">
          <template #default="{ row }"><el-tag :type="optionType(connectionStatusOptions, row.connectionStatus)">{{ optionLabel(connectionStatusOptions, row.connectionStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="监控" width="96">
          <template #default="{ row }"><el-tag :type="optionType(monitorStatusOptions, row.monitorStatus)">{{ optionLabel(monitorStatusOptions, row.monitorStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="Agent" width="96">
          <template #default="{ row }"><el-tag :type="row.agentEnabled === '1' ? 'success' : 'info'">{{ row.agentEnabled === '1' ? '启用' : '停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="最近采集" min-width="170" prop="lastCollectTime" />
        <el-table-column fixed="right" label="操作" width="250">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="success" @click="resetToken(row)">重置 Token</el-button>
            <el-button link type="danger" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>

    <el-dialog v-model="formVisible" :title="form.id ? '编辑服务器' : '新增服务器'" width="760px">
      <el-form :model="form" label-width="108px">
        <div class="form-grid">
          <el-form-item label="服务器 IP"><el-input v-model="form.serverIp" /></el-form-item>
          <el-form-item label="主机名"><el-input v-model="form.hostname" /></el-form-item>
          <el-form-item label="资产 ID"><el-input-number v-model="form.assetId" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="系统名称"><el-input v-model="form.osName" /></el-form-item>
          <el-form-item label="CPU 核数"><el-input-number v-model="form.cpuCores" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="内存 GB"><el-input-number v-model="form.memoryGb" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="磁盘 GB"><el-input-number v-model="form.diskGb" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="SSH 端口"><el-input-number v-model="form.sshPort" :min="1" controls-position="right" /></el-form-item>
          <el-form-item label="监控状态">
            <el-select v-model="form.monitorStatus"><el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="连接状态">
            <el-select v-model="form.connectionStatus"><el-option v-for="item in connectionStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="Agent">
            <el-switch v-model="form.agentEnabled" active-value="1" inactive-value="0" />
          </el-form-item>
          <el-form-item label="Agent 路径"><el-input v-model="form.agentPath" /></el-form-item>
        </div>
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
import { listServer, addServer, updateServer, resetAgentToken, delServer } from '@/api/monitor/server'
import { connectionStatusOptions, monitorStatusOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const formVisible = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, serverIp: '', hostname: '', connectionStatus: '', monitorStatus: '' })
const form = reactive(defaultForm())

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listServer(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, serverIp: '', hostname: '', connectionStatus: '', monitorStatus: '' })
  loadList()
}

function openForm(row) {
  Object.assign(form, defaultForm(), row || {})
  formVisible.value = true
}

async function submitForm() {
  if (form.id) {
    await updateServer(form)
  } else {
    await addServer(form)
  }
  ElMessage.success('服务器已保存')
  formVisible.value = false
  loadList()
}

async function resetToken(row) {
  await ElMessageBox.confirm(`确认重置 ${row.serverIp} 的 Agent Token？`, '重置 Token')
  const response = await resetAgentToken(row.id)
  ElMessage.success(response.msg || 'Agent Token 已重置')
  loadList()
}

async function removeRow(row) {
  await ElMessageBox.confirm(`确认删除服务器 ${row.serverIp}？`, '删除确认', { type: 'warning' })
  await delServer(row.id)
  ElMessage.success('服务器已删除')
  loadList()
}

function defaultForm() {
  return {
    id: undefined,
    assetId: undefined,
    serverIp: '',
    hostname: '',
    osName: '',
    cpuCores: 0,
    memoryGb: 0,
    diskGb: 0,
    monitorStatus: '2',
    connectionStatus: '1',
    sshPort: 22,
    agentPath: '/opt/asset-agent',
    agentEnabled: '1',
    remark: ''
  }
}
</script>
