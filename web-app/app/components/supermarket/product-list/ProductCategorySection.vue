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

    <div v-if="category.products.length > 4 && !expanded" class="text-center mt-4">
      <button
        class="px-4 py-2 rounded-lg border transition bg-green-600 text-white hover:bg-green-700"
        @click="emit('toggle')"
      >
        Show More ({{ category.products.length - 4 }} more)
      </button>
    </div>

    <div v-if="expanded" class="text-center mt-4">
      <button
        class="px-4 py-2 rounded-lg border transition bg-gray-500 text-white hover:bg-gray-600"
        @click="emit('toggle')"
      >
        Show Less
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ProductCard from '@/components/ui/ProductCard.vue'

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
