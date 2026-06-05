<template>
  <div class="w-full max-w-md">
    <div class="rounded-2xl border border-gray-100 bg-white p-8 shadow-lg">
      <!-- Title -->
      <div v-if="title || subtitle" class="mb-8 text-center">
        <h1 v-if="title" class="mb-2 text-2xl font-bold text-gray-900">{{ title }}</h1>
        <p v-if="subtitle" class="text-sm text-gray-500">{{ subtitle }}</p>
      </div>

      <!-- Card content -->
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: '',
  },
  subtitle: {
    type: String,
    default: '',
  },
})
</script>
