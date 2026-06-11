<template>
  <div>
    <LoadingState v-if="loading" message="Loading product details..." />
    <ErrorState v-else-if="error" :message="errorMessage" @retry="fetchProductInventory" />

    <!-- Product Details -->
    <div v-else-if="currentItem" class="mx-auto max-w-4xl">
      <!-- Breadcrumb navigation-->
      <nav class="mb-4 flex items-center gap-1.5 text-sm text-gray-500">
        <NuxtLink to="/" class="transition hover:text-green-700">Home</NuxtLink>
        <span>/</span>
        <NuxtLink
          :to="`/supermarkets/${currentItem.supermarketId}`"
          class="transition hover:text-green-700"
          >{{ supermarketName }}</NuxtLink
        >
        <span>/</span>
        <span class="truncate font-medium text-gray-800">{{ productName }}</span>
      </nav>

      <!-- Product Header with Image-->
      <div class="mb-6 flex gap-4">
        <!-- Product image-->
        <div class="flex h-114 w-1/2 items-center justify-center rounded-lg bg-white p-8">
          <SvgIcon name="icon-products" class="h-full w-full object-contain text-gray-400" />
        </div>

        <!-- Right column -->
        <div class="flex w-1/2 flex-col gap-4">
          <!-- Product Info -->
          <div class="rounded-lg bg-white p-5">
            <h1 class="mb-1 text-2xl font-semibold text-gray-800">{{ productName }}</h1>
            <p class="mb-5 text-lg text-gray-500">{{ categoryName }}</p>

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
              <label class="mb-2 block text-sm font-semibold text-gray-700">
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
              <div
                v-if="getAvailability(currentItem) !== 'out of stock'"
                class="flex items-stretch gap-3"
              >
                <QuantityCounter v-model="quantity" :max="currentItem.quantityAvailable" />
                <button
                  :disabled="isInCart"
                  class="flex-1 rounded-[11px] px-6 py-3 font-bold transition"
                  :class="
                    isInCart
                      ? 'cursor-not-allowed bg-gray-400 text-white'
                      : 'cursor-pointer bg-green-700 text-white hover:bg-green-800'
                  "
                  @click="addToCart"
                >
                  <span class="flex items-center justify-center gap-2">
                    <SvgIcon
                      :name="
                        isInCart ? 'icon-check-circle-outline' : 'icon-shopping-basket-add-outline'
                      "
                      class="h-6 w-6"
                    />
                    {{ isInCart ? 'Added to cart' : 'Add to cart' }}
                  </span>
                </button>
              </div>
              <div
                v-else
                class="w-full rounded-[11px] bg-gray-300 px-6 py-3 text-center font-semibold text-gray-600"
              >
                Not Available
              </div>
            </div>
          </div>

          <!-- View all products -->
          <div class="rounded-[12px] bg-white">
            <NuxtLink
              :to="`/supermarkets/${currentItem.supermarketId}`"
              class="group flex w-full items-center justify-between px-5 py-3 text-sm text-gray-600 transition hover:text-green-700"
            >
              <span
                >View all products at <span class="font-semibold">{{ supermarketName }}</span></span
              >
              <SvgIcon
                name="icon-chevron-right"
                class="h-3.5 w-3.5 text-gray-400 transition group-hover:text-green-700"
              />
            </NuxtLink>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div v-if="productDescription" class="mb-6 rounded-lg bg-white p-6">
        <h3 class="mb-2 font-semibold tracking-wide uppercase">Description</h3>
        <p class="text-sm text-gray-600">{{ productDescription }}</p>
      </div>

      <!-- Other Locations in the same city (if product available at other supermarkets) -->
      <div v-if="otherLocations.length > 0" class="mb-6 rounded-lg bg-white p-6">
        <h2 class="mb-4 text-2xl font-semibold">
          Also available at {{ otherLocationsTotal }} other location(s) in this city
        </h2>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
          <NuxtLink
            v-for="location in otherLocations"
            :key="location.supermarketId"
            :to="`/supermarkets/${location.supermarketId}/products/${productMasterId}`"
            class="block rounded-lg border border-gray-200 p-4 transition hover:shadow-md"
          >
            <h3 class="mb-2 text-lg font-semibold text-gray-800">
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
          </NuxtLink>
        </div>

        <!-- Load more -->
        <div v-if="otherLocationsHasNext">
          <ShowMoreButton
            :remaining="otherLocationsTotal - otherLocations.length"
            :show-remaining="false"
            :loading="loadingMoreLocations"
            label="Load more locations"
            @click="loadMoreLocations"
          />
          <p class="mt-2 text-center text-sm text-gray-500">
            Showing {{ otherLocations.length }} of {{ otherLocationsTotal }}
          </p>
        </div>
      </div>
    </div>

    <!-- No Data State -->
    <div v-else class="rounded-lg bg-white py-12 text-center">
      <p class="text-lg text-gray-500">No product information available</p>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DropdownSelect from '~/components/ui/DropdownSelect.vue'
