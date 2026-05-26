<template>
  <div class="min-h-screen flex flex-col bg-gray-100 text-gray-800 overflow-x-clip">
    <AppHeader
      v-model:selected-supermarket="selectedSupermarket"
      :supermarkets="supermarketStore.supermarkets"
      :current-supermarket-name="supermarketStore.currentSupermarketName"
      :cart-total="cartStore.totalItems"
      @supermarket-change="navigateToSupermarket"
    />

    <!-- Page Content -->
    <div class="max-w-7xl mx-auto mt-5 px-4 flex-grow w-full">
      <slot />
    </div>

    <AppFooter />
    <ScrollToTopButton />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useSupermarketStore } from "~/stores/supermarket";
import { useCartStore } from "~/stores/cart";
import { useAuthStore } from "~/stores/auth";
import AppHeader from "~/components/layout/AppHeader.vue";
import AppFooter from "~/components/layout/AppFooter.vue";
import ScrollToTopButton from "~/components/ui/ScrollToTopButton.vue";

const router = useRouter();
const route = useRoute();

// Stores
const supermarketStore = useSupermarketStore();
const cartStore = useCartStore();
const authStore = useAuthStore();

// Local state for dropdown
const selectedSupermarket = ref("");

// Update selected supermarket based on route
const updateSelectedSupermarket = () => {
  // Check if we're on a supermarket page
  if (route.path.startsWith("/supermarkets/")) {
    const supermarketId = route.params.id;
    if (supermarketId) {
      selectedSupermarket.value = supermarketId;
      supermarketStore.setSelectedSupermarketId(supermarketId);
    }
  }
  // Check if we're on a product page with supermarketId query param
  else if (route.path.startsWith("/products/") && route.query.supermarketId) {
    selectedSupermarket.value = route.query.supermarketId;
    supermarketStore.setSelectedSupermarketId(route.query.supermarketId);
  } else {
    selectedSupermarket.value = "";
    supermarketStore.clearSelectedSupermarket();
  }
};

// Navigate to selected supermarket
const navigateToSupermarket = () => {
  if (selectedSupermarket.value) {
    router.push(`/supermarkets/${selectedSupermarket.value}`);
  }
};

// Watch for route changes
watch(
  () => route.path,
  () => {
    updateSelectedSupermarket();
  },
  { flush: "post" }
);

watch(
  () => route.query.supermarketId,
  () => {
    updateSelectedSupermarket();
  },
  { flush: "post" }
);

// Update on mount
onMounted(() => {
  updateSelectedSupermarket();
  authStore.initAuth();
});
</script>
