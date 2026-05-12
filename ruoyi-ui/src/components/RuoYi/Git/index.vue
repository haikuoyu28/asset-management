<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'AssetGit',
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
