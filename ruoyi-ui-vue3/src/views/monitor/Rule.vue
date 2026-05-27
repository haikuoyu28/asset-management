<template>
  <section class="module-page">
    <div class="module-hero alarm">
      <div>
        <span class="eyebrow">Alarm Policy</span>
        <h1>告警规则</h1>
        <p>将阈值、范围、沉默时间和告警等级结构化，后续才能做智能降噪和规则推荐。</p>
      </div>
      <el-button type="primary" @click="openForm()">新增规则</el-button>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.ruleName" clearable placeholder="规则名称" />
        <el-select v-model="query.alarmType" clearable placeholder="指标类型">
          <el-option v-for="item in alarmTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="query.enabled" clearable placeholder="启用状态">
          <el-option label="启用" value="1" />
          <el-option label="停用" value="0" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="规则名称" min-width="180" prop="ruleName" />
        <el-table-column label="指标" width="100"><template #default="{ row }">{{ optionLabel(alarmTypeOptions, row.alarmType) }}</template></el-table-column>
        <el-table-column label="等级" width="96"><template #default="{ row }"><el-tag :type="optionType(alarmLevelOptions, row.alarmLevel)">{{ optionLabel(alarmLevelOptions, row.alarmLevel) }}</el-tag></template></el-table-column>
        <el-table-column label="条件" min-width="130"><template #default="{ row }">{{ row.compareOperator }} {{ row.thresholdValue }}</template></el-table-column>
        <el-table-column label="范围" min-width="150"><template #default="{ row }">{{ optionLabel(scopeTypeOptions, row.scopeType) }} {{ row.serverIp || '' }}</template></el-table-column>
        <el-table-column label="沉默分钟" width="110" prop="silentMinutes" />
        <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.enabled === '1' ? 'success' : 'info'">{{ row.enabled === '1' ? '启用' : '停用' }}</el-tag></template></el-table-column>
        <el-table-column fixed="right" label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="danger" @click="removeRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>

    <el-dialog v-model="formVisible" :title="form.id ? '编辑规则' : '新增规则'" width="680px">
      <el-form :model="form" label-width="100px">
        <div class="form-grid">
          <el-form-item label="规则名称"><el-input v-model="form.ruleName" /></el-form-item>
          <el-form-item label="指标类型">
            <el-select v-model="form.alarmType"><el-option v-for="item in alarmTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="告警等级">
            <el-select v-model="form.alarmLevel"><el-option v-for="item in alarmLevelOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="比较符">
            <el-select v-model="form.compareOperator"><el-option v-for="item in compareOperatorOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="阈值"><el-input-number v-model="form.thresholdValue" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="沉默分钟"><el-input-number v-model="form.silentMinutes" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="范围">
            <el-select v-model="form.scopeType"><el-option v-for="item in scopeTypeOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select>
          </el-form-item>
          <el-form-item label="服务器 ID"><el-input-number v-model="form.serverId" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="启用"><el-switch v-model="form.enabled" active-value="1" inactive-value="0" /></el-form-item>
        </div>
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
import { listRule, addRule, updateRule, delRule } from '@/api/monitor/rule'
import { alarmLevelOptions, alarmTypeOptions, compareOperatorOptions, scopeTypeOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const formVisible = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, ruleName: '', alarmType: '', enabled: '' })
const form = reactive(defaultForm())

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listRule(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, ruleName: '', alarmType: '', enabled: '' })
  loadList()
}

function openForm(row) {
  Object.assign(form, defaultForm(), row || {})
  formVisible.value = true
}

async function submitForm() {
  if (form.id) {
    await updateRule(form)
  } else {
    await addRule(form)
  }
  ElMessage.success('规则已保存')
  formVisible.value = false
  loadList()
}

async function removeRow(row) {
  await ElMessageBox.confirm(`确认删除规则 ${row.ruleName}？`, '删除确认', { type: 'warning' })
  await delRule(row.id)
  ElMessage.success('规则已删除')
  loadList()
}

function defaultForm() {
  return {
    id: undefined,
    ruleName: '',
    alarmType: 'cpu',
    alarmLevel: '2',
    compareOperator: '>=',
    thresholdValue: 80,
    scopeType: 'all',
    serverId: undefined,
    silentMinutes: 10,
    enabled: '1'
  }
}
</script>
