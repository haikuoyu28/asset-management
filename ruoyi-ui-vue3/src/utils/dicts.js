export const assetTypeOptions = [
  { label: '服务器', value: '1' },
  { label: '网络设备', value: '2' },
  { label: '安全设备', value: '3' },
  { label: '办公终端', value: '4' },
  { label: '其他', value: '9' }
]

export const assetStatusOptions = [
  { label: '在用', value: '0', type: 'success' },
  { label: '闲置', value: '1', type: 'info' },
  { label: '维修', value: '2', type: 'warning' },
  { label: '报废', value: '3', type: 'danger' }
]

export const connectionStatusOptions = [
  { label: '在线', value: '0', type: 'success' },
  { label: '离线', value: '1', type: 'danger' }
]

export const monitorStatusOptions = [
  { label: '正常', value: '0', type: 'success' },
  { label: '异常', value: '1', type: 'danger' },
  { label: '未知', value: '2', type: 'info' }
]

export const alarmLevelOptions = [
  { label: '提示', value: '1', type: 'info' },
  { label: '警告', value: '2', type: 'warning' },
  { label: '严重', value: '3', type: 'danger' }
]

export const alarmStatusOptions = [
  { label: '未处理', value: '0', type: 'danger' },
  { label: '处理中', value: '1', type: 'warning' },
  { label: '已处理', value: '2', type: 'success' },
  { label: '已忽略', value: '3', type: 'info' }
]

export const alarmTypeOptions = [
  { label: 'CPU', value: 'cpu' },
  { label: '内存', value: 'memory' },
  { label: '磁盘', value: 'disk' },
  { label: '离线', value: 'offline' },
  { label: '其他', value: 'other' }
]

export function optionLabel(options, value) {
  return options.find(item => item.value === String(value))?.label || value || '-'
}

export function optionType(options, value) {
  return options.find(item => item.value === String(value))?.type || 'info'
}
