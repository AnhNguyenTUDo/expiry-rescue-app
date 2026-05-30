<template>
  <div>
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-600 text-lg">Loading supermarket details...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p><strong>Error:</strong> {{ error }}</p>
    </div>

    <!-- Supermarket Details -->
    <div v-else-if="supermarket">
      <SupermarketDetailHeader :supermarket="supermarket" />

      <ProductFilter v-model="selectedCategoryFilter" :categories="allCategories" />

      <ProductCategorySection
        v-for="category in filteredCategoriesWithProducts"
        :key="category.id"
        :category="category"
        :expanded="expandedCategories[category.id]"
        :supermarket-id="supermarketId"
        @toggle="toggleCategory(category.id)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SupermarketService from '~/services/supermarket.service'
import ProductCategoryService from '~/services/product-category.service'
import SupermarketDetailHeader from '~/components/supermarket/product-list/SupermarketDetailHeader.vue'
import ProductCategorySection from '~/components/supermarket/product-list/ProductCategorySection.vue'
import ProductFilter from '~/components/supermarket/product-list/product-filter/ProductFilter.vue'

const route = useRoute()
const supermarketId = route.params.id

const supermarket = ref(null)
const products = ref([])
const allCategories = ref([])
const selectedCategoryFilter = ref('all')
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

// Shared helpers
import { formatDate, calculateDaysUntil } from '~/utils/date'
import { calculateDiscount } from '~/utils/price'
import { calculateAvailability, getCategoryEmoji } from '~/utils/product'

const filteredCategoriesWithProducts = computed(() => {
  let categoriesToShow = allCategories.value
  if (selectedCategoryFilter.value !== 'all') {
    categoriesToShow = categoriesToShow.filter((cat) => cat.id === selectedCategoryFilter.value)
  }
  return categoriesToShow.map((category) => ({
    ...category,
    products: products.value.filter((p) => p.categoryId === category.id),
  }))
})

const toggleCategory = (categoryId) => {
  expandedCategories.value[categoryId] = !expandedCategories.value[categoryId]
}

onMounted(async () => {
  await Promise.all([fetchSupermarketWithProducts(), fetchCategories()])
})
</script>
