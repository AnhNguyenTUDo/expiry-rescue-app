import axios from "axios";

let baseAxiosInstance = null;

export const useAxios = () => {
  if (!baseAxiosInstance) {
    const runtimeConfig = useRuntimeConfig();

    baseAxiosInstance = axios.create({
      baseURL: runtimeConfig.public.apiBase,
      headers: { "Content-Type": "application/json" },
    });

    baseAxiosInstance.interceptors.request.use(
      function (config) {
        // Add JWT token to Authorization header if available
        if (typeof window !== 'undefined') {
          const token = localStorage.getItem('auth_token')
          if (token) {
            config.headers.Authorization = `Bearer ${token}`
          }
        }
        return config;
      },
      function (error) {
        return Promise.reject(error);
      }
    );

    // Add response interceptor to handle auth errors
    baseAxiosInstance.interceptors.response.use(
      function (response) {
        return response;
      },
      function (error) {
        // Handle 401 Unauthorized errors
        if (error.response && error.response.status === 401) {
          // Clear auth data
          if (typeof window !== 'undefined') {
            localStorage.removeItem('auth_token')
            localStorage.removeItem('auth_user')
            // Redirect to login page
            // window.location.href = '/login'
          }
        }
        return Promise.reject(error);
      }
    );
  }
  return baseAxiosInstance;
};
