<template>
  <NuxtLink
    :to="detailLink"
    class="flex h-full cursor-pointer flex-col rounded-xl bg-white p-5 transition hover:shadow-md"
  >
    <!-- Status and Category Header -->
    <div class="mb-3 flex items-start justify-between">
      <AvailabilityTag :availability="product.availability" />
      <span
        v-if="showCategory"
        class="rounded-[7px] border border-gray-400 px-3 py-1 py-1.5 text-xs font-semibold text-gray-700"
        >{{ product.category }}</span
      >
    </div>

    <!-- Product image  -->
    <div class="mb-3 flex justify-center py-10">
      <SvgIcon name="icon-products" class="h-30 w-30 text-gray-400" />
    </div>

    <h3 class="line-clamp-2 min-h-14 text-lg font-semibold">
      {{ product.name }}
    </h3>
    <p v-if="showLocation" class="mt-1 mb-3 text-sm text-gray-500">{{ product.location }}</p>

    <ExpiryBadge :expiry-date="product.earliestExpiryDate" />

    <div class="mt-auto pt-4">
      <PriceTag :original-price="product.originalPrice" :selling-price="product.sellingPrice" />
    </div>
  </NuxtLink>
</template>

<script setup>
import { computed } from 'vue'
import AvailabilityTag from '~/components/ui/AvailabilityTag.vue'
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import PriceTag from '~/components/ui/PriceTag.vue'

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  supermarketId: {
    type: String,
    required: true,
  },
  showLocation: {
    type: Boolean,
    default: false,
  },
  showCategory: {
    type: Boolean,
    default: false,
  },
})

const detailLink = computed(() => {
  const productMasterId = props.product.productMasterId || props.product.id
  return `/supermarkets/${props.supermarketId}/products/${productMasterId}`
})
</script>
