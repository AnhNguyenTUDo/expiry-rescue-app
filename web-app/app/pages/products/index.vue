<template>
  <div>
    <!-- Controls -->
    <div class="bg-white p-6 rounded-xl shadow flex flex-wrap gap-4 items-center">
      <div class="flex flex-col md:flex-row gap-2 md:items-center">
        <label for="category" class="font-semibold">Category:</label>
        <select
          id="category"
          class="border border-gray-300 rounded px-3 py-2 focus:ring-green-600 focus:border-green-600"
        >
          <option value="all">All Categories</option>
          <option value="food">Food Products</option>
          <option value="spices">Spices</option>
          <option value="beverages">Beverages</option>
          <option value="cosmetics">Cosmetics</option>
          <option value="dairy">Dairy</option>
          <option value="bakery">Bakery</option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading products...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mt-8"
    >
      <p><strong>Error:</strong> {{ error }}</p>
      <button @click="fetchProducts" class="mt-2 text-sm underline">Retry</button>
    </div>

    <!-- Products Grid -->
    <div
      v-else-if="filteredProducts.length > 0"
      class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-8"
    >
      <ProductCard v-for="(p, i) in filteredProducts" :key="p.id || i" :product="p" />
    </div>

    <!-- Empty State -->
    <div v-else class="text-center py-12 bg-white rounded-lg shadow mt-8">
      <p class="text-gray-500 text-lg">No products available</p>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center items-center gap-2 mt-12">
      <button class="btn btn-outline" disabled>← Previous</button>
      <button class="btn bg-green-600 text-white">1</button>
      <button class="btn">2</button>
      <button class="btn">3</button>
      <button class="btn">4</button>
      <button class="btn btn-outline">Next →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import ProductCard from '@/components/ui/ProductCard.vue'
import ProductInventoryService from '~/services/product-inventory.service'

// State
const products = ref([])
const loading = ref(false)
const error = ref(null)
const selectedCategory = ref('all')

// Fetch products from API
const fetchProducts = async () => {
  loading.value = true
  error.value = null

  try {
    const response = await ProductInventoryService.getAllProductInventory((err) => {
      console.error('Error fetching products:', err)
      error.value = err.response?.data?.message || 'Failed to load products'
    })

    if (response && response.data) {
      // Group products by product_master_id
      const productMap = new Map()

      for (const item of response.data) {
        const key = item.productMasterId

        if (productMap.has(key)) {
          // Product master already exists, combine quantities
          const existingProduct = productMap.get(key)
          existingProduct.quantityAvailable += item.quantityAvailable

          // Use the earliest expiry date
          if (item.expiryDate < existingProduct.expiryTimestamp) {
            existingProduct.expiryTimestamp = item.expiryDate
            existingProduct.sellUntil = formatDate(item.expiryDate)
            existingProduct.expire = formatDate(item.expiryDate)
            existingProduct.sellDays = calculateDaysUntil(item.expiryDate)
            existingProduct.expireDays = calculateDaysUntil(item.expiryDate)
          }

          // Update availability
          existingProduct.availability = calculateAvailability(
            existingProduct.expiryTimestamp,
            existingProduct.quantityAvailable,
            'AVAILABLE'
          )
        } else {
          // First occurrence of this product master
          productMap.set(key, {
            id: item.id,
            emoji: '🛒',
            category: item.categoryName || item.productName,
            name: item.productName,
            location: item.supermarketName,
            sellUntil: formatDate(item.expiryDate),
            expire: formatDate(item.expiryDate),
            sellDays: calculateDaysUntil(item.expiryDate),
            expireDays: calculateDaysUntil(item.expiryDate),
            oldPrice: `${item.originalPrice.toLocaleString()}₫`,
            newPrice: `${item.sellingPrice.toLocaleString()}₫`,
            discount: calculateDiscount(item.originalPrice, item.sellingPrice),
            availability: calculateAvailability(
              item.expiryDate,
              item.quantityAvailable,
              item.status
            ),
            quantityAvailable: item.quantityAvailable,
            productMasterId: item.productMasterId,
            supermarketId: item.supermarketId,
            expiryTimestamp: item.expiryDate,
            createdAt: item.createdAt,
            updatedAt: item.updatedAt,
          })
        }
      }

      // Convert map to array
      products.value = Array.from(productMap.values())
    }
  } catch (err) {
    console.error('Error:', err)
    error.value = 'Failed to load products'
  } finally {
    loading.value = false
  }
}

// Shared helpers
import { formatDate, calculateDaysUntil } from '~/utils/date'
import { calculateDiscount } from '~/utils/price'
import { calculateAvailability } from '~/utils/product'

// Filter products by category (can be implemented later)
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value
  }
  return products.value.filter((p) =>
    p.category.toLowerCase().includes(selectedCategory.value.toLowerCase())
  )
})

// Load products on mount
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-4 py-2 rounded-lg border transition;
}
.btn-outline {
  @apply border-gray-300 hover:bg-green-600 hover:text-white hover:border-green-600;
}
</style>
