import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null,
    user: null,
    isLoading: false,
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
      if (typeof window !== "undefined") {
        const token = localStorage.getItem("auth_token");
        const user = localStorage.getItem("auth_user");

        if (token) {
          this.token = token;
        }
        if (user) {
          try {
            this.user = JSON.parse(user);
          } catch (e) {
            console.error("Error parsing user data:", e);
          }
        }
      }
    },

    /**
     * Set authentication data after successful login
     */
    setAuth(token, user = null) {
      this.token = token;
      this.user = user;
      this.error = null;

      if (typeof window !== "undefined") {
        localStorage.setItem("auth_token", token);
        if (user) {
          localStorage.setItem("auth_user", JSON.stringify(user));
        }
      }
    },

    /**
     * Clear authentication data on logout
     */
    logout() {
      this.token = null;
      this.user = null;
      this.error = null;

      if (typeof window !== "undefined") {
        localStorage.removeItem("auth_token");
        localStorage.removeItem("auth_user");
      }
    },

    /**
     * Set error state
     */
    setError(error) {
      this.error = error;
    },

    /**
     * Clear error state
     */
    clearError() {
      this.error = null;
    },

    /**
     * Set loading state
     */
    setLoading(loading) {
      this.isLoading = loading;
    },

    /**
     * Redirect to Google OAuth2 login
     */
    loginWithGoogle() {
      const runtimeConfig = useRuntimeConfig();
      // Backend OAuth2 authorization endpoint
      const backendUrl = runtimeConfig.public.apiBase.replace("/api/v1", "");
      window.location.href = `${backendUrl}/oauth2/authorization/google`;
    },

    /**
     * Redirect to Facebook OAuth2 login
     */
    loginWithFacebook() {
      const runtimeConfig = useRuntimeConfig();
      const backendUrl = runtimeConfig.public.apiBase.replace("/api/v1", "");
      window.location.href = `${backendUrl}/oauth2/authorization/facebook`;
    },

    /**
     * Fetch user profile data from backend
     */
    async fetchUser() {
      if (!this.token) return;

      try {
        const runtimeConfig = useRuntimeConfig();
        const endpoint = `${runtimeConfig.public.apiBase}/auth/me`;

        const response = await fetch(endpoint, {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });

        if (response.ok) {
          const userData = await response.json();
          this.user = userData.data;

          if (typeof window !== "undefined") {
            localStorage.setItem("auth_user", JSON.stringify(userData.data));
          }
        } else {
          console.error("❌ Failed to fetch user data, status:", response.status);
          const errorText = await response.text();
          console.error("Error response:", errorText);
        }
      } catch (error) {
        console.error("❌ Error fetching user data:", error);
      }
    },

    /**
     * Handle OAuth2 callback with token
     */
    async handleOAuthCallback(token) {
      if (token) {
        this.setAuth(token);
        // Fetch user data after setting token
        await this.fetchUser();
        return true;
      }
      return false;
    },

    /**
     * Handle OAuth2 callback with error
     */
    handleOAuthError(error) {
      const errorMessages = {
        access_denied: "Access was denied. Please try again.",
        invalid_token: "Invalid authentication token.",
        unauthorized_client: "Unauthorized client.",
        unsupported_provider: "Unsupported authentication provider.",
        email_not_found: "Email not found in your account.",
        server_error: "Server error occurred. Please try again later.",
        authentication_failed: "Authentication failed. Please try again.",
      };
      this.setError(errorMessages[error] || "An error occurred during login.");
    },
  },
});
