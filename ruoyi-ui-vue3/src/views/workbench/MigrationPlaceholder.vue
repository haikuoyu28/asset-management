<template>
  <section class="page-pad">
    <div class="placeholder-panel">
      <div>
        <p class="eyebrow">Migration Lane</p>
        <h1>{{ title }}</h1>
        <p>
          这个模块会在后续批次迁移到 Vue3。当前阶段先保留 Vue2 页面作为生产入口，避免主流程在升级中被打断。
        </p>
      </div>
      <el-button type="primary" @click="openLegacy">
        打开现有页面
      </el-button>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title || '业务模块')
const legacyBase = import.meta.env.VITE_APP_LEGACY_BASE || '/'

function openLegacy() {
  const path = route.meta.legacyPath || '/index'
  window.location.href = `${legacyBase.replace(/\/$/, '')}${path}`
}
</script>
