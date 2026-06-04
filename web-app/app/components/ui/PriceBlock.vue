<template>
  <div class="flex flex-col gap-1">
    <span class="text-2xl font-bold text-green-700">{{ formatPrice(sellingPrice) }}</span>
    <div class="flex items-center gap-2">
      <span class="text-base text-gray-400 line-through">{{ formatPrice(originalPrice) }}</span>
      <span class="text-sm font-semibold text-green-700">({{ discount }})</span>
    </div>
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
