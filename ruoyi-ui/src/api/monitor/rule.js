import request from '@/utils/request'

export function listRule(query) {
  return request({
    url: '/monitor/rule/list',
    method: 'get',
    params: query
  })
}

export function getRule(id) {
  return request({
    url: '/monitor/rule/' + id,
    method: 'get'
  })
}

export function addRule(data) {
  return request({
    url: '/monitor/rule',
    method: 'post',
    data: data
  })
}

export function updateRule(data) {
  return request({
    url: '/monitor/rule',
    method: 'put',
    data: data
  })
}

export function delRule(id) {
  return request({
    url: '/monitor/rule/' + id,
    method: 'delete'
  })
}
