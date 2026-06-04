<template>
  <div class="relative left-[calc(50%-50vw)] -mt-5 w-screen bg-white">
    <div class="mx-auto max-w-7xl px-4 py-6">
      <h1 class="text-3xl font-bold text-gray-800">{{ supermarket.name }}</h1>
      <div class="mt-4 space-y-2">
        <p class="text-gray-600">
          <span class="font-medium">Address:</span> {{ supermarket.address }}
        </p>
        <p v-if="supermarket.phone" class="text-gray-600">
          <span class="font-medium">Phone:</span> {{ supermarket.phone }}
        </p>
        <p
          v-if="supermarket.operatingHoursFrom && supermarket.operatingHoursTo"
          class="text-gray-600"
        >
          <span class="font-medium">Operating Hours:</span>
          {{ supermarket.operatingHoursFrom }} - {{ supermarket.operatingHoursTo }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  supermarket: {
    type: Object,
    required: true,
  },
})
</script>
