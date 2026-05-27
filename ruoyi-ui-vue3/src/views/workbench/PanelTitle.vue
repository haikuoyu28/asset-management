<template>
  <div class="panel-title">
    <div>
      <h2>{{ title }}</h2>
      <span>{{ subtitle }}</span>
    </div>
    <button v-if="action" type="button" :class="{ danger }" @click="$emit('action')">
      {{ action }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  action: { type: String, default: '' },
  danger: { type: Boolean, default: false }
})

defineEmits(['action'])
</script>
