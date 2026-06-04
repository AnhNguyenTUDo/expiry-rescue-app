<template>
  <div
    class="flex items-center gap-1.5 rounded-[7px] font-semibold"
    :class="[config.class, size === 'lg' ? 'px-2.5 py-1.5 text-sm' : 'px-2.5 py-1.5 text-xs']"
  >
    <SvgIcon :name="config.icon" :class="size === 'lg' ? 'h-4 w-4' : 'h-3 w-3'" />
    {{ label }}
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 'available' | 'limited' | 'out of stock' (see ~/utils/product getAvailability)
  availability: {
    type: String,
    required: true,
  },
  // 'sm' matches the compact ProductCard badge; 'lg' is for the product detail buy box
  size: {
    type: String,
    default: 'sm',
  },
})

const config = computed(() => {
  switch (props.availability) {
    case 'available':
      return {
        class: 'bg-green-50 border border-green-200 text-green-700',
        icon: 'icon-check-circle',
      }
    case 'limited':
      return { class: 'bg-yellow-50 border border-yellow-400 text-yellow-600', icon: 'icon-time' }
    default:
      return { class: 'bg-red-50 border border-red-200 text-[#dc3545]', icon: 'icon-out-of-stock' }
  }
})

const label = computed(
  () => props.availability.charAt(0).toUpperCase() + props.availability.slice(1)
)
</script>
