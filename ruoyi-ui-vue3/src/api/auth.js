import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    headers: {
      isToken: false
    },
    data
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

export function getCodeImg() {
  return request({
    url: '/captchaImage',
    method: 'get',
    headers: {
      isToken: false
    },
    timeout: 20000
  })
}
