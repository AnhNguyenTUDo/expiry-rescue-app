import axios from 'axios'

let baseAxiosInstance = null

export const useAxios = () => {
  if (!baseAxiosInstance) {
    const runtimeConfig = useRuntimeConfig()

    baseAxiosInstance = axios.create({
      baseURL: runtimeConfig.public.apiBase,
      headers: { 'Content-Type': 'application/json' },
    })

    baseAxiosInstance.interceptors.request.use(
      function (config) {
        // Add JWT token to Authorization header if available
        if (typeof window !== 'undefined') {
          const token = localStorage.getItem('auth_token')
          if (token) {
            config.headers.Authorization = `Bearer ${token}`
          }
        }
        return config
      },
      function (error) {
        return Promise.reject(error)
      }
    )

    // Add response interceptor to handle auth errors
    baseAxiosInstance.interceptors.response.use(
      function (response) {
        return response
      },
      function (error) {
        // Handle 401 Unauthorized errors
        if (error.response && error.response.status === 401 && typeof window !== 'undefined') {
          // Clear auth data
          localStorage.removeItem('auth_token')
          localStorage.removeItem('auth_user')

          // Guard against redirect loops if we're already on the login page (e.g. a
          // failed OTP verify also returns 401). Otherwise preserve the current path
          // so login can send the user back after re-authenticating.
          if (!window.location.pathname.startsWith('/login')) {
            const redirect = encodeURIComponent(window.location.pathname + window.location.search)
            window.location.href = `/login?redirect=${redirect}`
          }
        }
        return Promise.reject(error)
      }
    )
  }
  return baseAxiosInstance
}
