<template>
  <div class="flex items-center justify-between">
    <div class="flex flex-col">
      <span class="text-sm text-gray-400 line-through">{{ formatPrice(originalPrice) }}</span>
      <span class="text-2xl font-bold text-green-700">{{ formatPrice(sellingPrice) }}</span>
    </div>
    <span class="rounded-md bg-green-700 px-3 py-1 text-sm font-semibold text-white">
      {{ discount }}
    </span>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { formatPrice, calculateDiscount } from '~/utils/price'

const props = defineProps({
  originalPrice: {
    type: Number,
    required: true,
  },
  sellingPrice: {
    type: Number,
    required: true,
  },
})

const discount = computed(() => calculateDiscount(props.originalPrice, props.sellingPrice))
</script>
