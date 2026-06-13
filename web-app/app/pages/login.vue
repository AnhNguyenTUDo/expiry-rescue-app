<template>
  <div class="flex min-h-[calc(100vh-15rem)] items-center justify-center p-4">
    <div class="w-full max-w-md">
      <AuthCard title="Expiry Rescue" subtitle="Eat well, waste less">
        <!-- Step 1: choose a method (Google or email) -->
        <template v-if="step === 'email'">
          <!-- Portfolio demo: one-click login + a hint for the fixed-OTP path -->
          <template v-if="demoEnabled">
            <button
              type="button"
              :disabled="isLoading"
              class="flex w-full cursor-pointer items-center justify-center gap-2 rounded-[11px] bg-green-700 px-6 py-3 font-semibold text-white transition hover:bg-green-800 disabled:cursor-not-allowed disabled:opacity-60"
              @click="handleDemoLogin"
            >
              {{ isLoading && loginProvider === 'demo' ? 'Signing in…' : 'Demo Login' }}
            </button>
            <p class="mt-2 text-center text-xs text-gray-500">
              Or sign in with the demo code: email
              <span class="font-medium text-gray-700">{{ demoEmail }}</span
              >, code <span class="font-medium text-gray-700">{{ demoCode }}</span>
            </p>

            <AuthDivider label="or" />
          </template>

          <GoogleLoginButton
            :loading="isLoading && loginProvider === 'google'"
            @click="handleGoogleLogin"
          />

          <AuthDivider label="or" />

          <OtpEmailForm :loading="otpLoading" :error="otpError" @submit="handleEmailSubmit" />
        </template>

        <!-- Step 2: enter the emailed code -->
        <template v-else>
          <OtpCodeForm
            :email="otpEmail"
            :loading="otpLoading"
            :error="otpError"
            :resend-cooldown="resendCooldown"
            @verify="handleVerify"
            @resend="handleResend"
            @back="handleBack"
          />
        </template>
      </AuthCard>
    </div>
  </div>
</template>

<script setup>
import { onUnmounted, ref } from 'vue'
import AuthCard from '~/components/auth/AuthCard.vue'
import AuthDivider from '~/components/auth/AuthDivider.vue'
import GoogleLoginButton from '~/components/auth/GoogleLoginButton.vue'
import OtpCodeForm from '~/components/auth/OtpCodeForm.vue'
import OtpEmailForm from '~/components/auth/OtpEmailForm.vue'
import { useNotify } from '~/composables/useNotify'
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()
const route = useRoute()
const notify = useNotify()

// Demo mode (portfolio): toggled via NUXT_PUBLIC_DEMO_ENABLED
const config = useRuntimeConfig()
const demoEnabled = config.public.demoEnabled
const demoEmail = config.public.demoEmail
const demoCode = config.public.demoCode

// Where to send the user after a successful login: the page the auth middleware
// bounced them from (?redirect=...), or home. Only allow same-origin relative
// paths so the query param can't be used as an open redirect.
const redirectTarget = () => {
  const target = route.query.redirect
  return typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')
    ? target
    : '/'
}

// Which step of the login flow is showing: 'email' (choose method) or 'code' (enter OTP)
const step = ref('email')

// Google OAuth
const isLoading = ref(false)
const loginProvider = ref(null)

const handleGoogleLogin = () => {
  isLoading.value = true
  loginProvider.value = 'google'
  authStore.clearError()
  authStore.loginWithGoogle()
}

const handleDemoLogin = async () => {
  if (isLoading.value) return
  isLoading.value = true
  loginProvider.value = 'demo'
  authStore.clearError()
  try {
    await authStore.demoLogin()
    notify.success('Logged in as demo user.')
    navigateTo(redirectTarget())
  } catch (err) {
    notify.error(err.message)
    isLoading.value = false
    loginProvider.value = null
  }
}

// Passwordless OTP
const otpEmail = ref('')
const otpLoading = ref(false)
const otpError = ref('')
const resendCooldown = ref(0)
let cooldownInterval = null

const startResendCooldown = () => {
  clearInterval(cooldownInterval)
  resendCooldown.value = 60
  cooldownInterval = setInterval(() => {
    resendCooldown.value--
    if (resendCooldown.value <= 0) clearInterval(cooldownInterval)
  }, 1000)
}

const handleEmailSubmit = async (emailValue) => {
  otpEmail.value = emailValue
  otpError.value = ''
  otpLoading.value = true
  try {
    await authStore.requestOtp(emailValue)
    step.value = 'code'
    startResendCooldown()
    notify.success('Check your email for the verification code.')
  } catch (err) {
    otpError.value = err.message
    notify.error(err.message)
  } finally {
    otpLoading.value = false
  }
}

const handleVerify = async (code) => {
  otpError.value = ''
  otpLoading.value = true
  try {
    await authStore.verifyOtp(otpEmail.value, code)
    notify.success('Logged in successfully.')
    navigateTo(redirectTarget())
  } catch (err) {
    otpError.value = err.message
    notify.error(err.message)
  } finally {
    otpLoading.value = false
  }
}

const handleResend = async () => {
  otpError.value = ''
  otpLoading.value = true
  try {
    await authStore.requestOtp(otpEmail.value)
    startResendCooldown()
    notify.success('A new code has been sent.')
  } catch (err) {
    otpError.value = err.message
    notify.error(err.message)
  } finally {
    otpLoading.value = false
  }
}

const handleBack = () => {
  otpError.value = ''
  step.value = 'email'
}

onUnmounted(() => clearInterval(cooldownInterval))

onMounted(() => {
  authStore.initAuth()
  authStore.clearError()

  if (authStore.isAuthenticated) {
    navigateTo(redirectTarget())
  }
})
</script>
