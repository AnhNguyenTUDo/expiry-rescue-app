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
          <!-- <button class="btn btn-outline-light">Products</button>
          <button class="btn btn-outline-light">About</button> -->
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
          <!-- Profile Dropdown -->
          <div class="relative">
            <button 
              @click="toggleProfileDropdown"
              class="p-2 rounded-lg border border-white/40 text-white hover:bg-white/20 transition group cursor-pointer"
              title="Account"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 transition-transform group-hover:scale-110" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </button>

            <!-- Dropdown Menu -->
            <transition
              enter-active-class="transition ease-out duration-200"
              enter-from-class="transform opacity-0 scale-95"
              enter-to-class="transform opacity-100 scale-100"
              leave-active-class="transition ease-in duration-150"
              leave-from-class="transform opacity-100 scale-100"
              leave-to-class="transform opacity-0 scale-95"
            >
              <div
                v-if="isProfileDropdownOpen"
                class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-200 overflow-hidden z-50"
              >
                <!-- When not authenticated -->
                <template v-if="!authStore.isAuthenticated">
                  <NuxtLink
                    to="/login"
                    class="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-emerald-50 transition-colors duration-200"
                    @click="toggleProfileDropdown"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                    </svg>
                    Login
                  </NuxtLink>
                </template>

                <!-- When authenticated -->
                <template v-else>
                  <!-- User Info -->
                  <div class="px-4 py-3 border-b border-gray-100 bg-gradient-to-r from-emerald-50 to-cyan-50">
                    <p class="text-sm font-semibold text-gray-900">{{ authStore.user?.fullName || authStore.user?.email }}</p>
                    <p class="text-xs text-gray-600 truncate">{{ authStore.user?.email }}</p>
                  </div>


                  <!-- My Orders Link -->
                  <NuxtLink
                    to="/orders"
                    class="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-emerald-50 transition-colors duration-200"
                    @click="toggleProfileDropdown"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    My Orders
                  </NuxtLink>


                  <button
                    @click="handleLogout"
                    class="w-full flex items-center gap-3 px-4 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors duration-200 cursor-pointer"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                    </svg>
                    Logout
                  </button>
                </template>
              </div>
            </transition>
          </div>
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
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useSupermarketStore } from "~/stores/supermarket";
import { useCartStore } from "~/stores/cart";
import { useAuthStore } from "~/stores/auth";

const router = useRouter();
const route = useRoute();

// Stores
const supermarketStore = useSupermarketStore();
const cartStore = useCartStore();
const authStore = useAuthStore();

// Local state for dropdown
const selectedSupermarket = ref("");
const isProfileDropdownOpen = ref(false);

// Toggle profile dropdown
const toggleProfileDropdown = () => {
  isProfileDropdownOpen.value = !isProfileDropdownOpen.value;
};

// Handle logout
const handleLogout = () => {
  authStore.logout();
  isProfileDropdownOpen.value = false;
  router.push('/');
};

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  const dropdown = event.target.closest('.relative');
  if (!dropdown && isProfileDropdownOpen.value) {
    isProfileDropdownOpen.value = false;
  }
};

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
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
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
