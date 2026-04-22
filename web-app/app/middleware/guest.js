// export default defineNuxtRouteMiddleware((to, from) => {
//     // Skip middleware on server-side rendering
//     if (process.server) return

//     const authStore = useAuthStore()

//     // Initialize auth store
//     authStore.initAuth()
//     console.log("AAAAAAAAAAAAAAAAAAAA")
//     // If user is authenticated and trying to access login or callback
//     const guestOnlyRoutes = ['/login', '/auth/callback']
//     if (guestOnlyRoutes.includes(to.path) && authStore.isAuthenticated) {
//         return navigateTo('/')
//     }
// })
