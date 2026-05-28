import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import permission from './directives/permission'
import { useAppStore } from '@/stores/app'
import './styles/base.css'

const app = createApp(App)
const pinia = createPinia()

Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  app.component(key, component)
})

app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.directive('hasPermi', permission)

useAppStore(pinia).initTheme()

app.mount('#app')
