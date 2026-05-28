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

export function testSshConnection(id) {
  return request({
    url: `/monitor/server/${id}/test-ssh`,
    method: 'post'
  })
}

export function collectServer(id) {
  return request({
    url: `/monitor/server/${id}/collect`,
    method: 'post'
  })
}

export function executeServerCommand(id, commandKey) {
  return request({
    url: `/monitor/server/${id}/command`,
    method: 'post',
    data: { commandKey }
  })
}

export function delServer(id) {
  return request({
    url: `/monitor/server/${id}`,
    method: 'delete'
  })
}
