<template>
  <div>
    <LoadingState v-if="loading" message="Loading product details..." />
    <ErrorAlert v-else-if="error" :error="error" />

    <!-- Product Details -->
    <div v-else-if="currentItem" class="max-w-4xl mx-auto">
      <!-- Product Header with Image-->
      <div class="flex gap-4 mb-6">
        <!-- Product image-->
        <div class="w-1/2 h-114 bg-white rounded-lg flex items-center justify-center p-8">
          <SvgIcon name="icon-products" class="text-gray-400 w-full h-full object-contain" />
        </div>

        <!-- Right column -->
        <div class="w-1/2 flex flex-col gap-4">
          <!-- Product Info -->
          <div class="bg-white rounded-lg p-5">
            <h1 class="text-2xl font-semibold text-gray-800 mb-1">{{ productName }}</h1>
            <p class="text-lg text-gray-500 mb-5">{{ categoryName }}</p>

            <!-- Pricing Information -->
            <div class="mb-5">
              <PriceBlock
                :original-price="currentItem.originalPrice"
                :selling-price="currentItem.sellingPrice"
              />
            </div>

            <!-- Expiry Information -->
            <div class="mb-3">
              <ExpiryBadge :expiry-date="currentItem.expiryDate" size="lg" />
            </div>

            <!-- Batch selector -->
            <div v-if="otherInventoryItems.length > 0" class="mb-3">
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Other batches at {{ supermarketName }}:
              </label>
              <DropdownSelect
                v-model="selectedInventoryItemId"
                :options="batchOptions"
                min-width="100%"
                @change="onInventoryItemChange"
              />
            </div>

            <!-- Units left -->
            <p class="mb-2.5 text-sm text-gray-600">
              {{ currentItem.quantityAvailable }} units left
            </p>

            <!-- Add to cart Button -->
            <div>
              <button
                v-if="currentItem && getAvailability(currentItem) !== 'out of stock'"
                @click="addToCart"
                :disabled="isInCart"
                class="w-full py-3 px-6 rounded-[11px] font-bold transition"
                :class="
                  isInCart
                    ? 'bg-gray-400 text-white cursor-not-allowed'
                    : 'bg-green-700 text-white hover:bg-green-800 cursor-pointer'
                "
              >
                <span class="flex items-center justify-center gap-2">
                  <SvgIcon
                    :name="isInCart ? 'icon-check-circle' : 'icon-shopping-basket-add-outline'"
                    class="w-6 h-6"
                  />
                  {{ isInCart ? 'Added to cart' : 'Add to cart' }}
                </span>
              </button>
              <div
                v-else
                class="w-full py-3 px-6 rounded-[11px] font-semibold bg-gray-300 text-gray-600 text-center"
              >
                Not Available
              </div>
            </div>
          </div>

          <!-- View all products -->
          <div class="bg-white rounded-[12px]">
            <button
              @click="navigateToSupermarket(currentItem.supermarketId)"
              class="group w-full flex items-center justify-between px-5 py-3 text-sm text-gray-600 hover:text-green-700 transition cursor-pointer"
            >
              <span
                >View all products at <span class="font-semibold">{{ supermarketName }}</span></span
              >
              <SvgIcon
                name="icon-chevron-right"
                class="w-3.5 h-3.5 text-gray-400 group-hover:text-green-700 transition"
              />
            </button>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div v-if="productDescription" class="bg-white rounded-lg p-6 mb-6">
        <h3 class="font-semibold uppercase tracking-wide mb-2">Description</h3>
        <p class="text-gray-600 text-sm">{{ productDescription }}</p>
      </div>

      <!-- Other Locations in the same city (if product available at other supermarkets) -->
      <div v-if="otherLocations.length > 0" class="bg-white p-6 rounded-lg mb-6">
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
    <div v-else class="text-center py-12 bg-white rounded-lg">
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
import DropdownSelect from '~/components/ui/DropdownSelect.vue'
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

const batchOptions = computed(() =>
  allSupermarketItems.value.map((item) => ({
    value: item.id,
    label: `Expires ${formatDate(item.expiryDate)} · ${item.quantityAvailable} units · ${formatPrice(item.sellingPrice)}`,
  }))
)

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
