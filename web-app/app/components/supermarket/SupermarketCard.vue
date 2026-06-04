<template>
  <div class="group relative block h-full">
    <div
      class="absolute top-2 -right-1 -bottom-1 left-2 rounded-lg bg-gradient-to-r from-green-500 to-cyan-500 opacity-45 blur-sm transition duration-1000 group-hover:opacity-60 group-hover:duration-400"
    ></div>
    <NuxtLink
      :to="`/supermarkets/${supermarket.id}`"
      class="relative z-10 block h-full cursor-pointer rounded-lg bg-white p-4 shadow transition-shadow transition-transform duration-300 hover:-translate-y-1"
    >
      <!-- Logo + badge row -->
      <div class="mb-3 flex items-start justify-between">
        <div
          :class="storeBgClass(supermarket.name)"
          class="flex h-12 w-12 items-center justify-center rounded-lg text-xl font-bold text-white select-none"
        >
          {{ supermarket.name?.charAt(0).toUpperCase() }}
        </div>
        <span
          :class="[
            'inline-block rounded-full px-2 py-1 text-xs',
            open ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
          ]"
        >
          {{ open ? 'Open' : 'Closed' }}
        </span>
      </div>

      <h4 class="text-lg font-semibold text-gray-800">{{ supermarket.name }}</h4>
      <p class="mt-2 text-sm text-gray-600">
        <span class="font-medium">Address:</span> {{ supermarket.address }}
      </p>
      <p v-if="supermarket.phone" class="mt-1 text-sm text-gray-600">
        <span class="font-medium">Phone:</span> {{ supermarket.phone }}
      </p>
      <div
        v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo"
        class="mt-1 text-sm text-gray-600"
      >
        <span class="font-medium">Hours:</span>
        {{ formatHour(supermarket.operatingHoursFrom) }} -
        {{ formatHour(supermarket.operatingHoursTo) }}
      </div>
    </NuxtLink>
  </div>
</template>

<script setup>
import { formatHour, isSupermarketOpen } from '~/utils/supermarket'

const props = defineProps({
  supermarket: {
    type: Object,
    required: true,
  },
})

const STORE_COLORS = [
  'bg-gradient-to-br from-emerald-500 to-green-700',
  'bg-gradient-to-br from-teal-500 to-emerald-700',
  'bg-gradient-to-br from-green-500 to-teal-700',
  'bg-gradient-to-br from-cyan-500 to-teal-700',
  'bg-gradient-to-br from-lime-500 to-green-700',
  'bg-gradient-to-br from-emerald-400 to-cyan-600',
]

const storeBgClass = (name) => {
  if (!name) return STORE_COLORS[0]
  return STORE_COLORS[name.charCodeAt(0) % STORE_COLORS.length]
}

const open = computed(() => {
  return isSupermarketOpen(props.supermarket.operatingHoursFrom, props.supermarket.operatingHoursTo)
})
</script>
