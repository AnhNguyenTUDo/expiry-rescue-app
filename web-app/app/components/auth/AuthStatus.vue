<template>
  <div class="space-y-4 text-center">
    <div class="flex justify-center">
      <SvgIcon :name="icon" class="h-16 w-16" :class="iconClass" />
    </div>
    <h2 class="text-xl font-semibold text-gray-900">{{ heading }}</h2>
    <p class="text-sm text-gray-500">{{ message }}</p>

    <slot />
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    required: true,
  },
  iconClass: {
    type: String,
    default: '',
  },
  heading: {
    type: String,
    default: '',
  },
  message: {
    type: String,
    default: '',
  },
})
</script>
