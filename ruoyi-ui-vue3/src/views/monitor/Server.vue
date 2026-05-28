<template>
  <section class="module-page">
    <div class="module-hero server">
      <div>
        <span class="eyebrow">Monitor Center</span>
        <h1>服务器管理</h1>
        <p>通过 SSH 纳管 Linux 服务器，主动采集 CPU、内存、磁盘和负载数据，并支持连接测试、即时采集与预设远程命令。</p>
      </div>
      <el-button v-hasPermi="['monitor:server:add']" type="primary" @click="openForm()">新增服务器</el-button>
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
        <el-table-column label="SSH" min-width="130">
          <template #default="{ row }">{{ row.sshUsername || '-' }}@{{ row.sshPort || 22 }}</template>
        </el-table-column>
        <el-table-column label="规格" min-width="150">
          <template #default="{ row }">{{ row.cpuCores || 0 }}C / {{ row.memoryGb || 0 }}GB / {{ row.diskGb || 0 }}GB</template>
        </el-table-column>
        <el-table-column label="连接" width="96">
          <template #default="{ row }">
            <el-tag :type="optionType(connectionStatusOptions, row.connectionStatus)">
              {{ optionLabel(connectionStatusOptions, row.connectionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="监控" width="96">
          <template #default="{ row }">
            <el-tag :type="optionType(monitorStatusOptions, row.monitorStatus)">
              {{ optionLabel(monitorStatusOptions, row.monitorStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最近采集" min-width="170" prop="lastCollectTime" />
        <el-table-column fixed="right" label="操作" width="310">
          <template #default="{ row }">
            <el-button v-hasPermi="['monitor:server:edit']" link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button v-hasPermi="['monitor:server:edit']" link type="success" @click="testConnection(row)">连接测试</el-button>
            <el-button v-hasPermi="['monitor:server:edit']" link type="warning" @click="collectNow(row)">立即采集</el-button>
            <el-button v-hasPermi="['monitor:server:edit']" link type="info" @click="openCommand(row)">远程命令</el-button>
            <el-button v-hasPermi="['monitor:server:remove']" link type="danger" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>

    <el-dialog v-model="formVisible" :title="form.id ? '编辑服务器' : '新增服务器'" width="780px">
      <el-form :model="form" label-width="112px">
        <div class="form-grid">
          <el-form-item label="服务器 IP"><el-input v-model="form.serverIp" /></el-form-item>
          <el-form-item label="主机名"><el-input v-model="form.hostname" /></el-form-item>
          <el-form-item label="关联资产 ID"><el-input-number v-model="form.assetId" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="系统名称"><el-input v-model="form.osName" /></el-form-item>
          <el-form-item label="CPU 核数"><el-input-number v-model="form.cpuCores" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="内存 GB"><el-input-number v-model="form.memoryGb" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="磁盘 GB"><el-input-number v-model="form.diskGb" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="SSH 端口"><el-input-number v-model="form.sshPort" :min="1" controls-position="right" /></el-form-item>
          <el-form-item label="SSH 用户名"><el-input v-model="form.sshUsername" autocomplete="off" /></el-form-item>
          <el-form-item label="SSH 密码">
            <el-input v-model="form.sshPassword" autocomplete="new-password" show-password :placeholder="form.id ? '留空则不修改密码' : '请输入 SSH 密码'" />
          </el-form-item>
          <el-form-item label="监控状态">
            <el-select v-model="form.monitorStatus">
              <el-option v-for="item in monitorStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="连接状态">
            <el-select v-model="form.connectionStatus">
              <el-option v-for="item in connectionStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-alert show-icon type="info" :closable="false" title="SSH 密码会在后端加密保存，列表、详情和导出不会返回明文密码。" />
        <el-form-item label="备注" class="mt-16"><el-input v-model="form.remark" :rows="3" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="commandVisible" :title="`远程命令 - ${activeServer?.serverIp || ''}`" width="820px">
      <div class="command-bar">
        <el-select v-model="commandKey" placeholder="选择预设命令">
          <el-option label="查看磁盘" value="disk" />
          <el-option label="查看内存" value="memory" />
          <el-option label="查看负载" value="load" />
          <el-option label="查看高负载进程" value="process" />
          <el-option label="查看监听端口" value="port" />
        </el-select>
        <el-button :loading="commandLoading" type="primary" @click="runCommand">执行</el-button>
      </div>
      <pre class="command-output">{{ commandOutput || '请选择预设命令并执行。' }}</pre>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listServer, addServer, updateServer, testSshConnection, collectServer, executeServerCommand, delServer } from '@/api/monitor/server'
import { connectionStatusOptions, monitorStatusOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const formVisible = ref(false)
const commandVisible = ref(false)
const commandLoading = ref(false)
const activeServer = ref(null)
const commandKey = ref('disk')
const commandOutput = ref('')
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
  Object.assign(form, defaultForm(), row || {}, { sshPassword: '' })
  formVisible.value = true
}

async function submitForm() {
  if (!form.serverIp) {
    ElMessage.warning('请填写服务器 IP')
    return
  }
  if (!form.sshUsername) {
    ElMessage.warning('请填写 SSH 用户名')
    return
  }
  if (!form.id && !form.sshPassword) {
    ElMessage.warning('新增服务器需要填写 SSH 密码')
    return
  }
  if (form.id) {
    await updateServer(form)
  } else {
    await addServer(form)
  }
  ElMessage.success('服务器已保存')
  formVisible.value = false
  loadList()
}

async function testConnection(row) {
  await testSshConnection(row.id)
  ElMessage.success('SSH 连接成功')
  loadList()
}

async function collectNow(row) {
  await collectServer(row.id)
  ElMessage.success('采集完成，监控数据已入库')
  loadList()
}

function openCommand(row) {
  activeServer.value = row
  commandKey.value = 'disk'
  commandOutput.value = ''
  commandVisible.value = true
}

async function runCommand() {
  if (!activeServer.value) return
  commandLoading.value = true
  try {
    const response = await executeServerCommand(activeServer.value.id, commandKey.value)
    commandOutput.value = response.data?.output || response.output || '命令执行完成，但没有输出。'
  } finally {
    commandLoading.value = false
  }
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
    sshUsername: '',
    sshPassword: '',
    remark: ''
  }
}
</script>

<style scoped>
.mt-16 {
  margin-top: 16px;
}

.command-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}

.command-output {
  min-height: 320px;
  max-height: 520px;
  overflow: auto;
  padding: 16px;
  margin: 0;
  color: #d8e6ff;
  background: #111827;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
