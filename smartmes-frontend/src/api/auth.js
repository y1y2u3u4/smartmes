import request from './request'

/**
 * Auth API endpoints
 */
export const AUTH_PATHS = {
  LOGIN: '/auth/login',
  LOGOUT: '/auth/logout',
  REFRESH: '/auth/refresh',
  ME: '/auth/me',
  PASSWORD: '/auth/password'
}

/**
 * User login
 * @param {Object} credentials - { username, password }
 * @returns {Promise}
 */
export function login(credentials) {
  return request({
    url: AUTH_PATHS.LOGIN,
    method: 'POST',
    data: credentials
  })
}

/**
 * User logout
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: AUTH_PATHS.LOGOUT,
    method: 'POST'
  })
}

/**
 * Refresh access token
 * @param {string} refreshToken - The refresh token
 * @returns {Promise}
 */
export function refreshToken(refreshToken) {
  return request({
    url: AUTH_PATHS.REFRESH,
    method: 'POST',
    data: { refreshToken }
  })
}

/**
 * Get current user info
 * @returns {Promise}
 */
export function getCurrentUser() {
  return request({
    url: AUTH_PATHS.ME,
    method: 'GET'
  })
}

/**
 * Change password
 * @param {Object} passwordData - { oldPassword, newPassword, confirmPassword }
 * @returns {Promise}
 */
export function changePassword(passwordData) {
  return request({
    url: AUTH_PATHS.PASSWORD,
    method: 'PUT',
    data: passwordData
  })
}
