<template>
  <section class="module-page">
    <div class="module-hero data">
      <div>
        <span class="eyebrow">Telemetry</span>
        <h1>监控数据</h1>
        <p>沉淀 CPU、内存、磁盘、网络和负载数据，作为后续异常检测和容量预测的原始输入。</p>
      </div>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.serverIp" clearable placeholder="服务器 IP" />
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="服务器 IP" min-width="140" prop="serverIp" />
        <el-table-column label="CPU" width="110"><template #default="{ row }">{{ percent(row.cpuUsage) }}</template></el-table-column>
        <el-table-column label="内存" width="110"><template #default="{ row }">{{ percent(row.memoryUsage) }}</template></el-table-column>
        <el-table-column label="磁盘" width="110"><template #default="{ row }">{{ percent(row.diskUsage) }}</template></el-table-column>
        <el-table-column label="内存用量" min-width="140"><template #default="{ row }">{{ gb(row.memoryUsedGb) }} / {{ gb(row.memoryTotalGb) }}</template></el-table-column>
        <el-table-column label="磁盘用量" min-width="140"><template #default="{ row }">{{ gb(row.diskUsedGb) }} / {{ gb(row.diskTotalGb) }}</template></el-table-column>
        <el-table-column label="网络入/出" min-width="150"><template #default="{ row }">{{ row.networkIn || 0 }} / {{ row.networkOut || 0 }}</template></el-table-column>
        <el-table-column label="负载" min-width="110" prop="loadAverage" />
        <el-table-column label="进程数" width="100" prop="runningProcesses" />
        <el-table-column label="采集时间" min-width="170" prop="collectTime" />
        <el-table-column fixed="right" label="操作" width="90">
          <template #default="{ row }"><el-button link type="danger" @click="removeRow(row)">删除</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMonitorData, delMonitorData } from '@/api/monitor/data'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, serverIp: '' })

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listMonitorData(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, serverIp: '' })
  loadList()
}

async function removeRow(row) {
  await ElMessageBox.confirm('确认删除这条监控数据？', '删除确认', { type: 'warning' })
  await delMonitorData(row.id)
  ElMessage.success('监控数据已删除')
  loadList()
}

function percent(value) {
  return value === null || value === undefined ? '-' : `${Number(value).toFixed(1)}%`
}

function gb(value) {
  return value === null || value === undefined ? '-' : `${Number(value).toFixed(1)}GB`
}
</script>
