import request from '../utils/request'

// ========== 用户端 ==========

// 发送消息给客服
export function sendMessageApi(data) {
  return request.post('/chat/send', data)
}

// 获取与客服的聊天记录
export function getMessagesApi() {
  return request.get('/chat/messages')
}

// 获取未读消息数
export function getUnreadCountApi() {
  return request.get('/chat/unread')
}

// ========== 管理员端 ==========

// 获取会话列表
export function adminGetSessionsApi() {
  return request.get('/admin/chat/sessions')
}

// 获取与某用户的聊天记录
export function adminGetMessagesApi(userId) {
  return request.get(`/admin/chat/messages/${userId}`)
}

// 管理员发送消息给用户
export function adminSendMessageApi(userId, data) {
  return request.post(`/admin/chat/send/${userId}`, data)
}

// 获取管理员总未读数
export function adminGetUnreadApi() {
  return request.get('/admin/chat/unread')
}
