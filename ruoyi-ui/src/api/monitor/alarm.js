import request from '@/utils/request'

// 查询告警列表
export function listAlarm(query) {
  return request({
    url: '/monitor/alarm/list',
    method: 'get',
    params: query
  })
}

// 查询告警详细
export function getAlarm(id) {
  return request({
    url: '/monitor/alarm/' + id,
    method: 'get'
  })
}

// 新增告警
export function addAlarm(data) {
  return request({
    url: '/monitor/alarm',
    method: 'post',
    data: data
  })
}

// 修改告警
export function updateAlarm(data) {
  return request({
    url: '/monitor/alarm',
    method: 'put',
    data: data
  })
}

// 删除告警
export function delAlarm(id) {
  return request({
    url: '/monitor/alarm/' + id,
    method: 'delete'
  })
}

// 获取未处理告警数量
export function getUnhandleCount() {
  return request({
    url: '/monitor/alarm/unhandle/count',
    method: 'get'
  })
}

// 获取未处理告警列表
export function getUnhandleList() {
  return request({
    url: '/monitor/alarm/unhandle/list',
    method: 'get'
  })
}

// 处理告警
export function handleAlarm(id, data) {
  return request({
    url: '/monitor/alarm/handle/' + id,
    method: 'put',
    data: data
  })
}

// 忽略告警
export function ignoreAlarm(id) {
  return request({
    url: '/monitor/alarm/ignore/' + id,
    method: 'put'
  })
}
