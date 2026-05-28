import request from '@/utils/request'

export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}

export function updateUserPwd(oldPassword, newPassword) {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}
