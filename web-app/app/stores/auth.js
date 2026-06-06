import { defineStore } from 'pinia'
import AuthService from '~/services/auth.service'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    user: null,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    getToken: (state) => state.token,
  },

  actions: {
    /**
     * Initialize auth state from localStorage on app mount
     */
    initAuth() {
      if (typeof window !== 'undefined') {
        const token = localStorage.getItem('auth_token')
        const user = localStorage.getItem('auth_user')

        if (token) {
          this.token = token
        }
        if (user) {
          try {
            this.user = JSON.parse(user)
          } catch (e) {
            console.error('Error parsing user data:', e)
          }
        }
      }
    },

    /**
     * Set authentication data after successful login
     */
    setAuth(token, user = null) {
      this.token = token
      this.user = user
      this.error = null

      if (typeof window !== 'undefined') {
        localStorage.setItem('auth_token', token)
        if (user) {
          localStorage.setItem('auth_user', JSON.stringify(user))
        }
      }
    },

    /**
     * Clear authentication data on logout
     */
    logout() {
      this.token = null
      this.user = null
      this.error = null

      if (typeof window !== 'undefined') {
        localStorage.removeItem('auth_token')
        localStorage.removeItem('auth_user')
      }
    },

    /**
     * Set error state
     */
    setError(error) {
      this.error = error
    },

    /**
     * Clear error state
     */
    clearError() {
      this.error = null
    },

    /**
     * Redirect to Google OAuth2 login
     */
    loginWithGoogle() {
      const runtimeConfig = useRuntimeConfig()
      // Backend OAuth2 authorization endpoint
      const backendUrl = runtimeConfig.public.apiBase.replace('/api/v1', '')
      window.location.href = `${backendUrl}/oauth2/authorization/google`
    },

    /**
     * Fetch user profile data from backend
     */
    async fetchUser() {
      if (!this.token) return

      const body = await AuthService.getCurrentUser((err) => {
        console.error('Error fetching user data:', err)
      })

      if (body && body.data) {
        this.user = body.data

        if (typeof window !== 'undefined') {
          localStorage.setItem('auth_user', JSON.stringify(body.data))
        }
      }
    },

    /**
     * Handle OAuth2 callback with token
     */
    async handleOAuthCallback(token) {
      if (token) {
        this.setAuth(token)
        // Fetch user data after setting token
        await this.fetchUser()
        return true
      }
      return false
    },

    /**
     * Handle OAuth2 callback with error
     */
    handleOAuthError(error) {
      const errorMessages = {
        access_denied: 'Access was denied. Please try again.',
        invalid_token: 'Invalid authentication token.',
        unauthorized_client: 'Unauthorized client.',
        unsupported_provider: 'Unsupported authentication provider.',
        email_not_found: 'Email not found in your account.',
        server_error: 'Server error occurred. Please try again later.',
        authentication_failed: 'Authentication failed. Please try again.',
      }
      this.setError(errorMessages[error] || 'An error occurred during login.')
    },

    /**
     * Request a one-time passcode (OTP) to be sent to the given email.
     * Throws an Error (with a user-facing message) on failure.
     */
    async requestOtp(email) {
      try {
        await AuthService.requestOtp(email)
      } catch (error) {
        throw new Error(
          error.response?.data?.message || 'Could not send the code. Please try again.'
        )
      }
    },

    /**
     * Verify an OTP code and, on success, authenticate the user.
     * Throws an Error (with a user-facing message) on failure.
     */
    async verifyOtp(email, code) {
      let response
      try {
        response = await AuthService.verifyOtp(email, code)
      } catch (error) {
        throw new Error(
          error.response?.data?.message || 'Incorrect or expired code. Please try again.'
        )
      }

      const token = response.data.data?.token
      if (!token) {
        throw new Error('Login failed. Please try again.')
      }

      this.setAuth(token)
      await this.fetchUser()
      return true
    },
  },
})
