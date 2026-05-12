import request from '@/utils/request'

// 查询资产流程列表
export function listAssetFlow(query) {
  return request({
    url: '/asset/flow/list',
    method: 'get',
    params: query
  })
}

// 查询资产流程详细
export function getAssetFlow(id) {
  return request({
    url: '/asset/flow/' + id,
    method: 'get'
  })
}

// 根据资产ID查询流程列表
export function listAssetFlowByAssetId(assetId) {
  return request({
    url: '/asset/flow/list/' + assetId,
    method: 'get'
  })
}

// 新增资产流程
export function addAssetFlow(data) {
  return request({
    url: '/asset/flow',
    method: 'post',
    data: data
  })
}

// 修改资产流程
export function updateAssetFlow(data) {
  return request({
    url: '/asset/flow',
    method: 'put',
    data: data
  })
}

// 删除资产流程
export function delAssetFlow(id) {
  return request({
    url: '/asset/flow/' + id,
    method: 'delete'
  })
}
