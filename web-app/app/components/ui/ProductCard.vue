<template>
  <div
    @click="navigateToDetail"
    class="cursor-pointer bg-white rounded-xl shadow hover:shadow-lg transition p-5 border border-gray-200"
  >
    <!-- Status and Category Header -->
    <div class="flex justify-between items-start mb-3">
      <div
        class="text-xs px-3 py-1.5 rounded-full font-semibold flex items-center gap-1"
        :class="{
          'bg-green-700 text-white': product.availability === 'available',
          'bg-yellow-500 text-black': product.availability === 'limited',
          'bg-[#dc3545] text-white text-white': product.availability === 'out of stock',
        }"
      >
        <span v-if="product.availability === 'available'">✓</span>
        <span v-else-if="product.availability === 'limited'">⚠</span>
        <span v-else-if="product.availability === 'out of stock'">✕</span>
        {{ product.availability }}
      </div>
      <span
        class="py-1.5 text-xs px-3 py-1 font-semibold text-gray-700 rounded-full border border-gray-400"
        >{{ product.category }}</span
      >
    </div>

    <!-- Product Emoji (centered) -->
    <div class="flex justify-center mb-3 py-10">
      <div class="text-5xl">{{ product.emoji }}</div>
    </div>

    <h3 class="text-lg font-semibold">{{ product.name }}</h3>
    <p class="text-gray-500 text-sm mb-3 mt-1">{{ product.location }}</p>

    <div class="text-sm bg-green-50 border border-green-200 rounded-lg p-3 space-y-2">
      <div class="flex justify-between items-center">
        <span class="text-gray-500 font-semibold">Sell until:</span>
        <div class="flex items-center gap-2">
          <p class="text-green-700 font-semibold">{{ product.sellUntil }}</p>
          <span class="bg-green-700 text-white text-xs px-2 py-0.5 rounded-sm font-semibold">{{
            product.sellDays
          }}</span>
        </div>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-gray-500 font-semibold">Expires:</span>
        <div class="flex items-center gap-2">
          <p class="text-green-700 font-semibold">{{ product.expire }}</p>
          <span class="bg-green-700 text-white text-xs px-2 py-0.5 rounded-sm font-semibold">{{
            product.expireDays
          }}</span>
        </div>
      </div>
    </div>

    <div class="flex justify-between items-center mt-4">
      <div class="flex flex-col">
        <span class="line-through text-gray-400 text-sm">{{ product.oldPrice }}</span>
        <span class="text-green-700 font-bold text-2xl">{{ product.newPrice }}</span>
      </div>
      <span class="bg-[#dc3545] text-white text-sm font-semibold px-3 py-1 rounded-sm">{{
        product.discount
      }}</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  supermarketId: {
    type: String,
    required: false,
  },
})

const navigateToDetail = () => {
  // Navigate to product detail page with new URL structure
  const productMasterId = props.product.productMasterId || props.product.id
  const targetUrl = props.supermarketId
    ? `/supermarkets/${props.supermarketId}/products/${productMasterId}`
    : `/products/${productMasterId}`

  navigateTo(targetUrl)
}
</script>
