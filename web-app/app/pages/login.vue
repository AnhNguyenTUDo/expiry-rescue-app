<template>
  <div
    class="min-h-screen bg-gradient-to-br from-emerald-900 via-teal-800 to-cyan-900 flex items-center justify-center p-4"
  >
    <!-- Background decorations -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div
        class="absolute -top-40 -right-40 w-80 h-80 bg-emerald-500/20 rounded-full blur-3xl"
      ></div>
      <div
        class="absolute -bottom-40 -left-40 w-80 h-80 bg-cyan-500/20 rounded-full blur-3xl"
      ></div>
      <div
        class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl"
      ></div>
    </div>

    <!-- Login Card -->
    <div class="relative w-full max-w-md">
      <!-- Glassmorphism card -->
      <div
        class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-3xl shadow-2xl p-8 transition-all duration-500 hover:bg-white/15"
      >
        <!-- Logo and Title -->
        <div class="text-center mb-8">
          <div
            class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-emerald-400 to-cyan-400 rounded-2xl mb-4 shadow-lg shadow-emerald-500/30"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-10 w-10 text-white"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
              />
            </svg>
          </div>
          <h1 class="text-3xl font-bold text-white mb-2">Expiry Rescue</h1>
          <p class="text-emerald-200/80 text-sm">Save food, save money, save the planet</p>
        </div>

        <!-- Error Message -->
        <div
          v-if="authStore.error"
          class="mb-6 p-4 bg-red-500/20 border border-red-400/30 rounded-xl backdrop-blur-sm"
        >
          <div class="flex items-center gap-3">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 text-red-400 flex-shrink-0"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <span class="text-red-200 text-sm">{{ authStore.error }}</span>
          </div>
        </div>

        <!-- Social Login Buttons -->
        <div class="space-y-4">
          <!-- Google Login -->
          <button
            @click="handleGoogleLogin"
            :disabled="isLoading"
            class="cursor-pointer group relative w-full flex items-center justify-center gap-3 px-6 py-4 bg-white hover:bg-gray-50 rounded-xl text-gray-700 font-medium transition-all duration-300 shadow-lg hover:shadow-xl hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed overflow-hidden"
          >
            <div
              class="absolute inset-0 bg-gradient-to-r from-blue-500/0 via-blue-500/5 to-red-500/0 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700"
            ></div>
            <svg
              class="w-5 h-5 relative z-10"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                fill="#4285F4"
              />
              <path
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                fill="#34A853"
              />
              <path
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                fill="#FBBC05"
              />
              <path
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                fill="#EA4335"
              />
            </svg>
            <span class="relative z-10">Continue with Google</span>
            <div v-if="isLoading && loginProvider === 'google'" class="absolute right-4">
              <svg
                class="animate-spin h-5 w-5 text-gray-500"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            </div>
          </button>
        </div>

        <!-- Divider -->
        <div class="relative my-8">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-white/20"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-4 text-emerald-200/60 bg-transparent">or</span>
          </div>
        </div>

        <!-- Email Login Form (Placeholder) -->
        <form @submit.prevent="handleEmailLogin" class="space-y-4">
          <div>
            <label for="email" class="block text-sm font-medium text-emerald-200/80 mb-2"
              >Email</label
            >
            <input
              type="email"
              id="email"
              v-model="email"
              placeholder="Enter your email"
              class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white placeholder-white/40 focus:outline-none focus:ring-2 focus:ring-emerald-400/50 focus:border-transparent transition-all duration-300"
            />
          </div>
          <div>
            <label for="password" class="block text-sm font-medium text-emerald-200/80 mb-2"
              >Password</label
            >
            <input
              type="password"
              id="password"
              v-model="password"
              placeholder="Enter your password"
              class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white placeholder-white/40 focus:outline-none focus:ring-2 focus:ring-emerald-400/50 focus:border-transparent transition-all duration-300"
            />
          </div>
          <button
            type="submit"
            disabled
            class="w-full py-4 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 rounded-xl text-white font-semibold shadow-lg shadow-emerald-500/30 transition-all duration-300 hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Sign In
          </button>
        </form>

        <!-- Footer Links -->
        <div class="mt-8 text-center">
          <p class="text-emerald-200/60 text-sm">
            Don't have an account?
            <a
              href="#"
              class="text-emerald-400 hover:text-emerald-300 transition-colors duration-300"
              >Sign up</a
            >
          </p>
        </div>
      </div>

      <!-- Trust badges -->
      <div class="mt-6 flex items-center justify-center gap-6 text-emerald-300/50 text-xs">
        <span class="flex items-center gap-1">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
            />
          </svg>
          Secure login
        </span>
        <span class="flex items-center gap-1">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
            />
          </svg>
          Privacy protected
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useAuthStore } from "~/stores/auth";

const authStore = useAuthStore();

// Form data
const email = ref("");
const password = ref("");
const isLoading = ref(false);
const loginProvider = ref(null);

// Handle Google login
const handleGoogleLogin = () => {
  isLoading.value = true;
  loginProvider.value = "google";
  authStore.clearError();
  authStore.loginWithGoogle();
};

// Handle Facebook login
const handleFacebookLogin = () => {
  isLoading.value = true;
  loginProvider.value = "facebook";
  authStore.clearError();
  authStore.loginWithFacebook();
};

// Handle email login (placeholder)
const handleEmailLogin = () => {
  // TODO: Implement email/password login
  console.log("Email login not implemented yet");
};

// Initialize auth store
onMounted(() => {
  authStore.initAuth();

  // If already authenticated, redirect to home
  if (authStore.isAuthenticated) {
    navigateTo("/");
  }
});
</script>

<style scoped>
/* Custom animations */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.float-animation {
  animation: float 3s ease-in-out infinite;
}
</style>
