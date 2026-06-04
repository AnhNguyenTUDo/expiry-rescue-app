<template>
  <NuxtLink
    :to="detailLink"
    class="flex flex-col h-full cursor-pointer bg-white rounded-xl hover:shadow-md transition p-5"
  >
    <!-- Status and Category Header -->
    <div class="flex justify-between items-start mb-3">
      <AvailabilityTag :availability="product.availability" />
      <span
        v-if="showCategory"
        class="py-1.5 text-xs px-3 py-1 font-semibold text-gray-700 rounded-[7px] border border-gray-400"
        >{{ product.category }}</span
      >
    </div>

    <!-- Product image  -->
    <div class="flex justify-center mb-3 py-10">
      <SvgIcon name="icon-products" class="text-gray-400 w-30 h-30" />
    </div>

    <h3 class="text-lg font-semibold line-clamp-2 min-h-14">
      {{ product.name }}
    </h3>
    <p v-if="showLocation" class="text-gray-500 text-sm mb-3 mt-1">{{ product.location }}</p>

    <ExpiryBadge :expiry-date="product.earliestExpiryDate" />

    <div class="mt-auto pt-4">
      <PriceTag :original-price="product.originalPrice" :selling-price="product.sellingPrice" />
    </div>
  </NuxtLink>
</template>

<script setup>
import { computed } from 'vue'
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import AvailabilityTag from '~/components/ui/AvailabilityTag.vue'
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
