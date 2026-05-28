import { defineStore } from 'pinia'

const themeKey = 'asset-ops-theme'

function getInitialTheme() {
  const savedTheme = window.localStorage.getItem(themeKey)
  return savedTheme === 'dark' ? 'dark' : 'light'
}

function applyTheme(theme) {
  document.documentElement.classList.toggle('theme-dark', theme === 'dark')
  window.localStorage.setItem(themeKey, theme)
}

export const useAppStore = defineStore('app', {
  state: () => ({
    collapsed: false,
    theme: getInitialTheme()
  }),
  actions: {
    toggleSidebar() {
      this.collapsed = !this.collapsed
    },
    setTheme(theme) {
      this.theme = theme === 'dark' ? 'dark' : 'light'
      applyTheme(this.theme)
    },
    toggleTheme() {
      this.setTheme(this.theme === 'dark' ? 'light' : 'dark')
    },
    initTheme() {
      applyTheme(this.theme)
    }
  }
})
