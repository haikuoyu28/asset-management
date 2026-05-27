import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    cacheDir: '.vite-cache',
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 5173,
      proxy: {
        [env.VITE_APP_BASE_API || '/dev-api']: {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: path => path.replace(new RegExp(`^${env.VITE_APP_BASE_API || '/dev-api'}`), '')
        }
      }
    },
    build: {
      outDir: 'dist',
      sourcemap: false,
      chunkSizeWarningLimit: 1200,
      rollupOptions: {
        output: {
          manualChunks: {
            vue: ['vue', 'vue-router', 'pinia'],
            element: ['element-plus', '@element-plus/icons-vue'],
            charts: ['echarts']
          }
        }
      }
    }
  }
})
