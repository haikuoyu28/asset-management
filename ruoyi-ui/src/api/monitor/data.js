import request from '@/utils/request'

// 查询监控数据列表
export function listMonitorData(query) {
  return request({
    url: '/monitor/data/list',
    method: 'get',
    params: query
  })
}

// 获取最新监控数据
export function getLatestData(serverId) {
  return request({
    url: '/monitor/data/latest/' + serverId,
    method: 'get'
  })
}

// 获取最近监控数据（图表用）
export function getRecentData(serverId, limit) {
  return request({
    url: '/monitor/data/recent/' + serverId,
    method: 'get',
    params: { limit: limit }
  })
}

export function reportMonitorData(data) {
  return request({
    url: '/monitor/data/report',
    method: 'post',
    data: data
  })
}

export function delMonitorData(id) {
  return request({
    url: '/monitor/data/' + id,
    method: 'delete'
  })
}
