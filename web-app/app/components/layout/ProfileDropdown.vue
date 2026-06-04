<template>
  <div ref="dropdownRef" class="relative">
    <button
      class="group flex cursor-pointer items-center justify-center rounded-[10px] border border-gray-300 px-2 py-1.5 text-gray-500 transition hover:bg-gray-200"
      title="Account"
      @click="isOpen = !isOpen"
    >
      <SvgIcon name="icon-user" class="h-6 w-6" />
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
        class="absolute right-0 z-50 mt-2 w-48 overflow-hidden rounded-lg border border-gray-100 bg-white shadow-xl"
      >
        <!-- When not authenticated -->
        <template v-if="!authStore.isAuthenticated">
          <ul class="space-y-1 p-2 text-sm text-gray-700">
            <li>
              <NuxtLink
                to="/login"
                class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2 text-left transition-colors hover:bg-gray-100"
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
            class="border-b border-gray-100 bg-gradient-to-r from-emerald-50 to-cyan-50 px-4 py-3"
          >
            <p class="text-sm font-semibold text-gray-900">
              {{ authStore.user?.fullName || authStore.user?.email }}
            </p>
            <p class="truncate text-xs text-gray-600">{{ authStore.user?.email }}</p>
          </div>

          <ul class="space-y-1 p-2 text-sm text-gray-700">
            <li>
              <!-- My Orders Link -->
              <NuxtLink
                to="/orders"
                class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2 text-left transition-colors hover:bg-gray-100"
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
                class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2 text-left text-red-600 transition-colors hover:bg-red-50"
                @click="handleLogout"
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useClickOutside } from '~/composables/useClickOutside'
import { useAuthStore } from '~/stores/auth'

const isOpen = ref(false)
const dropdownRef = ref(null)
const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.logout()
  isOpen.value = false
  router.push('/')
}

useClickOutside(dropdownRef, () => {
  isOpen.value = false
})
</script>
