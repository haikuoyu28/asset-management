import request from '@/utils/request'

// 查询设备资产列表
export function listAssetInfo(query) {
  return request({
    url: '/asset/info/list',
    method: 'get',
    params: query
  })
}

// 查询设备资产详细
export function getAssetInfo(id) {
  return request({
    url: '/asset/info/' + id,
    method: 'get'
  })
}

// 新增设备资产
export function addAssetInfo(data) {
  return request({
    url: '/asset/info',
    method: 'post',
    data: data
  })
}

// 修改设备资产
export function updateAssetInfo(data) {
  return request({
    url: '/asset/info',
    method: 'put',
    data: data
  })
}

// 删除设备资产
export function delAssetInfo(id) {
  return request({
    url: '/asset/info/' + id,
    method: 'delete'
  })
}

// 导出设备资产
export function exportAssetInfo(query) {
  return request({
    url: '/asset/info/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
