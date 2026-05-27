import request from '@/utils/request'

export function listMonitorData(params) {
  return request({
    url: '/monitor/data/list',
    method: 'get',
    params
  })
}

export function delMonitorData(id) {
  return request({
    url: `/monitor/data/${id}`,
    method: 'delete'
  })
}
