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
    </div>

    <!-- Callback Processing Card -->
    <div class="relative w-full max-w-md">
      <div
        class="backdrop-blur-xl bg-white/10 border border-white/20 rounded-3xl shadow-2xl p-8 text-center"
      >
        <!-- Logo -->
        <div
          class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-emerald-400 to-cyan-400 rounded-2xl mb-6 shadow-lg shadow-emerald-500/30"
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
              class="animate-spin h-10 w-10 text-emerald-400"
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
          <p class="text-emerald-200/70 text-sm">Please wait while we verify your account</p>
        </div>

        <!-- Success State -->
        <div v-else-if="isSuccess" class="space-y-4">
          <div class="flex justify-center">
            <div class="w-16 h-16 bg-emerald-500/20 rounded-full flex items-center justify-center">
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
          <p class="text-emerald-200/70 text-sm">Redirecting you to the app...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="errorMessage" class="space-y-4">
          <div class="flex justify-center">
            <div class="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center">
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
          <p class="text-red-300/80 text-sm">{{ errorMessage }}</p>
          <button
            @click="goToLogin"
            class="mt-4 px-6 py-3 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 rounded-xl text-white font-medium shadow-lg shadow-emerald-500/30 transition-all duration-300 hover:-translate-y-0.5"
          >
            Try Again
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "~/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isProcessing = ref(true);
const isSuccess = ref(false);
const errorMessage = ref(null);

const goToLogin = () => {
  router.push("/login");
};

onMounted(async () => {
  // Get token or error from URL query parameters
  const token = route.query.token;
  const error = route.query.error;

  // Simulate a small delay for UX
  await new Promise((resolve) => setTimeout(resolve, 1000));

  if (token) {
    // Success: Store the token and fetch user profile
    await authStore.handleOAuthCallback(token);
    isProcessing.value = false;
    isSuccess.value = true;

    // Redirect to home after a short delay
    setTimeout(() => {
      router.push("/");
    }, 1500);
  } else if (error) {
    // Error: Show error message
    isProcessing.value = false;
    authStore.handleOAuthError(error);
    errorMessage.value = authStore.error;
  } else {
    // No token or error - invalid callback
    isProcessing.value = false;
    errorMessage.value = "Invalid authentication callback. Please try again.";
  }
});
</script>
