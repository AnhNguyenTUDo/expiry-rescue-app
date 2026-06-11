<template>
  <div class="rounded-xl bg-white p-12 text-center shadow">
    <SvgIcon
      name="icon-exclamation-square-filled"
      class="mx-auto mb-4 block h-20 w-20 text-gray-400"
    />
    <h2 class="mb-2 text-2xl font-semibold text-gray-700">{{ title }}</h2>
    <p class="mb-6 text-gray-500">{{ message }}</p>

    <button
      v-if="onRetry"
      type="button"
      class="inline-block cursor-pointer rounded-[11px] bg-green-700 px-6 py-3 font-semibold text-white transition hover:bg-green-800"
      @click="onRetry"
    >
      Try again
    </button>

    <slot />
  </div>
</template>

<script setup>
import { computed, useAttrs } from 'vue'

defineProps({
  title: { type: String, default: 'Something went wrong' },
  message: {
    type: String,
    default: "We couldn't load this right now. Please try again in a moment.",
  },
})

// Show the retry button only when the parent attaches a @retry handler.
const attrs = useAttrs()
const onRetry = computed(() => attrs.onRetry)
</script>
