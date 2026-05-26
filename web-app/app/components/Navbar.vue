<template>
  <nav class="bg-white shadow-md border-b border-gray-200 sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo and Brand -->
        <div class="flex items-center gap-3">
          <NuxtLink to="/" class="flex items-center gap-2 group">
            <div
              class="w-10 h-10 bg-gradient-to-br from-emerald-500 to-cyan-500 rounded-lg flex items-center justify-center shadow-lg shadow-emerald-500/30 transition-transform duration-300 group-hover:scale-105"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
                />
              </svg>
            </div>
            <span
              class="text-xl font-bold bg-gradient-to-r from-emerald-600 to-cyan-600 bg-clip-text text-transparent"
            >
              Expiry Rescue
            </span>
          </NuxtLink>
        </div>

        <!-- Navigation Links -->
        <div class="hidden md:flex items-center gap-6">
          <NuxtLink
            to="/"
            class="text-gray-700 hover:text-emerald-600 font-medium transition-colors duration-200"
            active-class="text-emerald-600"
          >
            Homeaaa
          </NuxtLink>
          <NuxtLink
            to="/products"
            class="text-gray-700 hover:text-emerald-600 font-medium transition-colors duration-200"
            active-class="text-emerald-600"
          >
            Products
          </NuxtLink>
          <NuxtLink
            to="/supermarkets"
            class="text-gray-700 hover:text-emerald-600 font-medium transition-colors duration-200"
            active-class="text-emerald-600"
          >
            Supermarkets
          </NuxtLink>
          <NuxtLink
            to="/cart"
            class="relative text-gray-700 hover:text-emerald-600 transition-colors duration-200"
            active-class="text-emerald-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
          </NuxtLink>
        </div>

        <!-- User Profile / Login -->
        <div class="flex items-center gap-4">
          <div v-if="authStore.isAuthenticated" class="relative">
            <!-- Profile Dropdown -->
            <button
              @click="toggleDropdown"
              class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors duration-200"
            >
              <img
                v-if="authStore.user?.profilePictureUrl"
                :src="authStore.user.profilePictureUrl"
                alt="Profile"
                class="w-8 h-8 rounded-full border-2 border-emerald-400"
              />
              <div
                v-else
                class="w-8 h-8 rounded-full bg-gradient-to-br from-emerald-400 to-cyan-400 flex items-center justify-center text-white font-semibold"
              >
                {{ getUserInitial }}
              </div>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-600"
                :class="{ 'rotate-180': isDropdownOpen }"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 9l-7 7-7-7"
                />
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
                v-if="isDropdownOpen"
                class="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-gray-200 overflow-hidden"
              >
                <!-- User Info -->
                <div
                  class="px-4 py-3 border-b border-gray-100 bg-gradient-to-r from-emerald-50 to-cyan-50"
                >
                  <p class="text-sm font-semibold text-gray-900">
                    {{ authStore.user?.fullName || authStore.user?.email }}
                  </p>
                  <p class="text-xs text-gray-600 truncate">{{ authStore.user?.email }}</p>
                </div>

                <!-- Menu Items -->
                <!-- <div class="py-2">
                  <NuxtLink
                    to="/profile"
                    class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 transition-colors duration-200"
                    @click="toggleDropdown"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    Profile
                  </NuxtLink>
                  <NuxtLink
                    to="/orders"
                    class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 transition-colors duration-200"
                    @click="toggleDropdown"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    My Orders
                  </NuxtLink>
                  <NuxtLink
                    to="/inventory"
                    class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 transition-colors duration-200"
                    @click="toggleDropdown"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                    </svg>
                    My Inventory
                  </NuxtLink>
                  <button
                    @click="handleLogout"
                    class="w-full flex items-center gap-3 px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors duration-200"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                    </svg>
                    Logout
                  </button>
                </div> -->
              </div>
            </transition>
          </div>

          <!-- Login Button (if not authenticated) -->
          <NuxtLink
            v-else
            to="/login"
            class="px-4 py-2 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-white font-medium rounded-lg shadow-lg shadow-emerald-500/30 transition-all duration-300 hover:-translate-y-0.5 cursor-pointer"
          >
            Sign In
          </NuxtLink>

          <!-- Mobile Menu Button -->
          <button
            @click="toggleMobileMenu"
            class="md:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6 text-gray-700"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"
              />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile Menu -->
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="transform opacity-0 -translate-y-2"
      enter-to-class="transform opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="transform opacity-100 translate-y-0"
      leave-to-class="transform opacity-0 -translate-y-2"
    >
      <div v-if="isMobileMenuOpen" class="md:hidden border-t border-gray-200 bg-white">
        <div class="px-4 py-3 space-y-2">
          <NuxtLink
            to="/"
            class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition-colors duration-200"
            @click="toggleMobileMenu"
          >
            Home
          </NuxtLink>
          <NuxtLink
            to="/products"
            class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition-colors duration-200"
            @click="toggleMobileMenu"
          >
            Products
          </NuxtLink>
          <NuxtLink
            to="/supermarkets"
            class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition-colors duration-200"
            @click="toggleMobileMenu"
          >
            Supermarkets
          </NuxtLink>
          <NuxtLink
            to="/cart"
            class="block px-3 py-2 rounded-lg text-gray-700 hover:bg-emerald-50 hover:text-emerald-600 transition-colors duration-200"
            @click="toggleMobileMenu"
          >
            Cart
          </NuxtLink>
        </div>
      </div>
    </transition>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useAuthStore } from "~/stores/auth";

const authStore = useAuthStore();

const isDropdownOpen = ref(false);
const isMobileMenuOpen = ref(false);

const getUserInitial = computed(() => {
  if (authStore.user?.fullName) {
    return authStore.user.fullName.charAt(0).toUpperCase();
  }
  if (authStore.user?.email) {
    return authStore.user.email.charAt(0).toUpperCase();
  }
  return "U";
});

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const handleLogout = () => {
  authStore.logout();
  isDropdownOpen.value = false;
  navigateTo("/login");
};

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  const dropdown = event.target.closest("button");
  if (!dropdown && isDropdownOpen.value) {
    isDropdownOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
