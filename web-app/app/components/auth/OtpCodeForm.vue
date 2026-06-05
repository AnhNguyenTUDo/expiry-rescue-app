<template>
  <form class="space-y-4" @submit.prevent="onVerify">
    <p class="text-center text-sm text-gray-500">
      Enter the 6-digit code sent to
      <span class="font-medium text-gray-900">{{ email }}</span>
    </p>

    <!-- 6-box OTP input -->
    <div class="flex justify-center gap-2">
      <input
        v-for="(_, i) in digits"
        :key="i"
        :ref="
          (el) => {
            if (el) inputRefs[i] = el
          }
        "
        v-model="digits[i]"
        type="text"
        inputmode="numeric"
        maxlength="1"
        :disabled="loading"
        class="h-12 w-10 rounded-xl border border-gray-300 text-center text-lg font-semibold text-gray-900 transition-colors focus:border-green-600 focus:ring focus:ring-green-600 focus:outline-none disabled:opacity-50"
        :class="{ 'border-red-500 focus:border-red-500 focus:ring-red-500/30': error }"
        @keydown="onKeydown($event, i)"
        @input="onInput($event, i)"
        @paste.prevent="onPaste($event)"
        @focus="$event.target.select()"
      />
    </div>

    <p v-if="error" class="text-center text-sm text-red-600">{{ error }}</p>

    <button
      type="submit"
      :disabled="loading || code.length < 6"
      class="w-full cursor-pointer rounded-xl bg-green-700 py-3.5 font-semibold text-white transition-colors hover:bg-green-800 disabled:cursor-not-allowed disabled:opacity-50"
    >
      {{ loading ? 'Verifying...' : 'Verify' }}
    </button>

    <div class="flex items-center justify-between text-sm">
      <button
        type="button"
        class="cursor-pointer text-gray-500 transition-colors hover:text-gray-700"
        @click="$emit('back')"
      >
        Use a different email
      </button>
      <button
        type="button"
        :disabled="resendCooldown > 0"
        class="cursor-pointer text-green-700 transition-colors hover:text-green-800 disabled:cursor-not-allowed disabled:text-gray-400"
        @click="$emit('resend')"
      >
        {{ resendCooldown > 0 ? `Resend in ${resendCooldown}s` : 'Resend code' }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { computed, ref } from 'vue'

defineProps({
  email: { type: String, default: '' },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
  resendCooldown: { type: Number, default: 0 },
})

const emit = defineEmits(['verify', 'resend', 'back'])

const digits = ref(['', '', '', '', '', ''])
const inputRefs = ref([])

const code = computed(() => digits.value.join(''))

const focusAt = (i) => {
  inputRefs.value[i]?.focus()
}

const onInput = (event, i) => {
  const val = event.target.value.replace(/\D/g, '')
  digits.value[i] = val.slice(-1)
  if (val && i < 5) focusAt(i + 1)
}

const onKeydown = (event, i) => {
  if (event.key === 'Backspace') {
    if (digits.value[i]) {
      digits.value[i] = ''
    } else if (i > 0) {
      digits.value[i - 1] = ''
      focusAt(i - 1)
    }
  } else if (event.key === 'ArrowLeft' && i > 0) {
    focusAt(i - 1)
  } else if (event.key === 'ArrowRight' && i < 5) {
    focusAt(i + 1)
  }
}

const onPaste = (event) => {
  const pasted = event.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
  pasted.split('').forEach((char, i) => {
    digits.value[i] = char
  })
  focusAt(Math.min(pasted.length, 5))
}

const onVerify = () => {
  if (code.value.length < 6) return
  emit('verify', code.value)
}
</script>
