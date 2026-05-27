const TOKEN_KEY = 'Admin-Token'

export function getToken() {
  const cookies = document.cookie ? document.cookie.split('; ') : []
  const target = cookies.find(item => item.startsWith(`${TOKEN_KEY}=`))
  return target ? decodeURIComponent(target.split('=').slice(1).join('=')) : ''
}

export function setToken(token) {
  document.cookie = `${TOKEN_KEY}=${encodeURIComponent(token)}; path=/`
}

export function removeToken() {
  document.cookie = `${TOKEN_KEY}=; path=/; max-age=0`
}

export function hasToken() {
  return Boolean(getToken())
}
