<template>
  <div>
    <LoadingState v-if="loading" message="Loading product details..." />
    <ErrorAlert v-else-if="error" :error="error" />

    <!-- Product Details -->
    <div v-else-if="currentItem">
      <!-- Product Header with Image-->
      <div class="bg-white p-8 rounded-xl shadow mb-6">
        <div class="flex gap-8">
          <!-- Product image-->
          <div class="w-1/3 flex items-center justify-center">
            <div class="text-9xl">
              <SvgIcon name="icon-products" class="text-gray-400 w-30 h-30" />
            </div>
          </div>

          <!-- Product Info -->
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
              <PriceBlock
                :original-price="currentItem.originalPrice"
                :selling-price="currentItem.sellingPrice"
                size="lg"
              />
              <div class="mt-3">
                <span class="text-sm text-gray-600">Available Units:</span>
                <p class="text-lg font-semibold text-gray-800">
                  {{ currentItem.quantityAvailable }} units
                </p>
              </div>
            </div>

            <!-- Expiry Information -->
            <div class="border border-gray-200 rounded-lg p-4 mb-4">
              <ExpiryBadge :expiry-date="currentItem.expiryDate" size="lg" />
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
                  {{ item.quantityAvailable }} units - {{ formatPrice(item.sellingPrice) }}
                </option>
              </select>
            </div>

            <!-- Add to Cart Button -->
            <div class="mt-6">
              <button
                v-if="currentItem && getAvailability(currentItem) !== 'out of stock'"
                @click="addToCart"
                :disabled="isInCart"
                class="w-full py-3 px-6 rounded-lg font-semibold text-lg transition"
                :class="
                  isInCart
                    ? 'bg-gray-400 text-white cursor-not-allowed'
                    : 'bg-green-600 text-white hover:bg-green-700 cursor-pointer'
                "
              >
                {{ isInCart ? '✓ Added to Cart' : 'Add to Cart' }}
              </button>
              <div
                v-else
                class="w-full py-3 px-6 rounded-lg font-semibold text-lg bg-gray-300 text-gray-600 text-center"
              >
                Not Available
              </div>
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

      <!-- Other Locations in the same city (if product available at other supermarkets) -->
      <div v-if="otherLocations.length > 0" class="bg-white p-6 rounded-xl shadow mb-6">
        <h2 class="text-2xl font-bold mb-4">
          Also available at {{ otherLocationsTotal }} other location(s) in this city
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
                {{ formatPrice(location.bestPrice) }}
              </p>
              <p class="text-gray-600">
                <span class="font-medium">Expires:</span>
                {{ formatDate(location.earliestExpiry) }}
              </p>
            </div>
          </div>
        </div>

        <!-- Load more -->
        <div v-if="otherLocationsHasNext" class="text-center mt-4">
          <button
            @click="loadMoreLocations"
            :disabled="loadingMoreLocations"
            class="btn bg-green-600 text-white hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
          >
            {{ loadingMoreLocations ? 'Loading...' : 'Load more locations' }}
          </button>
          <p class="text-sm text-gray-500 mt-2">
            Showing {{ otherLocations.length }} of {{ otherLocationsTotal }}
          </p>
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
import LoadingState from '~/components/ui/LoadingState.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import PriceBlock from '~/components/ui/PriceBlock.vue'
import { useRoute, useRouter } from 'vue-router'
import ProductInventoryService from '~/services/product-inventory.service'
import { useSupermarketStore } from '~/stores/supermarket'
import { useCartStore } from '~/stores/cart'
import { useAuthStore } from '~/stores/auth'

const route = useRoute()
const router = useRouter()
const supermarketStore = useSupermarketStore()
const cartStore = useCartStore()
const authStore = useAuthStore()

const supermarketId = route.params.id
const productMasterId = route.params.productId
const batchId = route.query.batch

// State
// Batches of this product at the current supermarket (drives header + batch dropdown)
const supermarketItems = ref([])
const selectedInventoryItemId = ref(null)
const loading = ref(true)
const error = ref(null)

// Other locations in the same city (server-aggregated + paginated, accumulated as we load more)
const LOCATIONS_PAGE_SIZE = 6
const otherLocations = ref([])
const otherLocationsPageIndex = ref(0)
const otherLocationsTotal = ref(0)
const otherLocationsHasNext = ref(false)
const loadingMoreLocations = ref(false)

