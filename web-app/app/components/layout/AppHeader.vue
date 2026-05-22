<template>
  <header class="bg-gradient-to-r from-green-700 to-green-500 text-white py-6 shadow">
    <div class="max-w-7xl mx-auto flex justify-between items-center px-4">
      <div class="flex items-center gap-4">
        <NuxtLink to="/" class="text-2xl font-bold hover:opacity-90 transition">
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
        <NuxtLink to="/" class="px-4 py-1.5 flex items-center rounded-[10px] border transition border-white/40 text-white hover:bg-white/20">Home</NuxtLink>
        <!-- <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">Products</button>
        <button class="px-4 py-1.5 flex items-center rounded-lg border transition border-white/40 text-white hover:bg-white/20">About</button> -->
        <NuxtLink to="/cart" class="px-4 py-1.5 flex items-center rounded-[10px] border transition border-white/40 text-white hover:bg-white/20 relative">
          Cart
          <span v-if="cartTotal > 0" class="ml-1">({{ cartTotal }})</span>
          <span
            v-if="cartTotal > 0"
            class="absolute -top-1 -right-1 bg-red-600 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center"
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
import SupermarketDropdown from "~/components/ui/SupermarketDropdown.vue";
import ProfileDropdown from "~/components/ui/ProfileDropdown.vue";

defineProps({
  supermarkets: {
    type: Array,
    required: true,
  },
  currentSupermarketName: {
    type: String,
    default: "",
  },
  cartTotal: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(["supermarket-change"]);

// Two-way binding for the dropdown selection
const selectedSupermarket = defineModel("selectedSupermarket");
</script>
