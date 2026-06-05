<template>
  <form class="space-y-4" novalidate @submit.prevent="onSubmit">
    <div>
      <label for="otp-email" class="mb-2 block text-sm font-medium text-gray-700"> Email </label>
      <input
        id="otp-email"
        v-model="email"
        type="email"
        autocomplete="email"
        placeholder="you@example.com"
        :disabled="loading"
        class="w-full rounded-xl border px-4 py-3 text-gray-900 placeholder-gray-400 transition-colors focus:outline-none disabled:opacity-50"
        :class="
          validationError
            ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
            : 'border-gray-300 focus:border-green-600 focus:ring-green-600'
        "
        @input="clearValidation"
      />
      <p v-if="validationError" class="mt-2 text-sm text-red-600">{{ validationError }}</p>
      <p v-else class="mt-2 text-xs text-gray-500">
        We'll email you a 6-digit code to sign in. No password needed.
      </p>
    </div>

    <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

    <button
      type="submit"
      :disabled="loading || !email"
      class="w-full cursor-pointer rounded-xl bg-green-700 py-3.5 font-semibold text-white transition-colors hover:bg-green-800 disabled:cursor-not-allowed disabled:opacity-50"
    >
      {{ loading ? 'Sending...' : 'Send code' }}
    </button>
  </form>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
  error: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['submit'])

const email = ref('')
const validationError = ref('')

// email shape check (x@y.z)
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const onSubmit = () => {
  if (!EMAIL_RE.test(email.value.trim())) {
    validationError.value = 'Please enter a valid email address.'
    return
  }
  validationError.value = ''
  emit('submit', email.value.trim())
}

// Clear the validation error as soon as the user starts correcting the input
const clearValidation = () => {
  if (validationError.value) {
    validationError.value = ''
  }
}
</script>