// Fetch a page of "other locations in this city" and append it to the list
const loadOtherLocations = async (page = 0) => {
  loadingMoreLocations.value = true
  try {
    const res = await ProductInventoryService.getOtherLocationSummaries(
      productMasterId,
      supermarketId,
      page,
      LOCATIONS_PAGE_SIZE,
      (err) => console.error('Error fetching other locations:', err)
    )
    const paged = res?.data
    if (paged && Array.isArray(paged.content)) {
      otherLocations.value =
        page === 0 ? paged.content : [...otherLocations.value, ...paged.content]
      otherLocationsPageIndex.value = paged.page
      otherLocationsTotal.value = paged.totalElements
      otherLocationsHasNext.value = paged.hasNext
    }
  } finally {
    loadingMoreLocations.value = false
  }
}

const loadMoreLocations = () => {
  if (otherLocationsHasNext.value && !loadingMoreLocations.value) {
    loadOtherLocations(otherLocationsPageIndex.value + 1)
  }
}

// Fetch this product's batches at the current supermarket, plus a summary of other locations
const fetchProductInventory = async () => {
  try {
    const response = await ProductInventoryService.getInventoryBySupermarketAndProductMaster(
      supermarketId,
      productMasterId,
      (err) => {
        console.error('Error fetching product inventory:', err)
        error.value = err.response?.data?.message || 'Failed to load product details'
      }
    )

    if (response && response.data) {
      supermarketItems.value = response.data

      if (supermarketItems.value.length === 0) {
        error.value = 'Product not found at this supermarket'
        return
      }

      // Set the initial selected batch based on the batch query param, else the first batch
      if (batchId) {
        const batchItem = supermarketItems.value.find((item) => item.id === batchId)
        selectedInventoryItemId.value = batchItem?.id || supermarketItems.value[0].id
      } else {
        selectedInventoryItemId.value = supermarketItems.value[0].id
      }

      // Update URL to include batch ID if not already present
      if (!batchId && selectedInventoryItemId.value) {
        router.replace({
          query: { batch: selectedInventoryItemId.value },
        })
      }
    }

    // Fetch the first page of "also available in this city" (non-blocking for the main view)
    await loadOtherLocations(0)
  } catch (err) {
    console.error('Error:', err)
    error.value = 'Failed to load product details'
  } finally {
    loading.value = false
  }
}

// Computed properties
const currentItem = computed(() => {
  return supermarketItems.value.find((item) => item.id === selectedInventoryItemId.value)
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

// All batches of this product at the current supermarket (already scoped by the API)
const allSupermarketItems = computed(() => supermarketItems.value)

// Other batches at this supermarket (excluding the selected one)
const otherInventoryItems = computed(() => {
  return allSupermarketItems.value.filter((item) => item.id !== selectedInventoryItemId.value)
})

// Shared helpers
import { formatDate } from '~/utils/date'
import { formatPrice } from '~/utils/price'
import { getAvailability } from '~/utils/product'

// Event handlers
const onInventoryItemChange = () => {
  // Update URL query param when switching batches
  router.replace({
    query: { batch: selectedInventoryItemId.value },
  })
}

// Navigation
const navigateToSupermarket = (supermarketIdParam) => {
  router.push(`/supermarkets/${supermarketIdParam}`)
}

const navigateToProductAtSupermarket = (supermarketIdParam) => {
  router.push(`/supermarkets/${supermarketIdParam}/products/${productMasterId}`)
}

const goBack = () => {
  router.back()
}

// Cart functionality
const isInCart = computed(() => {
  return currentItem.value ? cartStore.isInCart(currentItem.value.id) : false
})

const addToCart = () => {
  // Check if user is authenticated
  if (!authStore.isAuthenticated) {
    // Redirect to login page
    navigateTo('/login')
    return
  }

  // If authenticated, proceed with adding to cart
  if (currentItem.value) {
    cartStore.addToCart({
      inventoryId: currentItem.value.id,
      productMasterId: currentItem.value.productMasterId,
      productName: currentItem.value.productName,
      productDescription: currentItem.value.productDescription,
      categoryName: currentItem.value.categoryName,
      supermarketId: currentItem.value.supermarketId,
      supermarketName: currentItem.value.supermarketName,
      originalPrice: currentItem.value.originalPrice,
      sellingPrice: currentItem.value.sellingPrice,
      expiryDate: currentItem.value.expiryDate,
      quantityAvailable: currentItem.value.quantityAvailable,
    })
  }
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