import ErrorState from '~/components/ui/ErrorState.vue'
import ExpiryBadge from '~/components/ui/ExpiryBadge.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import PriceBlock from '~/components/ui/PriceBlock.vue'
import QuantityCounter from '~/components/ui/QuantityCounter.vue'
import ShowMoreButton from '~/components/ui/ShowMoreButton.vue'
import { useNotify } from '~/composables/useNotify'
import ProductInventoryService from '~/services/product-inventory.service'
import { useAuthStore } from '~/stores/auth'
import { useCartStore } from '~/stores/cart'
import { useSupermarketStore } from '~/stores/supermarket'
import { formatDate } from '~/utils/date'
import { formatPrice } from '~/utils/price'
import { getAvailability } from '~/utils/product'

const route = useRoute()
const router = useRouter()
const supermarketStore = useSupermarketStore()
const cartStore = useCartStore()
const authStore = useAuthStore()
const notify = useNotify()

const supermarketId = route.params.id
const productMasterId = route.params.productId
const batchId = route.query.batch

// State
// Batches of this product at the current supermarket (drives header + batch dropdown)
const supermarketItems = ref([])
const selectedInventoryItemId = ref(null)
const quantity = ref(1)
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
      (err) => {
        console.error('Error fetching other locations:', err)
        notify.error('Could not load other locations.')
      }
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
  loading.value = true
  error.value = null
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

const errorMessage = computed(() =>
  error.value === 'Product not found at this supermarket'
    ? "This product isn't available at this supermarket."
    : "We couldn't load this product right now. Please try again in a moment."
)

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

// Other batches at this supermarket (excluding the selected one)
const otherInventoryItems = computed(() => {
  return supermarketItems.value.filter((item) => item.id !== selectedInventoryItemId.value)
})

const batchOptions = computed(() =>
  supermarketItems.value.map((item) => ({
    value: item.id,
    label: `Expires ${formatDate(item.expiryDate)} · ${item.quantityAvailable} units · ${formatPrice(item.sellingPrice)}`,
  }))
)

// Event handlers
const onInventoryItemChange = () => {
  // Update URL query param when switching batches
  router.replace({
    query: { batch: selectedInventoryItemId.value },
  })
}

// Cart functionality
const isInCart = computed(() => {
  return currentItem.value ? cartStore.isInCart(currentItem.value.id) : false
})

const addToCart = () => {
  // Check if user is authenticated
  if (!authStore.isAuthenticated) {
    // Send to login, remembering this page so they come back after logging in
    navigateTo(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
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
      quantity: quantity.value,
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
    // Reset the quantity when switching batches (max available changes)
    quantity.value = 1
  },
  { immediate: true }
)

// Load data on mount
onMounted(() => {
  fetchProductInventory()
})
</script>
