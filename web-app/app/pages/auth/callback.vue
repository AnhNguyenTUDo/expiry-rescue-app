<template>
  <div class="flex min-h-[calc(100vh-15rem)] items-center justify-center p-4">
    <AuthCard>
      <!-- Loading State -->
      <AuthStatus
        v-if="isProcessing"
        icon="icon-loading-three-dots"
        icon-class="text-green-600"
        heading="Just a moment..."
        message="We're verifying your account"
      />

      <!-- Success State -->
      <AuthStatus
        v-else-if="isSuccess"
        icon="icon-smiley-thumbs-up"
        icon-class="text-green-700"
        heading="You're in!"
        message="Good to see you again. Taking you there now..."
      />

      <!-- Error State -->
      <AuthStatus
        v-else-if="errorMessage"
        icon="icon-smiley-in-trouble"
        icon-class="text-orange-400"
        heading="Hmm, that didn't work"
        message="Something went wrong. Want to try again?"
      >
        <button
          class="mt-4 w-full cursor-pointer rounded-xl bg-green-700 py-3.5 font-semibold text-white transition-colors hover:bg-green-800"
          @click="goToLogin"
        >
          Try Again
        </button>
      </AuthStatus>
    </AuthCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthCard from '~/components/auth/AuthCard.vue'
import AuthStatus from '~/components/auth/AuthStatus.vue'
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
