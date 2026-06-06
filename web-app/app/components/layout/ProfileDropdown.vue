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
                <SvgIcon name="icon-login" class="h-5.5 w-5.5 text-green-700" />

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
                <SvgIcon name="icon-clipboard" class="h-6 w-6 text-green-700" />
                My Orders
              </NuxtLink>
            </li>
            <li>
              <button
                class="flex w-full cursor-pointer items-center gap-2 rounded-md px-3 py-2 text-left text-orange-600 transition-colors hover:bg-red-50"
                @click="handleLogout"
              >
                <SvgIcon name="icon-sly-face" class="h-6 w-6 text-orange-600" />
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
