<template>
  <div
    @click="navigateToDetail"
    class="cursor-pointer bg-white rounded-xl shadow hover:shadow-lg transition p-5 border border-gray-200"
  >
    <!-- Status and Category Header -->
    <div class="flex justify-between items-start mb-3">
      <div
        class="text-xs px-3 py-1.5 rounded-[7px] font-semibold flex items-center gap-1"
        :class="{
          'bg-green-50 border border-green-200 text-green-700':
            product.availability === 'available',
          'bg-yellow-50 border border-yellow-400 text-yellow-600':
            product.availability === 'limited',
          'bg-red-50 border border-red-200 text-[#dc3545]': product.availability === 'out of stock',
        }"
      >
        <SvgIcon
          v-if="product.availability === 'available'"
          name="icon-checkmark"
          class="w-3 h-3"
        />
        <SvgIcon v-if="product.availability === 'limited'" name="icon-time" class="w-3 h-3" />
        <SvgIcon
          v-if="product.availability === 'out of stock'"
          name="icon-out-of-stock"
          class="w-3 h-3"
        />
        {{ product.availability.charAt(0).toUpperCase() + product.availability.slice(1) }}
      </div>
      <span
        class="py-1.5 text-xs px-3 py-1 font-semibold text-gray-700 rounded-[7px] border border-gray-400"
        >{{ product.category }}</span
      >
    </div>

    <!-- Product image  -->
    <div class="flex justify-center mb-3 py-10">
      <div class="text-5xl">
        <SvgIcon name="icon-products" class="text-gray-400 w-30 h-30" />
      </div>
    </div>

    <h3 class="text-lg font-semibold">{{ product.name }}</h3>
    <p class="text-gray-500 text-sm mb-3 mt-1">{{ product.location }}</p>

    <div class="text-sm bg-green-50 border border-green-200 rounded-md p-3 space-y-2">
      <div class="flex justify-between items-center">
        <span class="text-gray-500 font-semibold">Sell until:</span>
        <div class="flex items-center gap-2">
          <p class="text-green-700 font-semibold">{{ product.sellUntil }}</p>
          <span
            class="bg-green-700 text-white text-xs px-2 pt-px pb-0.75 rounded-sm font-semibold"
            >{{ product.sellDays }}</span
          >
        </div>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-gray-500 font-semibold">Expires:</span>
        <div class="flex items-center gap-2">
          <p class="text-green-700 font-semibold">{{ product.expire }}</p>
          <span
            class="bg-green-700 text-white text-xs px-2 pt-px pb-0.75 rounded-sm font-semibold"
            >{{ product.expireDays }}</span
          >
        </div>
      </div>
    </div>

    <div class="flex justify-between items-center mt-4">
      <div class="flex flex-col">
        <span class="line-through text-gray-400 text-sm">{{ product.oldPrice }}</span>
        <span class="text-green-700 font-bold text-2xl">{{ product.newPrice }}</span>
      </div>
      <span class="bg-[#da4150] text-white text-sm font-semibold px-3 py-1 rounded-md">{{
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
    required: true,
  },
})

const navigateToDetail = () => {
  const productMasterId = props.product.productMasterId || props.product.id
  navigateTo(`/supermarkets/${props.supermarketId}/products/${productMasterId}`)
}
</script>
