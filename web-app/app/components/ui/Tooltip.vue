<template>
  <!-- Flowbite Tooltip Element -->
  <div
    v-if="content && active"
    :id="id"
    role="tooltip"
    class="absolute z-50 invisible inline-block px-3 py-2 text-xs font-medium text-white transition-opacity duration-300 bg-gray-700 rounded-lg shadow-sm opacity-0 tooltip"
  >
    {{ content }}
    <div class="tooltip-arrow" data-popper-arrow></div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, watch } from 'vue'
import { initTooltips } from 'flowbite'
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  content: {
    type: String,
    default: '',
  },
  active: {
    type: Boolean,
    default: true,
  },
})

onMounted(() => {
  if (props.content && props.active) {
    initTooltips()
  }
})

// Watch active state to initialize the tooltip when it dynamically becomes active
watch(
  () => props.active,
  async (isActive) => {
    if (isActive && props.content) {
      await nextTick()
      initTooltips()
    }
  }
)
</script>
