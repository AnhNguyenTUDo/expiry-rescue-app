<template>
  <div class="min-h-screen flex flex-col bg-gray-100 text-gray-800">
    <!-- Header -->
    <header
      class="bg-gradient-to-r from-green-700 to-green-500 text-white py-6 shadow"
    >
      <div class="max-w-7xl mx-auto flex justify-between items-center px-4">
        <div class="flex items-center gap-4">
          <NuxtLink to="/" class="text-2xl font-bold hover:opacity-90 transition">
            Expiry Rescue
          </NuxtLink>

          <!-- Supermarket Dropdown -->
          <div class="relative">
            <SupermarketDropdown
              v-model="selectedSupermarket"
              :supermarkets="supermarketStore.supermarkets"
              :current-supermarket-name="supermarketStore.currentSupermarketName"
              @change="navigateToSupermarket"
            />
          </div>
        </div>

        <nav class="flex gap-3">
          <NuxtLink to="/" class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Home</NuxtLink>
          <!-- <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Products</button>
          <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">About</button> -->
          <NuxtLink to="/cart" class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20 relative">
            Cart
            <span v-if="cartStore.totalItems > 0" class="ml-1">({{ cartStore.totalItems }})</span>
            <span
              v-if="cartStore.totalItems > 0"
              class="absolute -top-1 -right-1 bg-red-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
            >
              {{ cartStore.totalItems > 99 ? '99+' : cartStore.totalItems }}
            </span>
          </NuxtLink>
          <!-- Profile Dropdown -->
          <ProfileDropdown />
        </nav>
      </div>
    </header>

    <!-- Page Content -->
    <div class="max-w-7xl mx-auto mt-5 px-4 flex-grow w-full">
      <slot />
    </div>

    <!-- Footer -->
    <footer class="bg-gray-900 text-white py-7 mt-10">
      <div class="max-w-7xl mx-auto px-4 flex flex-wrap justify-between items-center gap-6">
        <div>
          <h3 class="text-xl font-semibold mb-2">Expiry Rescue</h3>
          <p>Reducing food waste, saving money</p>
        </div>
        <div class="flex gap-3 flex-wrap items-center">
          <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Contact Us</button>
          <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Help Center</button>
          <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Terms of Service</button>
          <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Privacy Policy</button>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useSupermarketStore } from "~/stores/supermarket";
import { useCartStore } from "~/stores/cart";
import { useAuthStore } from "~/stores/auth";
import SupermarketDropdown from "~/components/ui/SupermarketDropdown.vue";
import ProfileDropdown from "~/components/ui/ProfileDropdown.vue";

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
  authStore.initAuth();
});
</script>
