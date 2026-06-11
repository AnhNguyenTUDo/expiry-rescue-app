<template>
  <div class="flex min-h-[calc(100vh-15rem)] items-center justify-center p-4">
    <div class="w-full max-w-md">
      <AuthCard title="Expiry Rescue" subtitle="Eat well, waste less">
        <!-- Step 1: choose a method (Google or email) -->
        <template v-if="step === 'email'">
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
