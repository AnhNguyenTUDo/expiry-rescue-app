import axios from 'axios'

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Update with your backend URL
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor - add auth token if available
apiClient.interceptors.request.use(
  (config) => {
    // Get token from localStorage or cookies
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors globally
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      // Handle specific error codes
      switch (error.response.status) {
        case 401:
          console.error('Unauthorized - Please login')
          // Redirect to login page or refresh token
          break
        case 403:
          console.error('Forbidden - Access denied')
          break
        case 404:
          console.error('Resource not found')
          break
        case 500:
          console.error('Server error')
          break
        default:
          console.error('API Error:', error.response.data)
      }
    } else if (error.request) {
      console.error('No response from server')
    } else {
      console.error('Request error:', error.message)
    }
    return Promise.reject(error)
  }
)

export default apiClient
