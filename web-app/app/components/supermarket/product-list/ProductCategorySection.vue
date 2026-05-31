<template>
  <div class="mb-8">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-xl font-semibold text-green-700">
        {{ category.name }} ({{ category.products.length }})
      </h3>
    </div>

    <div v-if="category.products.length === 0" class="text-center py-8 bg-gray-50 rounded-lg">
      <p class="text-gray-500">No available products in this category</p>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <ProductCard
        v-for="product in visibleProducts"
        :key="product.id"
        :product="product"
        :supermarket-id="supermarketId"
      />
    </div>

    <ShowMoreButton
      v-if="category.products.length > 4 && !expanded"
      :remaining="category.products.length - 4"
      @click="emit('toggle')"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ProductCard from '@/components/supermarket/product-list/ProductCard.vue'
import ShowMoreButton from '~/components/ui/ShowMoreButton.vue'

const props = defineProps({
  category: {
    type: Object,
    required: true,
  },
  expanded: {
    type: Boolean,
    default: false,
  },
  supermarketId: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['toggle'])

const visibleProducts = computed(() =>
  props.expanded ? props.category.products : props.category.products.slice(0, 4)
)
</script>
