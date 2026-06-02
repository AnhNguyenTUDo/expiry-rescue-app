<template>
  <div>
    <LoadingState v-if="loading" message="Loading supermarket details..." />
    <ErrorAlert v-else-if="error" :error="error" />

    <!-- Supermarket Details -->
    <div v-else-if="supermarket">
      <SupermarketDetailHeader :supermarket="supermarket" />

      <CategorySelector v-model="selectedCategoryId" :options="categoryOptions" />

      <ProductCategorySection
        v-if="displayedSection"
        :category="displayedSection"
        :expanded="expandedCategories[displayedSection.id]"
        :supermarket-id="supermarketId"
        @toggle="toggleCategory(displayedSection.id)"
      >
        <template #filters>
          <ProductListFilters v-model:sort="sortBy" />
        </template>
      </ProductCategorySection>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import SupermarketService from '~/services/supermarket.service'
import ProductCategoryService from '~/services/product-category.service'
import SupermarketDetailHeader from '~/components/supermarket/product-list/SupermarketDetailHeader.vue'
import ProductCategorySection from '~/components/supermarket/product-list/ProductCategorySection.vue'
import CategorySelector from '~/components/supermarket/product-list/product-filter/CategorySelector.vue'
import ProductListFilters from '~/components/supermarket/product-list/product-filter/ProductListFilters.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'

// Shared helpers
import { formatDate, calculateDaysUntil } from '~/utils/date'
import { calculateDiscount, formatPrice } from '~/utils/price'
import { calculateAvailability, isEndingSoon } from '~/utils/product'

const route = useRoute()
const supermarketId = route.params.id

const ENDING_SOON_ID = 'ending-soon'

const supermarket = ref(null)
const products = ref([])
const allCategories = ref([])
const selectedCategoryId = ref(ENDING_SOON_ID)
const sortBy = ref('expiry')
const loading = ref(true)
const error = ref(null)
const expandedCategories = ref({})

const headerShadow = useHeaderShadow()

const fetchSupermarketWithProducts = async () => {
  try {
    const response = await SupermarketService.getSupermarketWithProducts(supermarketId, (err) => {
      console.error('Error fetching supermarket:', err)
      error.value = err.response?.data?.message || 'Failed to load supermarket'
    })

    if (response && response.data) {
      supermarket.value = response.data

      const productMap = new Map()

      for (const item of response.data.products) {
        const key = item.productMasterId

        if (productMap.has(key)) {
          const existingProduct = productMap.get(key)
          existingProduct.quantityAvailable += item.quantityAvailable
          existingProduct.inventoryItems.push(item)

          if (item.expiryDate < existingProduct.earliestExpiryDate) {
            existingProduct.earliestExpiryDate = item.expiryDate
            existingProduct.expire = formatDate(item.expiryDate)
            existingProduct.expireDays = calculateDaysUntil(item.expiryDate)
          }

          const totalQuantity = existingProduct.quantityAvailable
          const earliestExpiry = existingProduct.earliestExpiryDate
          const hasAvailableStatus = existingProduct.inventoryItems.some(
            (inv) => inv.status === 'AVAILABLE'
          )

          existingProduct.availability = calculateAvailability(
            earliestExpiry,
            totalQuantity,
            hasAvailableStatus ? 'AVAILABLE' : 'NOT_AVAILABLE'
          )
        } else {
          const availability = calculateAvailability(
            item.expiryDate,
            item.quantityAvailable,
            item.status
          )

          productMap.set(key, {
            id: item.id,
            productMasterId: item.productMasterId,
            categoryId: item.categoryId,
            categoryName: item.categoryName,
            category: item.categoryName,
            name: item.productName,
            location: item.supermarketName,
            expire: formatDate(item.expiryDate),
            expireDays: calculateDaysUntil(item.expiryDate),
            oldPrice: formatPrice(item.originalPrice),
            newPrice: formatPrice(item.sellingPrice),
            sellingPrice: item.sellingPrice,
            discount: calculateDiscount(item.originalPrice, item.sellingPrice),
            discountPercent: Math.round(
              ((item.originalPrice - item.sellingPrice) / item.originalPrice) * 100
            ),
            availability,
            quantityAvailable: item.quantityAvailable,
            earliestExpiryDate: item.expiryDate,
            inventoryItems: [item],
          })
        }
      }

      products.value = Array.from(productMap.values())
    }
  } catch (err) {
    console.error('Error:', err)
    error.value = 'Failed to load supermarket details'
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await ProductCategoryService.getAllCategories((err) => {
      console.error('Error fetching categories:', err)
    })
    if (response && response.data) {
      allCategories.value = response.data
    }
  } catch (err) {
    console.error('Error fetching categories:', err)
  }
}

const endingSoonProducts = computed(() =>
  products.value.filter((p) => isEndingSoon(p.earliestExpiryDate))
)

// Sorts a copy of the list; out-of-stock products always sink to the bottom.
const sortProducts = (list) => {
  const comparators = {
    expiry: (a, b) => a.earliestExpiryDate - b.earliestExpiryDate,
    discount: (a, b) => b.discountPercent - a.discountPercent,
    price_asc: (a, b) => a.sellingPrice - b.sellingPrice,
    price_desc: (a, b) => b.sellingPrice - a.sellingPrice,
  }
  const outOfStock = (p) => (p.availability === 'out of stock' ? 1 : 0)
  return [...list].sort((a, b) => outOfStock(a) - outOfStock(b) || comparators[sortBy.value](a, b))
}

const categoryOptions = computed(() => {
  const options = []

  if (endingSoonProducts.value.length > 0) {
    options.push({
      id: ENDING_SOON_ID,
      name: 'Ending soon',
      count: endingSoonProducts.value.length,
    })
  }

  for (const cat of allCategories.value) {
    const count = products.value.filter((p) => p.categoryId === cat.id).length
    if (count > 0) {
      options.push({ id: cat.id, name: cat.name, count })
    }
  }

  return options
})

const displayedSection = computed(() => {
  if (selectedCategoryId.value === ENDING_SOON_ID) {
    return {
      id: ENDING_SOON_ID,
      name: 'Ending soon',
      products: sortProducts(endingSoonProducts.value),
    }
  }

  const cat = allCategories.value.find((c) => c.id === selectedCategoryId.value)
  if (!cat) return null

  return {
    ...cat,
    products: sortProducts(products.value.filter((p) => p.categoryId === cat.id)),
  }
})

watch(categoryOptions, (options) => {
  const stillValid = options.some((c) => c.id === selectedCategoryId.value)
  if (!stillValid && options.length > 0) {
    selectedCategoryId.value = options[0].id
  }
})

const toggleCategory = (sectionId) => {
  expandedCategories.value[sectionId] = !expandedCategories.value[sectionId]
}

onMounted(async () => {
  headerShadow.value = false // The AppHeader should have no shadow on this page
  await Promise.all([fetchSupermarketWithProducts(), fetchCategories()])
})

onBeforeUnmount(() => {
  headerShadow.value = true // Restores the default so every other page still gets the app header shadow
})
</script>
