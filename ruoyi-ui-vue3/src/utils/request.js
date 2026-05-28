import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    const isToken = (config.headers || {}).isToken === false
    if (token && !isToken) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const data = response.data
    const code = data?.code ?? 200

    if (response.request?.responseType === 'blob' || response.request?.responseType === 'arraybuffer') {
      return data
    }

    if (code === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      return Promise.reject(new Error('Unauthorized'))
    }

    if (code !== 200) {
      ElMessage.error(data?.msg || '接口请求失败')
      return Promise.reject(new Error(data?.msg || 'Request failed'))
    }

    return data
  },
  error => {
    const message = error.message === 'Network Error'
      ? '后端接口连接异常'
      : error.message?.includes('timeout')
        ? '系统接口请求超时'
        : '系统接口请求异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
