<template>
  <section class="module-page">
    <div class="module-hero asset">
      <div>
        <span class="eyebrow">Lifecycle</span>
        <h1>资产变更</h1>
        <p>记录资产领用、归还、报修、维修、巡检和报废过程，为后续风险画像和事件追溯提供上下文。</p>
      </div>
    </div>

    <div class="module-panel">
      <div class="filter-bar">
        <el-input v-model="query.assetName" clearable placeholder="资产名称" />
        <el-input v-model="query.assetCode" clearable placeholder="资产编号" />
        <el-select v-model="query.flowType" clearable placeholder="变更类型">
          <el-option v-for="item in flowTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="rows" class="ops-table">
        <el-table-column label="资产编号" min-width="130" prop="assetCode" />
        <el-table-column label="资产名称" min-width="160" prop="assetName" />
        <el-table-column label="变更类型" width="110">
          <template #default="{ row }">
            <el-tag :type="optionType(flowTypeOptions, row.flowType)">{{ optionLabel(flowTypeOptions, row.flowType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人" min-width="120" prop="operator" />
        <el-table-column label="变更前" min-width="120" prop="beforeStatus" />
        <el-table-column label="变更后" min-width="120" prop="afterStatus" />
        <el-table-column label="前负责人" min-width="120" prop="beforePerson" />
        <el-table-column label="后负责人" min-width="120" prop="afterPerson" />
        <el-table-column label="操作时间" min-width="170" prop="operateTime" />
        <el-table-column label="说明" min-width="220" prop="description" show-overflow-tooltip />
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" background layout="total, sizes, prev, pager, next" @current-change="loadList" @size-change="loadList" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { listAssetFlow } from '@/api/asset/flow'
import { flowTypeOptions, optionLabel, optionType } from '@/utils/dicts'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, assetName: '', assetCode: '', flowType: '' })

onMounted(loadList)

async function loadList() {
  loading.value = true
  try {
    const response = await listAssetFlow(query)
    rows.value = response.rows || []
    total.value = response.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, assetName: '', assetCode: '', flowType: '' })
  loadList()
}
</script>
