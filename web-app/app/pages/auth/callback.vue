<template>
  <div
    class="flex min-h-screen items-center justify-center bg-gradient-to-br from-emerald-900 via-teal-800 to-cyan-900 p-4"
  >
    <!-- Background decorations -->
    <div class="pointer-events-none absolute inset-0 overflow-hidden">
      <div
        class="absolute -top-40 -right-40 h-80 w-80 rounded-full bg-emerald-500/20 blur-3xl"
      ></div>
      <div
        class="absolute -bottom-40 -left-40 h-80 w-80 rounded-full bg-cyan-500/20 blur-3xl"
      ></div>
    </div>

    <!-- Callback Processing Card -->
    <div class="relative w-full max-w-md">
      <div
        class="rounded-3xl border border-white/20 bg-white/10 p-8 text-center shadow-2xl backdrop-blur-xl"
      >
        <!-- Logo -->
        <div
          class="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-emerald-400 to-cyan-400 shadow-lg shadow-emerald-500/30"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-8 w-8 text-white"
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

        <!-- Loading State -->
        <div v-if="isProcessing" class="space-y-4">
          <div class="flex justify-center">
            <svg
              class="h-10 w-10 animate-spin text-emerald-400"
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
          <h2 class="text-xl font-semibold text-white">Completing sign in...</h2>
          <p class="text-sm text-emerald-200/70">Please wait while we verify your account</p>
        </div>

        <!-- Success State -->
        <div v-else-if="isSuccess" class="space-y-4">
          <div class="flex justify-center">
            <div class="flex h-16 w-16 items-center justify-center rounded-full bg-emerald-500/20">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-8 w-8 text-emerald-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
            </div>
          </div>
          <h2 class="text-xl font-semibold text-white">Welcome back!</h2>
          <p class="text-sm text-emerald-200/70">Redirecting you to the app...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="errorMessage" class="space-y-4">
          <div class="flex justify-center">
            <div class="flex h-16 w-16 items-center justify-center rounded-full bg-red-500/20">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-8 w-8 text-red-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </div>
          </div>
          <h2 class="text-xl font-semibold text-white">Authentication Failed</h2>
          <p class="text-sm text-red-300/80">{{ errorMessage }}</p>
          <button
            class="mt-4 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 px-6 py-3 font-medium text-white shadow-lg shadow-emerald-500/30 transition-all duration-300 hover:-translate-y-0.5 hover:from-emerald-400 hover:to-cyan-400"
            @click="goToLogin"
          >
            Try Again
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isProcessing = ref(true)
const isSuccess = ref(false)
const errorMessage = ref(null)

const goToLogin = () => {
  router.push('/login')
}

onMounted(async () => {
  // Get token or error from URL query parameters
  const token = route.query.token
  const error = route.query.error

  // Simulate a small delay for UX
  await new Promise((resolve) => setTimeout(resolve, 1000))

  if (token) {
    // Success: Store the token and fetch user profile
    await authStore.handleOAuthCallback(token)
    isProcessing.value = false
    isSuccess.value = true

    // Redirect to home after a short delay
    setTimeout(() => {
      router.push('/')
    }, 1500)
  } else if (error) {
    // Error: Show error message
    isProcessing.value = false
    authStore.handleOAuthError(error)
    errorMessage.value = authStore.error
  } else {
    // No token or error - invalid callback
    isProcessing.value = false
    errorMessage.value = 'Invalid authentication callback. Please try again.'
  }
})
</script>
