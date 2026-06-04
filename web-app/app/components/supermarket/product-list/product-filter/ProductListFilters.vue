<template>
  <div class="mb-4 flex items-center gap-3">
    <ProductSortDropdown v-model="sortModel" />
  </div>
</template>

<script setup>
import ProductSortDropdown from './ProductSortDropdown.vue'

const sortModel = defineModel('sort', { type: String, required: true })
</script>
