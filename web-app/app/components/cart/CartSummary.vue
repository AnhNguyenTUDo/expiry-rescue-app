<template>
  <div class="sticky top-6 rounded-xl bg-white p-6">
    <h2 class="mb-4 text-lg font-semibold text-gray-800">Summary</h2>

    <div class="mb-6 space-y-2">
      <div class="flex items-center justify-between text-sm text-gray-600">
        <span>Items</span>
        <span class="font-semibold">{{ totalItems }}</span>
      </div>
      <div
        v-if="totalSavings > 0"
        class="flex items-center justify-between text-sm font-medium text-green-700"
      >
        <span>You save</span>
        <span>−{{ formatPrice(totalSavings) }}</span>
      </div>
      <div class="border-t border-gray-200 pt-2">
        <div class="flex items-center justify-between">
          <span class="font-semibold text-gray-700">Total</span>
          <span class="text-xl font-bold text-green-700">{{ formatPrice(totalPrice) }}</span>
        </div>
      </div>
    </div>

    <button
      :disabled="checkoutDisabled"
      class="w-full rounded-[11px] px-6 py-3 text-lg font-semibold transition"
      :class="
        checkoutDisabled
          ? 'cursor-not-allowed bg-gray-300 text-gray-500'
          : 'cursor-pointer bg-green-700 text-white hover:bg-green-800'
      "
      @click="emit('checkout')"
    >
      Checkout
    </button>
  </div>
</template>

<script setup>
import { formatPrice } from '~/utils/price'

defineProps({
  totalItems: { type: Number, required: true },
  totalPrice: { type: Number, required: true },
  totalSavings: { type: Number, default: 0 },
  checkoutDisabled: { type: Boolean, default: false },
})

const emit = defineEmits(['checkout'])
</script>
