<template>
  <div class="flex flex-col items-center justify-center gap-3 py-12 text-center">
    <SvgIcon name="icon-loading" class="h-8 w-8 animate-spin text-green-700" />
    <p class="text-lg text-gray-600">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: 'Loading...' },
})
</script>
