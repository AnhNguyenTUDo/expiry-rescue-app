<template>
  <div class="flex justify-between items-center">
    <div class="flex flex-col">
      <span class="line-through text-gray-400" :class="size === 'lg' ? 'text-base' : 'text-sm'">
        {{ formatPrice(originalPrice) }}
      </span>
      <span class="text-green-700 font-bold" :class="size === 'lg' ? 'text-3xl' : 'text-2xl'">
        {{ formatPrice(sellingPrice) }}
      </span>
    </div>
    <span class="bg-green-700 text-white text-sm font-semibold px-3 py-1 rounded-md">
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
  // 'sm' matches the compact ProductCard style; 'lg' is for the product detail buy box
  size: {
    type: String,
    default: 'sm',
  },
})

const discount = computed(() => calculateDiscount(props.originalPrice, props.sellingPrice))
</script>
