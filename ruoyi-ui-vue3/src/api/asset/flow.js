import request from '@/utils/request'

export function listAssetFlow(params) {
  return request({
    url: '/asset/flow/list',
    method: 'get',
    params
  })
}
