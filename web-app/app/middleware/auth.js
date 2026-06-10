export default defineNuxtRouteMiddleware((to) => {
  const auth = useAuthStore()

  // The store is normally hydrated from localStorage in the default layout's
  // onMounted, which runs *after* route middleware. On a hard refresh of a
  // protected page the store is still empty here, so rehydrate first to avoid
  // bouncing an already-logged-in user. initAuth() is idempotent and guarded
  // for the (SPA) client.
  if (!auth.isAuthenticated) {
    auth.initAuth()
  }

  if (!auth.isAuthenticated) {
    // Remember where the user was headed so login can send them back.
    return navigateTo(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }
})
