export default defineNuxtRouteMiddleware(async (to) => {
  if (!import.meta.client) return
  if (to.meta.auth === false) return

  const auth = useAdminAuth()
  auth.restore()
  if (!auth.isSignedIn()) {
    const session = await auth.refresh()
    if (!session) {
      return navigateTo('/auth/login', { replace: true })
    }
  }

  const role = to.meta.role as string | undefined
  if (role && auth.session.value?.user.role !== role) {
    return navigateTo(auth.session.value?.entryPath || '/auth/login', { replace: true })
  }
})
