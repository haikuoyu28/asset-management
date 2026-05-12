import request from '@/utils/request'

// 查询服务器列表
export function listServer(query) {
  return request({
    url: '/monitor/server/list',
    method: 'get',
    params: query
  })
}

// 查询服务器详细
export function getServer(id) {
  return request({
    url: '/monitor/server/' + id,
    method: 'get'
  })
}

// 新增服务器
export function addServer(data) {
  return request({
    url: '/monitor/server',
    method: 'post',
    data: data
  })
}

// 修改服务器
export function updateServer(data) {
  return request({
    url: '/monitor/server',
    method: 'put',
    data: data
  })
}

// 删除服务器
export function delServer(id) {
  return request({
    url: '/monitor/server/' + id,
    method: 'delete'
  })
}

// 获取所有在线服务器
export function getOnlineServers() {
  return request({
    url: '/monitor/server/online',
    method: 'get'
  })
}

// 导出服务器
export function exportServer(query) {
  return request({
    url: '/monitor/server/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
