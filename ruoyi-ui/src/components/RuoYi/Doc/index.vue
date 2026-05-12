<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'AssetDoc',
  data() {
    return {
      url: ''
    }
  },
  methods: {
    goto() {
      if (this.url) {
        window.open(this.url)
      }
    }
  }
}
</script>
