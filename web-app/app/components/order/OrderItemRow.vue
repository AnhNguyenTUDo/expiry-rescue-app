<template>
  <div class="flex items-start gap-3 py-4">
    <NuxtLink
      :to="productLink"
      aria-hidden="true"
      tabindex="-1"
      class="flex h-25 w-25 shrink-0 items-center justify-center rounded-[8px] border border-gray-200 bg-gray-100"
    >
      <SvgIcon name="icon-products" class="h-12 w-12 text-gray-300" />
    </NuxtLink>
    <div class="flex flex-1 items-baseline gap-4">
      <!-- Product name, expiry, unit price -->
      <div class="flex-1">
        <h3 class="text-lg font-semibold">
          <NuxtLink :to="productLink" class="text-gray-800 hover:text-green-700 hover:underline">
            {{ item.productName }}
          </NuxtLink>
        </h3>
        <p class="mt-0.5 text-sm text-gray-500">Expires: {{ formatDate(item.expiryDate) }}</p>
        <PriceBlock
          :original-price="item.originalPrice ?? 0"
          :selling-price="item.price ?? 0"
          size="sm"
          tone="neutral"
          class="mt-1"
        />
      </div>
      <!-- Quantity -->
      <p class="w-28 shrink-0 text-sm text-gray-600">Quantity: {{ item.quantity }}</p>
      <!-- Total -->
      <p class="w-24 shrink-0 text-right text-lg font-bold">
        {{ formatPrice(item.subtotal ?? 0) }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import PriceBlock from '~/components/ui/PriceBlock.vue'
import { formatDate } from '~/utils/date'
import { formatPrice } from '~/utils/price'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
})

const productLink = computed(
  () => `/supermarkets/${props.item.supermarketId}/products/${props.item.productMasterId}`
)
</script>
