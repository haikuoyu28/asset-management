import request from '@/utils/request'

export function listServer(params) {
  return request({
    url: '/monitor/server/list',
    method: 'get',
    params
  })
}

export function getServer(id) {
  return request({
    url: `/monitor/server/${id}`,
    method: 'get'
  })
}

export function addServer(data) {
  return request({
    url: '/monitor/server',
    method: 'post',
    data
  })
}

export function updateServer(data) {
  return request({
    url: '/monitor/server',
    method: 'put',
    data
  })
}

export function resetAgentToken(id) {
  return request({
    url: `/monitor/server/${id}/agent-token`,
    method: 'put'
  })
}

export function delServer(id) {
  return request({
    url: `/monitor/server/${id}`,
    method: 'delete'
  })
}
