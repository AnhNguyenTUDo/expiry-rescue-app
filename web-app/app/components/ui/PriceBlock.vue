<template>
  <div class="flex flex-col gap-1">
    <span class="text-green-700 font-bold text-2xl">{{ formatPrice(sellingPrice) }}</span>
    <div class="flex items-center gap-2">
      <span class="line-through text-gray-400 text-base">{{ formatPrice(originalPrice) }}</span>
      <span class="text-green-700 text-sm font-semibold">({{ discount }})</span>
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
