<template>
  <div class="relative group block h-full">
    <div
      class="absolute top-2 left-2 -right-1 -bottom-1 rounded-lg bg-gradient-to-r from-green-500 to-cyan-500 blur-sm opacity-45 group-hover:opacity-60 transition duration-1000 group-hover:duration-400"
    ></div>
    <NuxtLink
      :to="`/supermarkets/${supermarket.id}`"
      class="relative bg-white p-4 rounded-lg shadow transition-shadow cursor-pointer block h-full z-10 transition-transform duration-300 hover:-translate-y-1"
    >
      <!-- Logo + badge row -->
      <div class="flex items-start justify-between mb-3">
        <div
          :class="storeBgClass(supermarket.name)"
          class="w-12 h-12 rounded-lg flex items-center justify-center text-white text-xl font-bold select-none"
        >
          {{ supermarket.name?.charAt(0).toUpperCase() }}
        </div>
        <span
          :class="[
            'inline-block px-2 py-1 text-xs rounded-full',
            open ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800',
          ]"
        >
          {{ open ? 'Open' : 'Closed' }}
        </span>
      </div>

      <h4 class="text-lg font-semibold text-gray-800">{{ supermarket.name }}</h4>
      <p class="text-sm text-gray-600 mt-2">
        <span class="font-medium">Address:</span> {{ supermarket.address }}
      </p>
      <p v-if="supermarket.phone" class="text-sm text-gray-600 mt-1">
        <span class="font-medium">Phone:</span> {{ supermarket.phone }}
      </p>
      <div
        v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo"
        class="text-sm text-gray-600 mt-1"
      >
        <span class="font-medium">Hours:</span>
        {{ formatHour(supermarket.operatingHoursFrom) }} -
        {{ formatHour(supermarket.operatingHoursTo) }}
      </div>
    </NuxtLink>
  </div>
</template>

<script setup>
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

const formatHour = (time) => {
  if (!time) return ''
  const [h, m] = time.split(':')
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
}

const open = computed(() => {
  const { operatingHoursFrom: from, operatingHoursTo: to } = props.supermarket
  if (!from || !to) return false
  const now = new Date()
  const cur = now.getHours() * 60 + now.getMinutes()
  const [fh, fm] = from.split(':').map(Number)
  const [th, tm] = to.split(':').map(Number)
  const start = fh * 60 + fm
  const end = th * 60 + tm
  return start <= end ? cur >= start && cur < end : cur >= start || cur < end
})
</script>
