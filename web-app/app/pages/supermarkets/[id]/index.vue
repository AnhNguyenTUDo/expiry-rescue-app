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
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import SupermarketService from '~/services/supermarket.service'
import ProductCategoryService from '~/services/product-category.service'
import SupermarketDetailHeader from '~/components/supermarket/product-list/SupermarketDetailHeader.vue'
import ProductCategorySection from '~/components/supermarket/product-list/ProductCategorySection.vue'
import CategorySelector from '~/components/supermarket/product-list/product-filter/CategorySelector.vue'
import LoadingState from '~/components/ui/LoadingState.vue'
import ErrorAlert from '~/components/ui/ErrorAlert.vue'

// Shared helpers
import { formatDate, calculateDaysUntil } from '~/utils/date'
import { calculateDiscount } from '~/utils/price'
import { calculateAvailability, getCategoryEmoji, isEndingSoon } from '~/utils/product'

const route = useRoute()
const supermarketId = route.params.id

const ENDING_SOON_ID = 'ending-soon'

const supermarket = ref(null)
const products = ref([])
const allCategories = ref([])
const selectedCategoryId = ref(ENDING_SOON_ID)
const loading = ref(true)
const error = ref(null)
const expandedCategories = ref({})

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
            existingProduct.sellUntil = formatDate(item.expiryDate)
            existingProduct.expire = formatDate(item.expiryDate)
            existingProduct.sellDays = calculateDaysUntil(item.expiryDate)
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
            emoji: getCategoryEmoji(item.categoryName),
            category: item.categoryName,
            name: item.productName,
            location: item.supermarketName,
            sellUntil: formatDate(item.expiryDate),
            expire: formatDate(item.expiryDate),
            sellDays: calculateDaysUntil(item.expiryDate),
            expireDays: calculateDaysUntil(item.expiryDate),
            oldPrice: `${item.originalPrice.toLocaleString()}₫`,
            newPrice: `${item.sellingPrice.toLocaleString()}₫`,
            discount: calculateDiscount(item.originalPrice, item.sellingPrice),
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

const categoryOptions = computed(() => {
  const options = []

  if (endingSoonProducts.value.length > 0) {
    options.push({
      id: ENDING_SOON_ID,
      name: 'Ending soon',
      emoji: '⏰',
      count: endingSoonProducts.value.length,
    })
  }

  for (const cat of allCategories.value) {
    const count = products.value.filter((p) => p.categoryId === cat.id).length
    if (count > 0) {
      options.push({ id: cat.id, name: cat.name, emoji: getCategoryEmoji(cat.name), count })
    }
  }

  return options
})

const displayedSection = computed(() => {
  if (selectedCategoryId.value === ENDING_SOON_ID) {
    return { id: ENDING_SOON_ID, name: 'Ending soon', products: endingSoonProducts.value }
  }

  const cat = allCategories.value.find((c) => c.id === selectedCategoryId.value)
  if (!cat) return null

  return {
    ...cat,
    products: products.value.filter((p) => p.categoryId === cat.id),
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
  await Promise.all([fetchSupermarketWithProducts(), fetchCategories()])
})
</script>
