<template>
  <div>
    <h1 class="mb-6 text-3xl font-bold">Product Inventory</h1>

    <LoadingState v-if="loading" message="Loading inventories..." />
    <ErrorAlert v-else-if="error" :error="error" class="mb-4" />

    <!-- Success State - Display Inventories -->
    <div v-else>
      <div class="mb-4 flex items-center justify-between">
        <p class="text-gray-600">Total Items: {{ inventories.length }}</p>
        <button
          class="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
          @click="fetchInventories"
        >
          Refresh
        </button>
      </div>

      <!-- Inventory Grid -->
      <div
        v-if="inventories.length > 0"
        class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3"
      >
        <div
          v-for="item in inventories"
          :key="item.id"
          class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm transition-shadow hover:shadow-md"
        >
          <div class="mb-2 flex items-start justify-between">
            <h3 class="text-lg font-semibold text-gray-800">{{ item.productName }}</h3>
            <span
              :class="[
                'rounded-full px-2 py-1 text-xs',
                item.status === 'AVAILABLE'
                  ? 'bg-green-100 text-green-800'
                  : 'bg-red-100 text-red-800',
              ]"
            >
              {{ item.status }}
            </span>
          </div>

          <p class="mb-2 text-sm text-gray-600">
            <strong>Supermarket:</strong> {{ item.supermarketName }}
          </p>

          <div class="mb-3 space-y-1">
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

          <div class="border-t pt-2 text-xs text-gray-500">
            <p>Created: {{ formatDate(item.createdAt) }}</p>
            <p v-if="item.createdByName">By: {{ item.createdByName }}</p>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="rounded-lg bg-gray-50 py-12 text-center">
        <p class="text-lg text-gray-500">No inventories found</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import { useProductInventory } from '~/composables/useProductInventory'
import { formatDate } from '~/utils/date'

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

// Fetch inventories on component mount
onMounted(() => {
  fetchInventories()
})
</script>

<style scoped>
/* Additional custom styles can be added here */
</style>
