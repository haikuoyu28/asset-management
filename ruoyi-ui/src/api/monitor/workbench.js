import request from '@/utils/request'

export function getWorkbenchSummary() {
  return request({
    url: '/monitor/workbench/summary',
    method: 'get'
  })
}
