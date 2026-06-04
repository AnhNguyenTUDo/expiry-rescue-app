<template>
  <!-- Flowbite Tooltip Element -->
  <div
    v-if="content && active"
    :id="id"
    role="tooltip"
    class="tooltip invisible absolute z-50 inline-block rounded-lg bg-gray-700 px-3 py-2 text-xs font-medium text-white opacity-0 shadow-sm transition-opacity duration-300"
  >
    {{ content }}
    <div class="tooltip-arrow" data-popper-arrow></div>
  </div>
</template>

<script setup>
import { initTooltips } from 'flowbite'
import { nextTick, onMounted, watch } from 'vue'
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
