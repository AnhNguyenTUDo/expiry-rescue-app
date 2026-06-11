<template>
  <div class="flex flex-col gap-0.5">
    <span
      class="font-bold"
      :class="[sizeClasses.selling, tone === 'neutral' ? 'text-gray-800' : 'text-green-700']"
    >
      {{ formatPrice(sellingPrice) }}
    </span>
    <div class="flex items-center gap-2">
      <span class="text-gray-400 line-through" :class="sizeClasses.original">
        {{ formatPrice(originalPrice) }}
      </span>
      <span class="font-semibold text-green-700" :class="sizeClasses.discount">
        ({{ discount }})
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { calculateDiscount, formatPrice } from '~/utils/price'

const props = defineProps({
  originalPrice: {
    type: Number,
    required: true,
  },
  sellingPrice: {
    type: Number,
    required: true,
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md'].includes(v),
  },
  // 'deal' = green selling price (browsing/cart); 'neutral' = black selling price
  tone: {
    type: String,
    default: 'deal',
    validator: (v) => ['deal', 'neutral'].includes(v),
  },
})

const SIZES = {
  sm: { selling: 'text-md font-semibold', original: 'text-sm', discount: 'text-xs' },
  md: { selling: 'text-2xl', original: 'text-base', discount: 'text-sm' },
}

const sizeClasses = computed(() => SIZES[props.size])

const discount = computed(() => calculateDiscount(props.originalPrice, props.sellingPrice))
</script>
