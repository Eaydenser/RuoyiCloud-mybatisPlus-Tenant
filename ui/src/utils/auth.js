import Cookies from 'js-cookie'

const TokenKey = 'Token'

const ExpiresInKey = 'Admin-Expires-In'

const TenantIdKey = 'TenantId'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function getTenantId() {
  return Cookies.get(TenantIdKey)
}

export function setTenantId(tenantId) {
  return Cookies.set(TenantIdKey, tenantId)
}

export function removeTenantId() {
  return Cookies.remove(TenantIdKey)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getExpiresIn() {
  return Cookies.get(ExpiresInKey) || -1
}

export function setExpiresIn(time) {
  return Cookies.set(ExpiresInKey, time)
}

export function removeExpiresIn() {
  return Cookies.remove(ExpiresInKey)
}
