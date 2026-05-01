import { useStallmartApi } from '~/api/stallmart-api'
import type { AdminLoginInput, AdminSession } from '~/types/admin'

const ACCESS_TOKEN_KEY = 'stallmart.admin.accessToken'
const REFRESH_TOKEN_KEY = 'stallmart.admin.refreshToken'
const SESSION_KEY = 'stallmart.admin.session'
const SELECTED_STORE_KEY = 'stallmart.admin.selectedStoreId'

export const useAdminAuth = () => {
  const session = useState<AdminSession | null>('admin-session', () => null)
  const selectedStoreId = useState<number | null>('admin-selected-store-id', () => null)

  const restore = () => {
    if (!import.meta.client) return
    if (!session.value) {
      const raw = localStorage.getItem(SESSION_KEY)
      session.value = raw ? JSON.parse(raw) as AdminSession : null
    }
    if (!selectedStoreId.value) {
      const storeId = localStorage.getItem(SELECTED_STORE_KEY)
      selectedStoreId.value = storeId ? Number(storeId) : session.value?.storeId ?? null
    }
  }

  const persist = (nextSession: AdminSession) => {
    session.value = nextSession
    selectedStoreId.value = nextSession.storeId
    if (!import.meta.client) return
    localStorage.setItem(ACCESS_TOKEN_KEY, nextSession.accessToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, nextSession.refreshToken)
    localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession))
    if (nextSession.storeId) {
      localStorage.setItem(SELECTED_STORE_KEY, String(nextSession.storeId))
    } else {
      localStorage.removeItem(SELECTED_STORE_KEY)
    }
  }

  const login = async (payload: AdminLoginInput) => {
    const api = useStallmartApi()
    const nextSession = await api.login(payload)
    persist(nextSession)
    return navigateTo(nextSession.entryPath, { replace: true })
  }

  const refresh = async () => {
    if (!import.meta.client) return null
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!refreshToken) {
      signout()
      return null
    }
    try {
      const api = useStallmartApi()
      const nextSession = await api.refresh(refreshToken)
      persist(nextSession)
      return nextSession
    } catch {
      signout()
      return null
    }
  }

  const signout = () => {
    session.value = null
    selectedStoreId.value = null
    if (import.meta.client) {
      localStorage.removeItem(ACCESS_TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      localStorage.removeItem(SESSION_KEY)
      localStorage.removeItem(SELECTED_STORE_KEY)
    }
    return navigateTo('/auth/login', { replace: true })
  }

  const getAccessToken = () => {
    if (import.meta.client) {
      return localStorage.getItem(ACCESS_TOKEN_KEY)
    }
    return null
  }

  const getRefreshToken = () => {
    if (import.meta.client) {
      return localStorage.getItem(REFRESH_TOKEN_KEY)
    }
    return null
  }

  const isSignedIn = () => {
    restore()
    return Boolean(session.value || getAccessToken())
  }

  const hasRole = (role: string) => {
    restore()
    return session.value?.user.role === role
  }

  return {
    session,
    selectedStoreId,
    getAccessToken,
    getRefreshToken,
    hasRole,
    isSignedIn,
    login,
    refresh,
    restore,
    signout,
  }
}
