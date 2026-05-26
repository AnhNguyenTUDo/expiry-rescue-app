<template>
  <div>
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading product details...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p><strong>Error:</strong> {{ error }}</p>
    </div>

    <!-- Product Details -->
    <div v-else-if="currentItem">
      <!-- Product Header with Emoji taking 1/3 width -->
      <div class="bg-white p-8 rounded-xl shadow mb-6">
        <div class="flex gap-8">
          <!-- Product Emoji - 1/3 width -->
          <div class="w-1/3 flex items-center justify-center">
            <div class="text-9xl">{{ productEmoji }}</div>
          </div>

          <!-- Product Info - 2/3 width -->
          <div class="w-2/3">
            <h1 class="text-4xl font-bold text-gray-800 mb-2">{{ productName }}</h1>
            <p class="text-xl text-gray-600 mb-4">{{ categoryName }}</p>

            <!-- Description -->
            <div v-if="productDescription" class="mb-6">
              <h3 class="text-lg font-semibold text-gray-700 mb-2">Description</h3>
              <p class="text-gray-600">{{ productDescription }}</p>
            </div>

            <!-- Pricing Information -->
            <div class="bg-gray-50 p-4 rounded-lg mb-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <span class="text-sm text-gray-600">Original Price:</span>
                  <p class="text-lg line-through text-gray-400">
                    {{ currentItem.originalPrice.toLocaleString() }}₫
                  </p>
                </div>
                <div>
                  <span class="text-sm text-gray-600">Sale Price:</span>
                  <p class="text-2xl font-bold text-green-700">
                    {{ currentItem.sellingPrice.toLocaleString() }}₫
                  </p>
                </div>
                <div>
                  <span class="text-sm text-gray-600">Discount:</span>
                  <p class="text-lg font-semibold text-red-600">
                    {{ calculateDiscount(currentItem.originalPrice, currentItem.sellingPrice) }}
                  </p>
                </div>
                <div>
                  <span class="text-sm text-gray-600">Available Units:</span>
                  <p class="text-lg font-semibold text-gray-800">
                    {{ currentItem.quantityAvailable }} units
                  </p>
                </div>
              </div>
            </div>

            <!-- Expiry Information -->
            <div class="bg-green-50 border border-green-200 rounded-lg p-4 mb-4">
              <div class="flex justify-between items-center">
                <div>
                  <span class="text-sm text-gray-600 font-semibold">Expires:</span>
                  <p class="text-lg text-green-700 font-semibold">
                    {{ formatDate(currentItem.expiryDate) }}
                  </p>
                </div>
                <div class="flex items-center gap-3">
                  <span class="bg-green-700 text-white text-sm px-3 py-1.5 rounded font-semibold">
                    {{ calculateDaysUntil(currentItem.expiryDate) }}
                  </span>
                  <div
                    class="text-sm px-4 py-2 rounded-full font-semibold"
                    :class="{
                      'bg-green-700 text-white': getAvailability(currentItem) === 'available',
                      'bg-yellow-500 text-black': getAvailability(currentItem) === 'limited',
                      'bg-red-600 text-white': getAvailability(currentItem) === 'out of stock',
                    }"
                  >
                    {{ getAvailability(currentItem) }}
                  </div>
                </div>
              </div>
            </div>

            <!-- Other Inventory Items Dropdown (if multiple items exist in same supermarket) -->
            <div v-if="otherInventoryItems.length > 0" class="mb-4">
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Other batches of this product at {{ supermarketName }}:
              </label>
              <select
                v-model="selectedInventoryItemId"
                @change="onInventoryItemChange"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-green-600 focus:border-green-600"
              >
                <option v-for="item in allSupermarketItems" :key="item.id" :value="item.id">
                  Batch - Expires: {{ formatDate(item.expiryDate) }} -
                  {{ item.quantityAvailable }} units - {{ item.sellingPrice.toLocaleString() }}₫
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Supermarket Information -->
      <div class="bg-white p-6 rounded-xl shadow mb-6">
        <h2 class="text-2xl font-bold mb-4">Available at {{ supermarketName }}</h2>
        <button
          @click="navigateToSupermarket(currentItem.supermarketId)"
          class="btn bg-green-600 text-white hover:bg-green-700"
        >
          View all products at this supermarket
        </button>
      </div>

      <!-- Other Locations (if product available at other supermarkets) -->
      <div v-if="otherLocations.length > 0" class="bg-white p-6 rounded-xl shadow mb-6">
        <h2 class="text-2xl font-bold mb-4">
          Also available at {{ otherLocations.length }} other location(s)
        </h2>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div
            v-for="location in otherLocations"
            :key="location.supermarketId"
            class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition cursor-pointer"
            @click="navigateToProductAtSupermarket(location.supermarketId)"
          >
            <h3 class="text-lg font-semibold text-gray-800 mb-2">
              {{ location.supermarketName }}
            </h3>
            <div class="space-y-1 text-sm">
              <p class="text-gray-600">
                <span class="font-medium">Total Units:</span> {{ location.totalQuantity }}
              </p>
              <p class="text-gray-600">
                <span class="font-medium">Best Price:</span>
                {{ location.bestPrice.toLocaleString() }}₫
              </p>
              <p class="text-gray-600">
                <span class="font-medium">Expires:</span>
                {{ formatDate(location.earliestExpiry) }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Back Button -->
      <div class="text-center">
        <button @click="goBack" class="btn bg-gray-500 text-white hover:bg-gray-600">← Back</button>
      </div>
    </div>

    <!-- No Data State -->
    <div v-else class="text-center py-12 bg-white rounded-lg shadow">
      <p class="text-gray-500 text-lg">No product information available</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ProductInventoryService from '~/services/product-inventory.service'
import { useSupermarketStore } from '~/stores/supermarket'

const route = useRoute()
const router = useRouter()
const supermarketStore = useSupermarketStore()

const productMasterId = route.params.id
const supermarketIdFromQuery = route.query.supermarketId

// State
const allInventoryItems = ref([])
const selectedInventoryItemId = ref(null)
const loading = ref(true)
const error = ref(null)

// Fetch all inventory items for this product master
const fetchProductInventory = async () => {
  try {
    const response = await ProductInventoryService.getInventoryByProductMaster(
      productMasterId,
      (err) => {
        console.error('Error fetching product inventory:', err)
        error.value = err.response?.data?.message || 'Failed to load product details'
      }
    )

    if (response && response.data) {
      // Items are already filtered by productMasterId from the backend
      allInventoryItems.value = response.data

      if (allInventoryItems.value.length === 0) {
        error.value = 'Product not found'
      } else {
        // Set the initial selected item
        if (supermarketIdFromQuery) {
          // Find first item from the specified supermarket
          const itemFromSupermarket = allInventoryItems.value.find(
            (item) => item.supermarketId === supermarketIdFromQuery
          )
          selectedInventoryItemId.value = itemFromSupermarket?.id || allInventoryItems.value[0].id
        } else {
          selectedInventoryItemId.value = allInventoryItems.value[0].id
        }
      }
    }
  } catch (err) {
    console.error('Error:', err)
    error.value = 'Failed to load product details'
  } finally {
    loading.value = false
  }
}

// Computed properties
const currentItem = computed(() => {
  return allInventoryItems.value.find((item) => item.id === selectedInventoryItemId.value)
})

const productName = computed(() => {
  return currentItem.value?.productName || 'Unknown Product'
})

const productDescription = computed(() => {
  return currentItem.value?.productDescription || ''
})

const categoryName = computed(() => {
  return currentItem.value?.categoryName || 'Unknown Category'
})

const supermarketName = computed(() => {
  return currentItem.value?.supermarketName || 'Unknown Supermarket'
})

const productEmoji = computed(() => {
  const category = categoryName.value.toLowerCase()
  const emojiMap = {
    dairy: '🧀',
    bakery: '🥐',
    beverages: '🥤',
    spices: '🌶️',
    cosmetics: '💄',
    meat: '🍖',
    seafood: '🦐',
    produce: '🥬',
    fruits: '🍎',
    vegetables: '🥕',
  }

  for (const [keyword, emoji] of Object.entries(emojiMap)) {
    if (category.includes(keyword)) {
      return emoji
    }
  }

  return '🛒'
})

// Get all inventory items from the same supermarket
const allSupermarketItems = computed(() => {
  if (!currentItem.value) return []
  return allInventoryItems.value.filter(
    (item) => item.supermarketId === currentItem.value.supermarketId
  )
})

// Get other inventory items (excluding current one)
const otherInventoryItems = computed(() => {
  return allSupermarketItems.value.filter((item) => item.id !== selectedInventoryItemId.value)
})

// Get grouped items by supermarket (excluding current supermarket)
const otherLocations = computed(() => {
  if (!currentItem.value) return []

  const locationMap = new Map()

  for (const item of allInventoryItems.value) {
    // Skip items from current supermarket
    if (item.supermarketId === currentItem.value.supermarketId) continue

    const key = item.supermarketId

    if (locationMap.has(key)) {
      const location = locationMap.get(key)
      location.totalQuantity += item.quantityAvailable
      location.bestPrice = Math.min(location.bestPrice, item.sellingPrice)
      location.earliestExpiry = Math.min(location.earliestExpiry, item.expiryDate)
    } else {
      locationMap.set(key, {
        supermarketId: item.supermarketId,
        supermarketName: item.supermarketName,
        totalQuantity: item.quantityAvailable,
        bestPrice: item.sellingPrice,
        earliestExpiry: item.expiryDate,
      })
    }
  }

  return Array.from(locationMap.values())
})

// Helper functions
const formatDate = (timestamp) => {
  if (!timestamp) return 'N/A'
  const date = new Date(timestamp)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

const calculateDaysUntil = (timestamp) => {
  if (!timestamp) return 'N/A'
  const now = Date.now()
  const diff = timestamp - now
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24))

  if (days < 0) return 'Expired'
  if (days === 0) return 'Today'
  if (days === 1) return '1 day'
  return `${days} days`
}

