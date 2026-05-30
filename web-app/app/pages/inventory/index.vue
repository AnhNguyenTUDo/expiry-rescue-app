<template>
  <div>
    <h1 class="text-3xl font-bold mb-6">Product Inventory</h1>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-8">
      <p class="text-gray-600">Loading inventories...</p>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4"
    >
      <p><strong>Error:</strong> {{ error }}</p>
    </div>

    <!-- Success State - Display Inventories -->
    <div v-else>
      <div class="mb-4 flex justify-between items-center">
        <p class="text-gray-600">Total Items: {{ inventories.length }}</p>
        <button
          @click="fetchInventories"
          class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
        >
          Refresh
        </button>
      </div>

      <!-- Inventory Grid -->
      <div
        v-if="inventories.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
      >
        <div
          v-for="item in inventories"
          :key="item.id"
          class="bg-white border border-gray-200 rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow"
        >
          <div class="flex justify-between items-start mb-2">
            <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
            <span
              :class="[
                'px-2 py-1 text-xs rounded-full',
                item.status === 'AVAILABLE'
                  ? 'bg-green-100 text-green-800'
                  : 'bg-red-100 text-red-800',
              ]"
            >
              {{ item.status }}
            </span>
          </div>

          <p class="text-sm text-gray-600 mb-2">
            <strong>Supermarket:</strong> {{ item.supermarketName }}
          </p>

          <div class="space-y-1 mb-3">
            <p class="text-sm">
              <span class="text-gray-600">Original Price:</span>
              <span class="font-medium">${{ item.originalPrice }}</span>
            </p>
            <p class="text-sm">
              <span class="text-gray-600">Selling Price:</span>
              <span class="font-medium text-green-600">${{ item.sellingPrice }}</span>
            </p>
            <p class="text-sm">
              <span class="text-gray-600">Quantity:</span>
              <span class="font-medium">{{ item.quantityAvailable }}</span>
            </p>
            <p class="text-sm">
              <span class="text-gray-600">Expires:</span>
              <span class="font-medium">{{ formatDate(item.expiryDate) }}</span>
            </p>
          </div>

          <div class="text-xs text-gray-500 pt-2 border-t">
            <p>Created: {{ formatDate(item.createdAt) }}</p>
            <p v-if="item.createdByName">By: {{ item.createdByName }}</p>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-12 bg-gray-50 rounded-lg">
        <p class="text-gray-500 text-lg">No inventories found</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProductInventory } from '~/composables/useProductInventory'

// State
const inventories = ref([])
const loading = ref(false)
const error = ref(null)

// Get API methods
const { getAllInventories } = useProductInventory()

/**
 * Fetch all inventories from API
 */
const fetchInventories = async () => {
  loading.value = true
  error.value = null

  try {
    const response = await getAllInventories()

    // Extract data from API response
    if (response.success && response.data) {
      inventories.value = response.data
      console.log('Inventories loaded:', inventories.value)
    } else {
      inventories.value = []
    }
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Failed to load inventories'
    console.error('Error fetching inventories:', err)
  } finally {
    loading.value = false
  }
}

// Shared helpers
import { formatDate } from '~/utils/date'

// Fetch inventories on component mount
onMounted(() => {
  fetchInventories()
})
</script>

<style scoped>
/* Additional custom styles can be added here */
</style>
