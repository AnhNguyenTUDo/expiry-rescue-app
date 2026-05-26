<template>
  <div class="relative" ref="dropdownRef">
    <button
      @click="isOpen = !isOpen"
      class="px-2 py-1.5 rounded-[10px] border border-gray-300 text-gray-700 hover:bg-gray-200 transition group cursor-pointer flex items-center justify-center"
      title="Account"
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
          stroke-width="1.5"
          d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
        />
      </svg>
    </button>

    <transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-show="isOpen"
        class="absolute right-0 z-50 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-100 overflow-hidden"
      >
        <!-- When not authenticated -->
        <template v-if="!authStore.isAuthenticated">
          <ul class="p-2 text-sm text-gray-700 space-y-1">
            <li>
              <NuxtLink
                to="/login"
                class="w-full text-left px-3 py-2 rounded-md hover:bg-gray-100 cursor-pointer flex items-center gap-2 transition-colors"
                @click="isOpen = false"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-emerald-600"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"
                  />
                </svg>
                Login
              </NuxtLink>
            </li>
          </ul>
        </template>

        <!-- When authenticated -->
        <template v-else>
          <!-- User Info -->
          <div
            class="px-4 py-3 border-b border-gray-100 bg-gradient-to-r from-emerald-50 to-cyan-50"
          >
            <p class="text-sm font-semibold text-gray-900">
              {{ authStore.user?.fullName || authStore.user?.email }}
            </p>
            <p class="text-xs text-gray-600 truncate">{{ authStore.user?.email }}</p>
          </div>

          <ul class="p-2 text-sm text-gray-700 space-y-1">
            <li>
              <!-- My Orders Link -->
              <NuxtLink
                to="/orders"
                class="w-full text-left px-3 py-2 rounded-md hover:bg-gray-100 cursor-pointer flex items-center gap-2 transition-colors"
                @click="isOpen = false"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-emerald-600"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                  />
                </svg>
                My Orders
              </NuxtLink>
            </li>
            <li>
              <button
                @click="handleLogout"
                class="w-full text-left px-3 py-2 rounded-md hover:bg-red-50 text-red-600 cursor-pointer flex items-center gap-2 transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                  />
                </svg>
                Logout
              </button>
            </li>
          </ul>
        </template>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "~/stores/auth";

const isOpen = ref(false);
const dropdownRef = ref(null);
const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  isOpen.value = false;
  router.push("/");
};

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
