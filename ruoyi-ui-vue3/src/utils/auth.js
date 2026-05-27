const TOKEN_KEY = 'Admin-Token'

export function getToken() {
  const cookies = document.cookie ? document.cookie.split('; ') : []
  const target = cookies.find(item => item.startsWith(`${TOKEN_KEY}=`))
  return target ? decodeURIComponent(target.split('=').slice(1).join('=')) : ''
}

export function hasToken() {
  return Boolean(getToken())
}
