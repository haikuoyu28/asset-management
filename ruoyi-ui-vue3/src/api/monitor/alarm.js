import request from '@/utils/request'

export function listAlarm(params) {
  return request({
    url: '/monitor/alarm/list',
    method: 'get',
    params
  })
}

export function getAlarm(id) {
  return request({
    url: `/monitor/alarm/${id}`,
    method: 'get'
  })
}

export function handleAlarm(id, data) {
  return request({
    url: `/monitor/alarm/handle/${id}`,
    method: 'put',
    data
  })
}

export function ignoreAlarm(id) {
  return request({
    url: `/monitor/alarm/ignore/${id}`,
    method: 'put'
  })
}
