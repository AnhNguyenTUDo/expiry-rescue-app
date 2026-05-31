<template>
  <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
    <p><strong>Error:</strong> {{ error }}</p>
    <button v-if="hasRetry" @click="emit('retry')" class="mt-2 text-sm underline">Retry</button>
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
