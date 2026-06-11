<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-gray-900/50"></div>

    <div class="relative w-full max-w-md p-4">
      <div ref="panel" class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <h3 class="text-lg font-semibold text-gray-900">{{ title }}</h3>
        <p v-if="message" class="mt-2 text-sm text-gray-500">{{ message }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <button
            type="button"
            class="cursor-pointer rounded-[10px] border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100"
            @click="emit('cancel')"
          >
            {{ cancelLabel }}
          </button>
          <button
            type="button"
            class="cursor-pointer rounded-[10px] px-4 py-2 text-sm font-medium text-white transition-colors"
            :class="
              variant === 'danger'
                ? 'bg-red-700 hover:bg-red-800'
                : 'bg-green-700 hover:bg-green-800'
            "
            @click="emit('confirm')"
          >
            {{ confirmLabel }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useClickOutside } from '~/composables/useClickOutside'

const props = defineProps({
  show: { type: Boolean, required: true },
  title: { type: String, required: true },
  message: { type: String, default: '' },
  confirmLabel: { type: String, default: 'Confirm' },
  cancelLabel: { type: String, default: 'Cancel' },
  // 'default' = green confirm button; 'danger' = red confirm button
  variant: {
    type: String,
    default: 'default',
    validator: (v) => ['default', 'danger'].includes(v),
  },
})

const emit = defineEmits(['confirm', 'cancel'])

// Close on backdrop click (anything outside the panel) and on Escape
const panel = ref(null)
useClickOutside(panel, () => {
  if (props.show) emit('cancel')
})

const onKeydown = (e) => {
  if (e.key === 'Escape' && props.show) emit('cancel')
}
onMounted(() => document.addEventListener('keydown', onKeydown))
onUnmounted(() => document.removeEventListener('keydown', onKeydown))
</script>
