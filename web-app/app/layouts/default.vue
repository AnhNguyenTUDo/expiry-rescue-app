<template>
  <div class="flex min-h-screen flex-col overflow-x-clip bg-gray-100 text-gray-800">
    <AppHeader
      v-model:selected-supermarket="selectedSupermarket"
      :supermarkets="supermarketStore.supermarkets"
      :current-supermarket-name="supermarketStore.currentSupermarketName"
      :cart-total="cartStore.totalItems"
      @supermarket-change="navigateToSupermarket"
    />

    <!-- Page Content -->
    <div class="mx-auto mt-5 w-full max-w-7xl flex-grow px-4">
      <slot />
    </div>

    <AppFooter />
    <ScrollToTopButton />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppFooter from '~/components/layout/AppFooter.vue'
import AppHeader from '~/components/layout/AppHeader.vue'
import ScrollToTopButton from '~/components/ui/ScrollToTopButton.vue'
import { useAuthStore } from '~/stores/auth'
import { useCartStore } from '~/stores/cart'
import { useSupermarketStore } from '~/stores/supermarket'

const router = useRouter()
const route = useRoute()

// Stores
const supermarketStore = useSupermarketStore()
const cartStore = useCartStore()
const authStore = useAuthStore()

// Local state for dropdown
const selectedSupermarket = ref('')

// Update selected supermarket based on route
const updateSelectedSupermarket = () => {
  // Check if we're on a supermarket page
  if (route.path.startsWith('/supermarkets/')) {
    const supermarketId = route.params.id
    if (supermarketId) {
      selectedSupermarket.value = supermarketId
      supermarketStore.setSelectedSupermarketId(supermarketId)
    }
  } else {
    selectedSupermarket.value = ''
    supermarketStore.clearSelectedSupermarket()
  }
}

// Navigate to selected supermarket
const navigateToSupermarket = () => {
  if (selectedSupermarket.value) {
    router.push(`/supermarkets/${selectedSupermarket.value}`)
  }
}

// Watch for route changes
watch(
  () => route.path,
  () => {
    updateSelectedSupermarket()
  },
  { flush: 'post' }
)

watch(
  () => route.query.supermarketId,
  () => {
    updateSelectedSupermarket()
  },
  { flush: 'post' }
)

// Update on mount
onMounted(() => {
  updateSelectedSupermarket()
  authStore.initAuth()
})
</script>
