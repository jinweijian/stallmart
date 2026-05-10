import type { ApiResult } from '~/types/admin'

type FetchOptions = Parameters<typeof $fetch>[1]

export const createApiRequest = () => {
  const config = useRuntimeConfig()
  const baseURL = String(config.public.apiBase).replace(/\/$/, '')

  const request = async <T>(path: string, options: FetchOptions = {}, appendAuthToken = true, retried = false): Promise<T> => {
    const { getAccessToken, selectedStoreId } = useAdminAuth()
    const token = appendAuthToken ? getAccessToken() : null
    const fetchOptions = {
      ...options,
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(selectedStoreId.value ? { 'X-Store-Id': String(selectedStoreId.value) } : {}),
        ...(options.headers || {}),
      },
    }

    try {
      const response = await $fetch<ApiResult<T>>(`${baseURL}${path}`, fetchOptions)
      if (response.code !== 200) {
        throw new Error(response.message || '请求失败')
      }
      return response.data
    } catch (error: any) {
      if (appendAuthToken && !retried && error?.response?.status === 401) {
        const auth = useAdminAuth()
        const nextSession = await auth.refresh()
        if (nextSession?.accessToken) {
          return request<T>(path, options, appendAuthToken, true)
        }
      }
      throw error
    }
  }

  return request
}

export type ApiRequest = ReturnType<typeof createApiRequest>
