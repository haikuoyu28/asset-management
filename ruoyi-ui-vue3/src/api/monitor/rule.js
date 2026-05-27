import request from '@/utils/request'

export function listRule(params) {
  return request({
    url: '/monitor/rule/list',
    method: 'get',
    params
  })
}

export function addRule(data) {
  return request({
    url: '/monitor/rule',
    method: 'post',
    data
  })
}

export function updateRule(data) {
  return request({
    url: '/monitor/rule',
    method: 'put',
    data
  })
}

export function delRule(id) {
  return request({
    url: `/monitor/rule/${id}`,
    method: 'delete'
  })
}
