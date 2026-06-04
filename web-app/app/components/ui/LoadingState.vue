<template>
  <div class="py-12 text-center">
    <p class="text-lg text-gray-600">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: 'Loading...' },
})
</script>
