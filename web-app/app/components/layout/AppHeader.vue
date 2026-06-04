<template>
  <header
    class="sticky top-0 z-40 bg-white py-6 text-gray-800 transition-shadow duration-200"
    :class="{ 'shadow-lg': headerShadow }"
  >
    <div class="mx-auto flex max-w-7xl items-center justify-between px-4">
      <div class="flex items-center gap-4">
        <NuxtLink to="/" class="text-2xl font-bold text-green-700 transition hover:opacity-90">
          Expiry Rescue
        </NuxtLink>

        <!-- Supermarket Dropdown -->
        <div class="relative">
          <SupermarketDropdown
            v-model="selectedSupermarket"
            :supermarkets="supermarkets"
            :current-supermarket-name="currentSupermarketName"
            @change="emit('supermarket-change')"
          />
        </div>
      </div>

      <nav class="flex gap-3">
        <NuxtLink
          to="/"
          class="flex items-center rounded-[10px] border border-gray-300 px-4 py-1.5 text-gray-700 transition hover:bg-gray-200"
          >Home</NuxtLink
        >
        <!-- <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-gray-300 text-gray-700 hover:bg-gray-200">Products</button>
        <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-gray-300 text-gray-700 hover:bg-gray-200">About</button> -->
        <NuxtLink
          to="/cart"
          class="relative flex items-center rounded-[10px] border border-gray-300 px-4 py-1.5 text-gray-700 transition hover:bg-gray-200"
        >
          Cart
          <span v-if="cartTotal > 0" class="ml-1">({{ cartTotal }})</span>
          <span
            v-if="cartTotal > 0"
            class="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-red-600 text-xs font-bold text-white"
          >
            {{ cartTotal > 99 ? '99+' : cartTotal }}
          </span>
        </NuxtLink>
        <!-- Profile Dropdown -->
        <ProfileDropdown />
      </nav>
    </div>
  </header>
</template>

<script setup>
import ProfileDropdown from '~/components/layout/ProfileDropdown.vue'
import SupermarketDropdown from '~/components/supermarket/SupermarketDropdown.vue'

defineProps({
  supermarkets: {
    type: Array,
    required: true,
  },
  currentSupermarketName: {
    type: String,
    default: '',
  },
  cartTotal: {
    type: Number,
    default: 0,
  },
})

const emit = defineEmits(['supermarket-change'])

// Two-way binding for the dropdown selection
const selectedSupermarket = defineModel('selectedSupermarket', { type: Object, default: null })

// Controlled by pages that want to transfer the shadow (e.g. supermarket details)
const headerShadow = useHeaderShadow()
</script>
