import { defineStore } from 'pinia'
import { getInfo, logout as logoutApi } from '@/api/auth'
import { getToken, removeToken, setToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    roles: [],
    permissions: []
  }),
  getters: {
    isLoaded: state => Boolean(state.name || state.roles.length || state.permissions.length)
  },
  actions: {
    setLoginToken(token) {
      this.token = token
      setToken(token)
    },
    async loadUserInfo() {
      if (!this.token) {
        return
      }
      const response = await getInfo()
      const user = response.user || {}
      this.name = user.nickName || user.userName || '运维用户'
      this.roles = response.roles || []
      this.permissions = response.permissions || []
    },
    async logout() {
      try {
        if (this.token) {
          await logoutApi()
        }
      } finally {
        this.token = ''
        this.name = ''
        this.roles = []
        this.permissions = []
        removeToken()
      }
    }
  }
})
