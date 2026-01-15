<template>
  <div class="min-h-screen bg-gray-100 text-gray-800">
    <!-- Header -->
    <header
      class="bg-gradient-to-r from-green-700 to-green-500 text-white py-6 shadow"
    >
      <div class="max-w-7xl mx-auto flex justify-between items-center px-4">
        <div class="flex items-center gap-4">
          <NuxtLink to="/" class="text-2xl font-bold hover:opacity-90 transition">
            🛒 Expiry Rescue
          </NuxtLink>

          <!-- Supermarket Dropdown -->
          <div class="relative">
            <select
              v-model="selectedSupermarket"
              @change="navigateToSupermarket"
              class="bg-white/10 border border-white/30 text-white rounded-lg px-4 py-2 pr-8 focus:outline-none focus:ring-2 focus:ring-white/50 cursor-pointer hover:bg-white/20 transition"
            >
              <option value="" class="text-gray-800" disabled>
                {{ supermarketStore.currentSupermarketName || "Select Supermarket" }}
              </option>
              <option
                v-for="supermarket in supermarketStore.supermarkets"
                :key="supermarket.id"
                :value="supermarket.id"
                class="text-gray-800"
              >
                {{ supermarket.name }} - {{ supermarket.district }}
              </option>
            </select>
          </div>
        </div>

        <nav class="flex gap-3">
          <NuxtLink to="/" class="btn btn-outline-light">Home</NuxtLink>
          <button class="btn btn-outline-light">Products</button>
          <button class="btn btn-outline-light">About</button>
          <NuxtLink to="/cart" class="btn btn-outline-light relative">
            Cart
            <span v-if="cartStore.totalItems > 0" class="ml-1">({{ cartStore.totalItems }})</span>
            <span
              v-if="cartStore.totalItems > 0"
              class="absolute -top-1 -right-1 bg-red-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
            >
              {{ cartStore.totalItems > 99 ? '99+' : cartStore.totalItems }}
            </span>
          </NuxtLink>
          <button class="btn btn-outline-light">Login</button>
        </nav>
      </div>
    </header>

    <!-- Page Content -->
    <div class="max-w-7xl mx-auto mt-8 px-4">
      <slot />
    </div>

    <!-- Footer -->
    <footer class="bg-gray-900 text-white py-10 mt-16">
      <div class="max-w-7xl mx-auto px-4 flex flex-wrap justify-between gap-6">
        <div>
          <h3 class="text-xl font-semibold mb-2">Expiry Rescue</h3>
          <p>Reducing food waste, saving money</p>
        </div>
        <div class="flex gap-3 flex-wrap">
          <button class="btn btn-outline-light">Contact Us</button>
          <button class="btn btn-outline-light">Help Center</button>
          <button class="btn btn-outline-light">Terms of Service</button>
          <button class="btn btn-outline-light">Privacy Policy</button>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useSupermarketStore } from "~/stores/supermarket";
import { useCartStore } from "~/stores/cart";

const router = useRouter();
const route = useRoute();

// Stores
const supermarketStore = useSupermarketStore();
const cartStore = useCartStore();

// Local state for dropdown
const selectedSupermarket = ref("");

// Update selected supermarket based on route
const updateSelectedSupermarket = () => {
  // Check if we're on a supermarket page
  if (route.path.startsWith('/supermarkets/')) {
    const supermarketId = route.params.id;
    if (supermarketId) {
      selectedSupermarket.value = supermarketId;
      supermarketStore.setSelectedSupermarketId(supermarketId);
    }
  }
  // Check if we're on a product page with supermarketId query param
  else if (route.path.startsWith('/products/') && route.query.supermarketId) {
    selectedSupermarket.value = route.query.supermarketId;
    supermarketStore.setSelectedSupermarketId(route.query.supermarketId);
  }
  else {
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
  { flush: 'post' }
);

watch(
  () => route.query.supermarketId,
  () => {
    updateSelectedSupermarket();
  },
  { flush: 'post' }
);

// Update on mount
onMounted(() => {
  updateSelectedSupermarket();
});
</script>

<style scoped>
@reference "tailwindcss";
.btn {
  @apply px-4 py-2 rounded-lg border transition;
}
.btn-outline {
  @apply border-gray-300 hover:bg-green-600 hover:text-white hover:border-green-600;
}
.btn-outline-light {
  @apply border-white/40 text-white hover:bg-white/20;
}
</style>
