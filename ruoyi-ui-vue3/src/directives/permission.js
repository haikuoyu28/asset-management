import { useUserStore } from '@/stores/user'

export function hasPermi(value) {
  const user = useUserStore()
  const permissions = user.permissions || []
  if (!value || !value.length || permissions.includes('*:*:*')) {
    return true
  }
  return value.some(item => permissions.includes(item))
}

export default {
  mounted(el, binding) {
    if (!hasPermi(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
