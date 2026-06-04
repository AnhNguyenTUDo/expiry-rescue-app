<template>
  <div class="rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
    <p><strong>Error:</strong> {{ error }}</p>
    <button v-if="hasRetry" class="mt-2 text-sm underline" @click="emit('retry')">Retry</button>
    <slot />
  </div>
</template>

<script setup>
import { computed, useAttrs } from 'vue'

defineProps({
  error: { type: String, required: true },
})

const emit = defineEmits(['retry'])
const attrs = useAttrs()
const hasRetry = computed(() => !!attrs.onRetry)
</script>
