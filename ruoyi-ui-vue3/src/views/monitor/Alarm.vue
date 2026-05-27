<template>
  <section class="module-page">
    <div class="module-hero alarm">
      <div>
        <span class="eyebrow">Incident Loop</span>
        <h1>告警事件</h1>
        <p>按照未处理、处理中、已处理和已忽略沉淀处置记录，为后续智能降噪和根因分析准备样本。</p>
      </div>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.serverIp" clearable placeholder="服务器 IP" />
        <el-select v-model="query.alarmLevel" clearable placeholder="告警等级">
          <el-option v-for="item in alarmLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="query.alarmStatus" clearable placeholder="处理状态">
          <el-option v-for="item in alarmStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="服务器" min-width="170">
          <template #default="{ row }">{{ row.serverIp }} / {{ row.hostname || '-' }}</template>
        </el-table-column>
        <el-table-column label="类型" width="100"><template #default="{ row }">{{ optionLabel(alarmTypeOptions, row.alarmType) }}</template></el-table-column>
        <el-table-column label="等级" width="96">
          <template #default="{ row }"><el-tag :type="optionType(alarmLevelOptions, row.alarmLevel)">{{ optionLabel(alarmLevelOptions, row.alarmLevel) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }"><el-tag :type="optionType(alarmStatusOptions, row.alarmStatus)">{{ optionLabel(alarmStatusOptions, row.alarmStatus) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="告警值" width="100" prop="alarmValue" />
        <el-table-column label="阈值" width="100" prop="thresholdValue" />
        <el-table-column label="告警内容" min-width="240" prop="alarmMessage" show-overflow-tooltip />
        <el-table-column label="告警时间" min-width="170" prop="alarmTime" />
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="{ row }">
            <el-button :disabled="row.alarmStatus === '2'" link type="primary" @click="openHandle(row)">处理</el-button>
            <el-button :disabled="row.alarmStatus === '2' || row.alarmStatus === '3'" link type="info" @click="ignoreRow(row)">忽略</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>

    <el-dialog v-model="handleVisible" title="处理告警" width="560px">
      <el-form :model="handleForm" label-width="92px">
        <el-form-item label="告警内容"><el-input v-model="handleForm.alarmMessage" disabled type="textarea" /></el-form-item>
        <el-form-item label="处理结果"><el-input v-model="handleForm.handleResult" :rows="4" placeholder="请输入处理结果" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAlarm, handleAlarm, ignoreAlarm } from '@/api/monitor/alarm'
import { alarmLevelOptions, alarmStatusOptions, alarmTypeOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const handleVisible = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, serverIp: '', alarmLevel: '', alarmStatus: '' })
const handleForm = reactive({ id: undefined, alarmMessage: '', handleResult: '' })

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listAlarm(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, serverIp: '', alarmLevel: '', alarmStatus: '' })
  loadList()
}

function openHandle(row) {
  Object.assign(handleForm, { id: row.id, alarmMessage: row.alarmMessage || '', handleResult: row.handleResult || '' })
  handleVisible.value = true
}

async function submitHandle() {
  if (!handleForm.handleResult.trim()) {
    ElMessage.warning('请填写处理结果')
    return
  }
  await handleAlarm(handleForm.id, { handleResult: handleForm.handleResult })
  ElMessage.success('告警已处理')
  handleVisible.value = false
  loadList()
}

async function ignoreRow(row) {
  await ElMessageBox.confirm(`确认忽略 ${row.serverIp} 的告警？`, '忽略告警')
  await ignoreAlarm(row.id)
  ElMessage.success('告警已忽略')
  loadList()
}
</script>
