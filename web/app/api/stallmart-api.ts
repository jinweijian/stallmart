import { createApiRequest } from '~/api/client'
import { createAuthApi } from '~/api/auth-api'
import { createDecorationApi } from '~/api/decoration-api'
import { createOrderApi } from '~/api/order-api'
import { createPlatformApi } from '~/api/platform-api'
import { createProductApi } from '~/api/product-api'
import { createVendorApi } from '~/api/vendor-api'

export const useStallmartApi = () => {
  const request = createApiRequest()

  return {
    ...createAuthApi(request),
    ...createPlatformApi(request),
    ...createVendorApi(request),
    ...createProductApi(request),
    ...createOrderApi(request),
    ...createDecorationApi(request),
  }
}