const calculateDiscount = (originalPrice, sellingPrice) => {
  if (!originalPrice || !sellingPrice) return ''
  const discount = Math.round(((originalPrice - sellingPrice) / originalPrice) * 100)
  return `-${discount}%`
}

const calculateAvailability = (expiryDate, quantityAvailable, status) => {
  if (status !== 'AVAILABLE') {
    return 'out of stock'
  }

  const now = Date.now()
  if (expiryDate <= now) {
    return 'out of stock'
  }

  const daysUntilExpiry = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24))
  if (daysUntilExpiry <= 3) {
    return 'limited'
  }

  if (quantityAvailable === 0) {
    return 'out of stock'
  } else if (quantityAvailable <= 10) {
    return 'limited'
  }

  return 'available'
}

const getAvailability = (item) => {
  return calculateAvailability(item.expiryDate, item.quantityAvailable, item.status)
}

// Event handlers
const onInventoryItemChange = () => {
  // Update URL query param when switching batches
  router.replace({
    query: { supermarketId: currentItem.value.supermarketId },
  })
}

// Navigation
const navigateToSupermarket = (supermarketId) => {
  router.push(`/supermarkets/${supermarketId}`)
}

const navigateToProductAtSupermarket = (supermarketId) => {
  router.push(`/products/${productMasterId}?supermarketId=${supermarketId}`)
}

const goBack = () => {
  router.back()
}

// Watch for changes to currentItem and update store
watch(
  () => currentItem.value,
  (newItem) => {
    if (newItem && newItem.supermarketId) {
      supermarketStore.setSelectedSupermarketId(newItem.supermarketId)
    }
  },
  { immediate: true }
)

// Load data on mount
onMounted(() => {
  fetchProductInventory()
})
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-4 py-2 rounded-lg border transition;
}
</style>
