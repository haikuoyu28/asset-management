import request from '@/utils/request'

export function listAssetInfo(params) {
  return request({
    url: '/asset/info/list',
    method: 'get',
    params
  })
}

export function getAssetInfo(id) {
  return request({
    url: `/asset/info/${id}`,
    method: 'get'
  })
}

export function addAssetInfo(data) {
  return request({
    url: '/asset/info',
    method: 'post',
    data
  })
}

export function updateAssetInfo(data) {
  return request({
    url: '/asset/info',
    method: 'put',
    data
  })
}

export function delAssetInfo(id) {
  return request({
    url: `/asset/info/${id}`,
    method: 'delete'
  })
}
